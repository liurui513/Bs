package com.project.certificate.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.springframework.data.annotation.CreatedBy;

import java.math.BigInteger;
import java.security.PublicKey;

//电子证书
@Entity
public class Invoice {
	private Integer id;
	private String number;   //证书号码
	private String code;    //证书代码
	private String serial;   //唯一序列号
	private Lessee lessee;    //公司或组织
	private String name;    //申请者
	private String date;      //申请时间
	private String revocationReason;    //吊销原因
	private String endDate;   //吊销时间
	private String state;   //状态，是否吊销了
	private Integer zstype;		//证书申请=0，使用=1，,吊销申请=2，已吊销=3

	//private String issuer;  // 颁发机构名称（如：XX公司CA）
	private String publicKey;
	@Id
	@GeneratedValue
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	@ManyToOne
	public Lessee getLessee() {
		return lessee;
	}
	public void setLessee(Lessee lessee) {
		this.lessee = lessee;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}

	public String getRevocationReason() {
		return revocationReason;
	}

	public void setRevocationReason(String revocationReason) {
		this.revocationReason = revocationReason;
	}

	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}

	public String getSerial() {
		return serial;
	}

	public void setSerial(String serial) {
		this.serial = serial;
	}

	public Integer getZstype() {
		return zstype;
	}
	public void setZstype(Integer zstype) {
		this.zstype = zstype;
	}


	public String getPublicKey() {
		return publicKey;
	}

	public void setPublicKey(String publicKey) {
		this.publicKey = publicKey;
	}

	/*public String getIssuer() {
		return issuer;
	}

	public void setIssuer(String issuer) {
		this.issuer = issuer;
	}*/
}
