package com.project.certificate.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

//授权信息
@Entity
public class Accredit {
	private Integer id;
	private String beginDate;   //开始时间
	private String overDate;    //截止时间时间
	private Integer month;   //授权期限，月
	private float cost;    //缴纳费用
	private Lessee lessee;   //公司或组织名称
	
	@Id
	@GeneratedValue
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getBeginDate() {
		return beginDate;
	}
	public void setBeginDate(String beginDate) {
		this.beginDate = beginDate;
	}
	public String getOverDate() {
		return overDate;
	}
	public void setOverDate(String overDate) {
		this.overDate = overDate;
	}
	public float getCost() {
		return cost;
	}
	public void setCost(float cost) {
		this.cost = cost;
	}
	public Integer getMonth() {
		return month;
	}
	public void setMonth(Integer month) {
		this.month = month;
	}
	@OneToOne
	public Lessee getLessee() {
		return lessee;
	}
	public void setLessee(Lessee lessee) {
		this.lessee = lessee;
	}
}
