package com.project.certificate.entity;

import org.apache.commons.lang.StringUtils;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;

import javax.persistence.Entity;

//公司或组织管理员
@Entity
public class LesseeAdmin extends SysUser{
	private Integer id;
	private String ou;	//部门
	private String phone;  //电话

	private String cn;//常用名称
	private String o;//企业名称
	private String c;//国家
	private String st;//省份
	private String l;//城市

	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}

	public String getOu() {
		return ou;
	}

	public void setOu(String ou) {
		this.ou = ou;
	}

	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
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

	public X500Name build(){
		X500NameBuilder x500NameBuilder = new X500NameBuilder();
		if(StringUtils.isNotBlank(cn)){
			x500NameBuilder.addRDN(BCStyle.CN,cn);
		}
		if (StringUtils.isNotBlank(o)) {
			x500NameBuilder.addRDN(BCStyle.O, o);
		}
		if (StringUtils.isNotBlank(ou)) {
			x500NameBuilder.addRDN(BCStyle.OU, ou);
		}
		if (StringUtils.isNotBlank(c)) {
			x500NameBuilder.addRDN(BCStyle.C, c);
		}
		if (StringUtils.isNotBlank(st)) {
			x500NameBuilder.addRDN(BCStyle.ST, st);
		}
		if (StringUtils.isNotBlank(l)) {
			x500NameBuilder.addRDN(BCStyle.L, l);
		}
		return x500NameBuilder.build();
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
