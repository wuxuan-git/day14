package com.xiaoshu.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import com.xiaoshu.config.util.ConfigUtil;
import com.xiaoshu.entity.Company;
import com.xiaoshu.entity.Log;
import com.xiaoshu.entity.Operation;
import com.xiaoshu.entity.Person;
import com.xiaoshu.entity.PersonVo;
import com.xiaoshu.entity.Role;
import com.xiaoshu.entity.User;
import com.xiaoshu.service.OperationService;
import com.xiaoshu.service.PersonService;
import com.xiaoshu.service.RoleService;
import com.xiaoshu.service.UserService;
import com.xiaoshu.util.StringUtil;
import com.xiaoshu.util.TimeUtil;
import com.xiaoshu.util.WriterUtil;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;

@Controller
@RequestMapping("person")
public class PersonController extends LogController {
	static Logger logger = Logger.getLogger(PersonController.class);

	@Autowired
	private UserService userService;

	@Autowired
	private RoleService roleService;

	@Autowired
	private OperationService operationService;
	@Autowired
	private PersonService service;

	@RequestMapping("personIndex")
	public String index(HttpServletRequest request, Integer menuid) throws Exception {
		// List<Role> roleList = roleService.findRole(new Role());
		List<Operation> operationList = operationService.findOperationIdsByMenuid(menuid);
		request.setAttribute("operationList", operationList);

		List<Company> clist = service.findCompany();
		request.setAttribute("clist", clist);
		return "person";
	}

	@RequestMapping(value = "personList", method = RequestMethod.POST)
	public void userList(PersonVo personVo, HttpServletRequest request, HttpServletResponse response, String offset,
			String limit) throws Exception {
		try {

			Integer pageSize = StringUtil.isEmpty(limit) ? ConfigUtil.getPageSize() : Integer.parseInt(limit);
			Integer pageNum = (Integer.parseInt(offset) / pageSize) + 1;
			PageInfo<PersonVo> personList = service.findPage(personVo, pageNum, pageSize);

			JSONObject jsonObj = new JSONObject();
			jsonObj.put("total", personList.getTotal());
			jsonObj.put("rows", personList.getList());
			WriterUtil.write(response, jsonObj.toString());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("用户展示错误", e);
			throw e;
		}
	}

	// 新增或修改
	@RequestMapping("reserveUser")
	public void reserveUser(Person person, HttpServletRequest request, User user, HttpServletResponse response) {
		Integer id = person.getId();
		JSONObject result = new JSONObject();
		try {

			// 添加修改去重
			Person person2 = service.findByName(person.getExpressName());
			if (id != null) { // userId不为空 说明是修改

				if (person2 == null || (person2 != null && person2.getId().equals(person.getId()))) {

					service.updatePerson(person);
					result.put("success", true);
				} else {
					result.put("success", true);
					result.put("errorMsg", "该员工名被使用");
				}

			} else { // 添加
				if (person2 == null) { // 没有重复可以添加
					service.addPerson(person);
					result.put("success", true);
				} else {
					result.put("success", true);
					result.put("errorMsg", "该用户名被使用");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("保存用户信息错误", e);
			result.put("success", true);
			result.put("errorMsg", "对不起，操作失败");
		}
		WriterUtil.write(response, result.toString());
	}

	@RequestMapping("deleteUser")
	public void delUser(HttpServletRequest request, HttpServletResponse response) {
		JSONObject result = new JSONObject();
		try {
			String[] ids = request.getParameter("ids").split(",");
			for (String id : ids) {
				// userService.deleteUser(Integer.parseInt(id));
				service.deletePerson(Integer.parseInt(id));
			}
			result.put("success", true);
			result.put("delNums", ids.length);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("删除用户信息错误", e);
			result.put("errorMsg", "对不起，删除失败");
		}
		WriterUtil.write(response, result.toString());
	}
	
	//统计后台
	@RequestMapping("countPerson")
	public void countPerson(HttpServletRequest request, HttpServletResponse response) {
		JSONObject result = new JSONObject();
		try {
			List<PersonVo> list = service.findCount();
			//System.out.println(list.toString());
			result.put("success", true);
			result.put("data", list);
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("删除用户信息错误", e);
			result.put("errorMsg", "对不起，删除失败");
		}
		WriterUtil.write(response, result.toString());
	}

	// 导入后台
	@RequestMapping("importPerson")
	public void importPerson(MultipartFile personFile, HttpServletRequest request, HttpServletResponse response) {
		JSONObject result = new JSONObject();
		try {
			// 导入业务
			// 先获取工作簿对象 03 07 解决冲突
			Workbook workBook = WorkbookFactory.create(personFile.getInputStream());
			// 工作表对象
			Sheet sheet = workBook.getSheetAt(0);
			// 获取共有多少行
			int RowNum = sheet.getLastRowNum();
			for (int i = 0; i < RowNum; i++) {
				// 第一行表头
				Row row = sheet.getRow(i + 1);
				String name = row.getCell(0).toString();
				String sex = row.getCell(1).toString();
				String trait = row.getCell(2).toString();
				Date entryTime = row.getCell(3).getDateCellValue();
				String cname = row.getCell(4).toString();
				// 判断条件 条件符合的存到实体类中 在存到数据库中
				if (sex.equals("男") && cname.equals("圆通")) {
					// 把数据封装到实体类中
					Person person = new Person();
					person.setExpressName(name);
					person.setSex(sex);
					person.setExpressTrait(trait);
					person.setEntryTime(entryTime);
					person.setCreateTime(new Date());
					// 公司id
					Company com = service.findCompanyByName(cname);
					person.setExpressTypeId(com.getId());
					// 保存到数据库
					service.addPerson(person);
				}

			}
			result.put("success", true);

		} catch (Exception e) {
			e.printStackTrace();
			logger.error("导入信息错误", e);
			result.put("errorMsg", "对不起，导入失败");
		}
		WriterUtil.write(response, result.toString());
	}

	// 导出
	@RequestMapping("exportPerson")
	public void exportPerson(PersonVo personVo, HttpServletRequest request, HttpServletResponse response) {
		JSONObject result = new JSONObject();
		try {
			String time = TimeUtil.formatTime(new Date(), "yyyyMMddHHmmss");
			String excelName = "手动备份" + time;
			List<PersonVo> list = service.findAll(personVo);

			String[] handers = { "用户编号", "人员名字", "人员性别", "人员特点", "入职时间", "所属公司", "创建时间" };

			// 1导入硬盘
			ExportExcelToDisk(request, handers, list, excelName);
			result.put("success", true);

		} catch (Exception e) {
			e.printStackTrace();
			logger.error("删除用户信息错误", e);
			result.put("errorMsg", "对不起，删除失败");
		}
		WriterUtil.write(response, result.toString());
	}

	// 导出到硬盘
	@SuppressWarnings("resource")
	private void ExportExcelToDisk(HttpServletRequest request, String[] handers, List<PersonVo> list, String excleName)
			throws Exception {

		try {
			HSSFWorkbook wb = new HSSFWorkbook();// 创建工作簿
			HSSFSheet sheet = wb.createSheet("操作记录备份");// 第一个sheet
			HSSFRow rowFirst = sheet.createRow(0);// 第一个sheet第一行为标题
			rowFirst.setHeight((short) 500);
			for (int i = 0; i < handers.length; i++) {
				sheet.setColumnWidth((short) i, (short) 4000);// 设置列宽
			}
			// 写标题了
			for (int i = 0; i < handers.length; i++) {
				// 获取第一行的每一个单元格
				HSSFCell cell = rowFirst.createCell(i);
				// 往单元格里面写入值
				cell.setCellValue(handers[i]);
			}
			for (int i = 0; i < list.size(); i++) {
				// 获取list里面存在是数据集对象
				PersonVo personVo = list.get(i);
				// 创建数据行
				HSSFRow row = sheet.createRow(i + 1);
				// 设置对应单元格的值
				row.setHeight((short) 400); // 设置每行的高度
				// "序号","操作人","IP地址","操作时间","操作模块","操作类型","详情"
				row.createCell(0).setCellValue(personVo.getId());
				row.createCell(1).setCellValue(personVo.getExpressName());
				row.createCell(2).setCellValue(personVo.getSex());
				row.createCell(3).setCellValue(personVo.getExpressTrait());
				row.createCell(4).setCellValue(TimeUtil.formatTime(personVo.getEntryTime(), "yyyy-MM-dd"));
				row.createCell(5).setCellValue(personVo.getEname());
				row.createCell(6).setCellValue(TimeUtil.formatTime(personVo.getCreateTime(), "yyyy-MM-dd"));
			}
			// 写出文件（path为文件路径含文件名）
			OutputStream os;
			File file = new File("D:/" + excleName + ".xls");

			if (!file.exists()) {// 若此目录不存在，则创建之
				file.createNewFile();
				logger.debug("创建文件夹路径为：" + file.getPath());
			}
			os = new FileOutputStream(file);
			wb.write(os);
			os.close();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	@RequestMapping("editPassword")
	public void editPassword(HttpServletRequest request, HttpServletResponse response) {
		JSONObject result = new JSONObject();
		String oldpassword = request.getParameter("oldpassword");
		String newpassword = request.getParameter("newpassword");
		HttpSession session = request.getSession();
		User currentUser = (User) session.getAttribute("currentUser");
		if (currentUser.getPassword().equals(oldpassword)) {
			User user = new User();
			user.setUserid(currentUser.getUserid());
			user.setPassword(newpassword);
			try {
				userService.updateUser(user);
				currentUser.setPassword(newpassword);
				session.removeAttribute("currentUser");
				session.setAttribute("currentUser", currentUser);
				result.put("success", true);
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("修改密码错误", e);
				result.put("errorMsg", "对不起，修改密码失败");
			}
		} else {
			logger.error(currentUser.getUsername() + "修改密码时原密码输入错误！");
			result.put("errorMsg", "对不起，原密码输入错误！");
		}
		WriterUtil.write(response, result.toString());
	}
}
