package com.project.certificate.custom;

public class CertificateAlreadyRevokedException extends RuntimeException {
    public CertificateAlreadyRevokedException(String message) {
        super(message);  // 调用父类构造器
    }
}