package com.xiaoshu.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xiaoshu.dao.CourseMapper;
import com.xiaoshu.dao.StudentMapper;
import com.xiaoshu.entity.Course;
import com.xiaoshu.entity.Student;
import com.xiaoshu.entity.StudentVo;

@Service
public class StudentService {
	@Autowired
	private StudentMapper smapper;
	@Autowired
	private CourseMapper cmapper;

	public PageInfo<StudentVo> findPage(StudentVo studentVo, Integer pageNum, Integer pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		List<StudentVo> list =smapper.findPage(studentVo);
		PageInfo<StudentVo> page = new PageInfo<>(list);
		return page;
	}

	public List<Course> findCourse() {
		
		return cmapper.selectAll();
	}

	public Student findByName(String getsName) {
		Student stu = new Student();
		stu.setsName(getsName);
		return smapper.selectOne(stu);
	}

	public void updateStudent(Student student) {
		smapper.updateByPrimaryKeySelective(student);
		
	}

	public void addStudent(Student student) {
		student.setCreatetime(new Date());
		smapper.insert(student);
		
	}

	public void deleteStudent(int parseInt) {
		
		smapper.deleteByPrimaryKey(parseInt);
		
	}

	public List<StudentVo> findCount() {
		List<StudentVo> list = smapper.findCount();
		return list;
	}

	public Course findByNameCourse(String name) {
		Course c = new Course();
		c.setName(name);
		
		return cmapper.selectOne(c);
	}

	public void addCourse(Course course) {
		cmapper.insert(course);
		
	}

}
