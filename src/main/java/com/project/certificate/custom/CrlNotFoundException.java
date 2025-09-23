package com.project.certificate.custom;

public class CrlNotFoundException extends RuntimeException {
    public CrlNotFoundException(String message) {
        super(message);  // 调用父类构造器
    }
}

