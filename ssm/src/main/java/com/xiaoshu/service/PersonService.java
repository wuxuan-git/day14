package com.xiaoshu.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xiaoshu.dao.CompanyMapper;
import com.xiaoshu.dao.PersonMapper;
import com.xiaoshu.entity.Company;
import com.xiaoshu.entity.Person;
import com.xiaoshu.entity.PersonVo;

@Service
public class PersonService {
	@Autowired
	private PersonMapper mapper;
	@Autowired
	private CompanyMapper cmapper;
	public PageInfo<PersonVo> findPage(PersonVo personVo, Integer pageNum, Integer pageSize) {
	PageHelper.startPage(pageNum, pageSize);
	List<PersonVo> list = mapper.findPage(personVo);
	PageInfo<PersonVo> page = new PageInfo<>(list);
		return page;
	}

	public List<Company> findCompany() {
		
		return cmapper.selectAll();
	}

	public void deletePerson(int parseInt) {
		mapper.deleteByPrimaryKey(parseInt);
		
		
	}

	public Person findByName(String expressName) {
		Person person = new Person();
		person.setExpressName(expressName);
		Person p = mapper.selectOne(person);
		return p;
	}

	public void updatePerson(Person person) {
		mapper.updateByPrimaryKeySelective(person);		
	}

	public void addPerson(Person person) {
		person.setCreateTime(new Date());
		mapper.insert(person);
		
	}

	public List<PersonVo> findAll(PersonVo personVo) {
		
		return mapper.findAll(personVo);
	}

	public Company findCompanyByName(String cname) {
		Company com = new Company();
		com.setExpressName(cname);
		
		return cmapper.selectOne(com);
	}

	public List<PersonVo> findCount() {
		List<PersonVo> list =mapper.findCount();
		return list;
	}

}
