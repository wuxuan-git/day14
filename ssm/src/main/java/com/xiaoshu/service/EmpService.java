package com.xiaoshu.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageInfo;
import com.xiaoshu.dao.DeptMapper;
import com.xiaoshu.dao.EmpMapper;
import com.xiaoshu.entity.Dept;
import com.xiaoshu.entity.Emp;
import com.xiaoshu.entity.EmpVo;

@Service
public class EmpService {
	@Autowired
	private EmpMapper emapper;
	@Autowired
	private DeptMapper dmapper;

	public PageInfo<EmpVo> findPage(EmpVo empVo, Integer pageNum, Integer pageSize) {
		List<EmpVo> elist = emapper.findPage(empVo);
		PageInfo<EmpVo> page = new PageInfo<>(elist);
		return page;
	}

	public List<Dept> findDept() {
	
		return dmapper.selectAll();
	}

	public void deleteEmp(int id) {
		emapper.deleteByPrimaryKey(id);
		
	}

	public Emp findByName(String getpName) {
		Emp emp = new Emp();
		emp.setpName(getpName);
		Emp empp = emapper.selectOne(emp);
		return empp;
	}

	public void updateEmp(Emp emp) {
		emapper.updateByPrimaryKeySelective(emp);
		
	}

	public void addEmp(Emp emp) {
		emapper.insert(emp);
		
	}

	public List<EmpVo> findAll() {
		
		return emapper.findAll();
	}

	public List<EmpVo> findCount() {
		
		return emapper.findCount();
	}

	public Dept getid(String dname) {
		// TODO Auto-generated method stub
		return emapper.getid(dname);
	}

}
