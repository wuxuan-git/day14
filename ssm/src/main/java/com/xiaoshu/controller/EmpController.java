package com.xiaoshu.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
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
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.xiaoshu.config.util.ConfigUtil;
import com.xiaoshu.entity.Dept;
import com.xiaoshu.entity.Emp;
import com.xiaoshu.entity.EmpVo;
import com.xiaoshu.entity.Log;
import com.xiaoshu.entity.Operation;
import com.xiaoshu.entity.Role;
import com.xiaoshu.entity.User;
import com.xiaoshu.service.EmpService;
import com.xiaoshu.service.OperationService;
import com.xiaoshu.service.RoleService;
import com.xiaoshu.service.UserService;
import com.xiaoshu.util.StringUtil;
import com.xiaoshu.util.TimeUtil;
import com.xiaoshu.util.WriterUtil;

@Controller
@RequestMapping("emp")
public class EmpController extends LogController{
	static Logger logger = Logger.getLogger(EmpController.class);

	@Autowired
	private UserService userService;
	
	@Autowired
	private RoleService roleService ;
	
	@Autowired
	private OperationService operationService;
	@Autowired
	private EmpService service;
	
	
	@RequestMapping("empShow")
	public String index(HttpServletRequest request,Integer menuid) throws Exception{
		List<Role> roleList = roleService.findRole(new Role());
		List<Operation> operationList = operationService.findOperationIdsByMenuid(menuid);
		List<Dept> dlist = service.findDept();
		request.setAttribute("operationList", operationList);
		request.setAttribute("roleList", roleList);
		request.setAttribute("dlist", dlist);
		return "empp";
	}
	
	
	@RequestMapping(value="empList",method=RequestMethod.POST)
	public void userList(EmpVo empVo,HttpServletRequest request,HttpServletResponse response,String offset,String limit) throws Exception{
		try {
		
			
			Integer pageSize = StringUtil.isEmpty(limit)?ConfigUtil.getPageSize():Integer.parseInt(limit);
			Integer pageNum =  (Integer.parseInt(offset)/pageSize)+1;
			//PageInfo<User> userList= userService.findUserPage(user,pageNum,pageSize,ordername,order);
			PageInfo<EmpVo> empList = service.findPage(empVo,pageNum,pageSize);
			JSONObject jsonObj = new JSONObject();
			jsonObj.put("total",empList.getTotal() );
			jsonObj.put("rows", empList.getList());
	        WriterUtil.write(response,jsonObj.toString());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("用户展示错误",e);
			throw e;
		}
	}
	
	
	// 新增或修改
	@RequestMapping("reserveUser")
	public void reserveUser(Emp emp,HttpServletRequest request,User user,HttpServletResponse response){
		
		Integer id = emp.getId();
		JSONObject result=new JSONObject();
		try {
			Emp emp2 = service.findByName(emp.getpName());
			if (id != null) {   // userId不为空 说明是修改
				if(emp2==null || (emp2!= null && emp2.getId().equals(id))){
					
					//userService.updateUser(user);
					service.updateEmp(emp);
					result.put("success", true);
				}else{
					result.put("success", true);
					result.put("errorMsg", "该用户名被使用");
				}
				
			}else {   // 添加
				if(emp2==null){  // 没有重复可以添加
					service.addEmp(emp);
					result.put("success", true);
				} else {
					result.put("success", true);
					result.put("errorMsg", "该用户名被使用");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("保存用户信息错误",e);
			result.put("success", true);
			result.put("errorMsg", "对不起，操作失败");
		}
		WriterUtil.write(response, result.toString());
	}
	
	
	@RequestMapping("deleteEmp")
	public void delUser(HttpServletRequest request,HttpServletResponse response){
		JSONObject result=new JSONObject();
		try {
			String[] ids=request.getParameter("ids").split(",");
			for (String id : ids) {
				//userService.deleteUser(Integer.parseInt(id));
				service.deleteEmp(Integer.parseInt(id));
			}
			result.put("success", true);
			result.put("delNums", ids.length);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("删除用户信息错误",e);
			result.put("errorMsg", "对不起，删除失败");
		}
		WriterUtil.write(response, result.toString());
	}
	@RequestMapping("importEmp")
	public void importEmp(MultipartFile empFile,HttpServletRequest request,HttpServletResponse response){
		JSONObject result=new JSONObject();
		try {
			Workbook wb=WorkbookFactory.create(empFile.getInputStream());
			Sheet sheet = wb.getSheetAt(0);
			int lastRowNum = sheet.getLastRowNum();
			for (int i = 1; i <= lastRowNum; i++) {
				String pName = sheet.getRow(i).getCell(0).toString();
				String gender = sheet.getRow(i).getCell(1).toString();
				String hobby = sheet.getRow(i).getCell(2).toString();
				Date createtime = sheet.getRow(i).getCell(3).getDateCellValue();
				String dname = sheet.getRow(i).getCell(4).toString();
				Dept dept=service.getid(dname);
				Integer companyId = dept.getCompanyId();
				
				if(gender.equals("男") && dname.equals("老干部")){
					Emp emp = new Emp();
					emp.setpName(pName);
					emp.setGender(gender);
					emp.setHobby(hobby);
					emp.setCreatetime(createtime);
					emp.setCompanyId(companyId);
					service.addEmp(emp);
				}
			}
		result.put("success", true);
		
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("删除用户信息错误",e);
			result.put("errorMsg", "对不起，删除失败");
		}
		WriterUtil.write(response, result.toString());
	}
	//统计图
	@RequestMapping("echartsEmp")
	public void echartsEmp(HttpServletRequest request,HttpServletResponse response){
		JSONObject result=new JSONObject();
		try {

			List<EmpVo> list = service.findCount();
			result.put("success", true);
			result.put("data", list);
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("删除用户信息错误",e);
			result.put("errorMsg", "对不起，删除失败");
		}
		WriterUtil.write(response, result.toString());
	}
	//导出后台
	@RequestMapping("exportEmp")
	public void exportEmp(HttpServletRequest request,HttpServletResponse response){
		JSONObject result=new JSONObject();
		try {
			String time = TimeUtil.formatTime(new Date(), "yyyyMMddHHmmss");
		    String excelName = "员工信息表"+time;
			Log log = new Log();
			//List<Log> list = logService.findLog(log);
			List<EmpVo> list = service.findAll();
			List<EmpVo> list1 = new ArrayList<>();
			for (int i = 0; i < list.size(); i++) {
				EmpVo empVo = list.get(i);
				String hobby = empVo.getHobby();
				String[] arr = hobby.split(",");
				if(arr.length>=2){
					list1.add(empVo);
				}
				
			}
			String[] handers = {"员工编号","员工姓名","员工性别","员工爱好","员工生日","所属方向"};
			// 1导入硬盘
			ExportExcelToDisk(request,handers,list1, excelName);
			result.put("success", true);
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("删除用户信息错误",e);
			result.put("errorMsg", "对不起，删除失败");
		}
		WriterUtil.write(response, result.toString());
	}
	
	// 导出到硬盘
	@SuppressWarnings("resource")
	private void ExportExcelToDisk(HttpServletRequest request,
			String[] handers, List<EmpVo> list1, String excleName) throws Exception {
		
		try {
			HSSFWorkbook wb = new HSSFWorkbook();//创建工作簿
			HSSFSheet sheet = wb.createSheet("操作记录备份");//第一个sheet
			HSSFRow rowFirst = sheet.createRow(0);//第一个sheet第一行为标题
			rowFirst.setHeight((short) 500);
			for (int i = 0; i < handers.length; i++) {
				sheet.setColumnWidth((short) i, (short) 4000);// 设置列宽
			}
			//写标题了
			for (int i = 0; i < handers.length; i++) {
			    //获取第一行的每一个单元格
			    HSSFCell cell = rowFirst.createCell(i);
			    //往单元格里面写入值
			    cell.setCellValue(handers[i]);
			}
			for (int i = 0;i < list1.size(); i++) {
			    //获取list里面存在是数据集对象
				
			    EmpVo emp = list1.get(i);
			    String hobby = emp.getHobby();
			    
			    //创建数据行
			    
			    HSSFRow row = sheet.createRow(i+1);
			    //设置对应单元格的值
			    row.setHeight((short)400);   // 设置每行的高度
			    
			    //{"员工编号","员工姓名","员工性别","员工爱好","员工生日","所属方向"};
			    row.createCell(0).setCellValue(emp.getId());
			    row.createCell(1).setCellValue(emp.getpName());
			    row.createCell(2).setCellValue(emp.getGender());
			    row.createCell(3).setCellValue(hobby);
			    row.createCell(4).setCellValue(TimeUtil.formatTime(emp.getCreatetime(),"yyyy-MM-dd"));
			    row.createCell(5).setCellValue(emp.getDname());
			    
			}
			//写出文件（path为文件路径含文件名）
				OutputStream os;
				File file = new File("D:/"+excleName+".xls");
				
				if (!file.exists()){//若此目录不存在，则创建之  
					file.createNewFile();  
					logger.debug("创建文件夹路径为："+ file.getPath());  
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
	public void editPassword(HttpServletRequest request,HttpServletResponse response){
		JSONObject result=new JSONObject();
		String oldpassword = request.getParameter("oldpassword");
		String newpassword = request.getParameter("newpassword");
		HttpSession session = request.getSession();
		User currentUser = (User) session.getAttribute("currentUser");
		if(currentUser.getPassword().equals(oldpassword)){
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
				logger.error("修改密码错误",e);
				result.put("errorMsg", "对不起，修改密码失败");
			}
		}else{
			logger.error(currentUser.getUsername()+"修改密码时原密码输入错误！");
			result.put("errorMsg", "对不起，原密码输入错误！");
		}
		WriterUtil.write(response, result.toString());
	}
}
