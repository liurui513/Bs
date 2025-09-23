package com.project.certificate.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.springframework.data.annotation.CreatedBy;

//公司或组织
@Entity
public class Lessee {
	private Integer id;
	private String phone;    //电话
	private String name;   //公司或组织名称
	private String linkman;  //联系人
	private LesseeAdmin lesseeAdmin;
	private Integer number;    //电子证书数量

	private String cn;//常用名称==简写
	private String o;//企业名称==name
	private String c;//国家
	private String st;//省份
	private String l;//城市
	private String ou;	//部门
	@Id
	@GeneratedValue
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	
	@ManyToOne
	public LesseeAdmin getLesseeAdmin() {
		return lesseeAdmin;
	}
	public void setLesseeAdmin(LesseeAdmin lesseeAdmin) {
		this.lesseeAdmin = lesseeAdmin;
	}

	public String getLinkman() {
		return linkman;
	}

	public void setLinkman(String linkman) {
		this.linkman = linkman;
	}

	public Integer getNumber() {
		return number;
	}
	public void setNumber(Integer number) {
		this.number = number;
	}

	public String getCn() {
		return cn;
	}

	public void setCn(String cn) {
		this.cn = cn;
	}

	public String getO() {
		return o;
	}

	public void setO(String o) {
		this.o = o;
	}

	public String getC() {
		return c;
	}

	public void setC(String c) {
		this.c = c;
	}

	public String getSt() {
		return st;
	}

	public void setSt(String st) {
		this.st = st;
	}

	public String getL() {
		return l;
	}

	public void setL(String l) {
		this.l = l;
	}

	public String getOu() {
		return ou;
	}

	public void setOu(String ou) {
		this.ou = ou;
	}

	@Override
	public String toString() {
		return "cn='" + cn + '\'' +
				", o='" + o + '\'' +
				", ou='" + ou + '\'' +
				", l='" + l + '\'' +
				", st='" + st + '\'' +
				", c='" + c + '\'';
	}
}
