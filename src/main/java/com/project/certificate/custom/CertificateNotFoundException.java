package com.project.certificate.custom;

// 自定义异常类
public class CertificateNotFoundException extends RuntimeException {
    public CertificateNotFoundException(String message) {
        super(message);  // 调用父类构造器
    }
}