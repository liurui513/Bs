package com.project.certificate.entity;


import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
public class CertificateRevocationList {

    @Id
    @GeneratedValue
    private Long id;  // 主键ID

    private String issuer;        // CRL颁发者(通常与证书颁发者相同)
    private Date thisUpdate;      // 本次CRL生成时间
    private Date nextUpdate;      // 下次CRL更新时间
    private String crlNumber;     // CRL序列号(唯一标识)

    @OneToMany(mappedBy = "crl", cascade = CascadeType.ALL)  // 一对多关系，级联所有操作
    private List<RevokedCertificateEntry> revokedCertificates = new ArrayList<>();  // 被吊销证书条目列表

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public Date getThisUpdate() {
        return thisUpdate;
    }

    public void setThisUpdate(Date thisUpdate) {
        this.thisUpdate = thisUpdate;
    }

    public Date getNextUpdate() {
        return nextUpdate;
    }

    public void setNextUpdate(Date nextUpdate) {
        this.nextUpdate = nextUpdate;
    }

    public String getCrlNumber() {
        return crlNumber;
    }

    public void setCrlNumber(String crlNumber) {
        this.crlNumber = crlNumber;
    }

    public List<RevokedCertificateEntry> getRevokedCertificates() {
        return revokedCertificates;
    }

    public void setRevokedCertificates(List<RevokedCertificateEntry> revokedCertificates) {
        this.revokedCertificates = revokedCertificates;
    }
}
