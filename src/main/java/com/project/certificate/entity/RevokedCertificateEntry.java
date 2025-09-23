package com.project.certificate.entity;

import javax.persistence.*;
import java.util.Date;

@Entity
public class RevokedCertificateEntry {
    @Id
    @GeneratedValue
     private Integer id;
    private String serialNumber;  // 被吊销证书的序列号
    private Date revocationDate;  // 证书吊销日期
    private String revocationReason; // 吊销原因

    @ManyToOne  // 多对一关系，多个吊销条目对应一个CRL
    @JoinColumn(name = "crl_id")  // 外键列名
    private CertificateRevocationList crl;  // 关联的CRL

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public Date getRevocationDate() {
        return revocationDate;
    }

    public void setRevocationDate(Date revocationDate) {
        this.revocationDate = revocationDate;
    }

    public String getRevocationReason() {
        return revocationReason;
    }

    public void setRevocationReason(String revocationReason) {
        this.revocationReason = revocationReason;
    }

    public CertificateRevocationList getCrl() {
        return crl;
    }

    public void setCrl(CertificateRevocationList crl) {
        this.crl = crl;
    }
}
