package com.xiaoshu.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xiaoshu.dao.MajMapper;
import com.xiaoshu.dao.StuMapper;
import com.xiaoshu.entity.Maj;
import com.xiaoshu.entity.Stu;
import com.xiaoshu.entity.StuVo;

@Service
public class StuService {
	@Autowired
	private StuMapper smapper;
	@Autowired
	private MajMapper mmapper;

	public PageInfo<StuVo> findPage(StuVo stuVo, Integer pageNum, Integer pageSize) {
	PageHelper.startPage(pageNum, pageSize);
	List<StuVo> list = smapper.findPage(stuVo);
	PageInfo<StuVo> page = new PageInfo<>(list);
		return page;
	}

	public List<Maj> findMaj() {
		List<Maj> list = mmapper.selectAll();
		return list;
	}

	public Stu findByName(String getsName) {
		Stu stu = new Stu();
		stu.setsName(getsName);
		Stu s = smapper.selectOne(stu);
		return s;
	}

	public void updateStu(Stu stu) {
		smapper.updateByPrimaryKeySelective(stu);		
	}

	public void addStu(Stu stu) {
		smapper.insert(stu);
		
	}

	public void deleteStu(int parseInt) {
		smapper.deleteByPrimaryKey(parseInt);
		
	}

}
