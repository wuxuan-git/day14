package com.xiaoshu.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

import org.springframework.format.annotation.DateTimeFormat;

@Table(name = "jiyun_student")
public class Student implements Serializable {
    @Id
    private Integer id;

    @Column(name = "course_id")
    private Integer courseId;

    @Column(name = "s_name")
    private String sName;

    private Integer age;

    @Column(name = "s_code")
    private Integer sCode;

    private String grade;

    @Column(name = "s_entry_time")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private Date sEntryTime;
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private Date createtime;

    private static final long serialVersionUID = 1L;

    /**
     * @return id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return course_id
     */
    public Integer getCourseId() {
        return courseId;
    }

    /**
     * @param courseId
     */
    public void setCourseId(Integer courseId) {
        this.courseId = courseId;
    }

    /**
     * @return s_name
     */
    public String getsName() {
        return sName;
    }

    /**
     * @param sName
     */
    public void setsName(String sName) {
        this.sName = sName == null ? null : sName.trim();
    }

    /**
     * @return age
     */
    public Integer getAge() {
        return age;
    }

    /**
     * @param age
     */
    public void setAge(Integer age) {
        this.age = age;
    }

    /**
     * @return s_code
     */
    public Integer getsCode() {
        return sCode;
    }

    /**
     * @param sCode
     */
    public void setsCode(Integer sCode) {
        this.sCode = sCode;
    }

    /**
     * @return grade
     */
    public String getGrade() {
        return grade;
    }

    /**
     * @param grade
     */
    public void setGrade(String grade) {
        this.grade = grade == null ? null : grade.trim();
    }

    /**
     * @return s_entry_time
     */
    public Date getsEntryTime() {
        return sEntryTime;
    }

    /**
     * @param sEntryTime
     */
    public void setsEntryTime(Date sEntryTime) {
        this.sEntryTime = sEntryTime;
    }

    /**
     * @return createtime
     */
    public Date getCreatetime() {
        return createtime;
    }

    /**
     * @param createtime
     */
    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", courseId=").append(courseId);
        sb.append(", sName=").append(sName);
        sb.append(", age=").append(age);
        sb.append(", sCode=").append(sCode);
        sb.append(", grade=").append(grade);
        sb.append(", sEntryTime=").append(sEntryTime);
        sb.append(", createtime=").append(createtime);
        sb.append("]");
        return sb.toString();
    }
}