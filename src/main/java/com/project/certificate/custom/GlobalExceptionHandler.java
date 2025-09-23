package com.project.certificate.custom;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice  // 全局控制器增强，处理所有控制器抛出的异常
public class GlobalExceptionHandler {

    /**
     * 处理证书不存在异常
     *
     * @param ex 异常对象
     * @return 404响应
     */
    @ExceptionHandler(CertificateNotFoundException.class)
    public ResponseEntity<String> handleCertificateNotFound(CertificateNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
    /**
     * 处理证书已吊销异常
     * @param ex 异常对象
     * @return 400响应
     */
    @ExceptionHandler(CertificateAlreadyRevokedException.class)
    public ResponseEntity<String> handleCertificateAlreadyRevoked(CertificateAlreadyRevokedException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    /**
     * 处理CRL不存在异常
     * @param ex 异常对象
     * @return 404响应
     */
    @ExceptionHandler(CrlNotFoundException.class)
    public ResponseEntity<String> handleCrlNotFound(CrlNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
}





