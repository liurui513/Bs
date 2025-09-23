package com.project.certificate.controller;

import com.project.certificate.entity.KeyPairEntity;
import com.project.certificate.entity.SysUser;
import com.project.certificate.security.UserUtils;
import com.project.certificate.service.KeyPairService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping(value="/keypair")
public class KeyPairController {

    @Autowired
    private KeyPairService keyPairDAO;
    @Autowired
    private UserUtils userUtils;

    @RequestMapping("/generate-key-pair")
    public ResponseEntity<String> generateKeyPair() {
        keyPairDAO.generateAndSaveKeyPair();
        // 返回下载链接
        String response = "Key pair generated successfully!<br>" +
                "<a href='/certificate/download/public_key.pem'>Download Public Key</a><br>"+
                "<a href='/certificate/download1/private_key.pem'>Download Private Key</a><br>";
        return ResponseEntity.ok(response);
    }

    @RequestMapping(value="/jiami")
    public String jiami() {
        return "user/crypto";
    }

    @RequestMapping("/encrypt")
    public ResponseEntity<Map<String, String>> encrypt(@RequestBody String data) throws Exception {
        String encryptedData = keyPairDAO.encryptWithRSA(data);
        Map<String, String> response = new HashMap<>();
        response.put("encryptedData", encryptedData);
        return ResponseEntity.ok(response);
    }

    @RequestMapping("/decrypt")
    public ResponseEntity<Map<String, String>> decrypt(@RequestBody Map<String, String> requestData) throws Exception {
        String encryptedData = requestData.get("encryptedData");
        String decryptedData = keyPairDAO.decryptWithRSA(encryptedData);
        Map<String, String> response = new HashMap<>();
        response.put("decryptedData", decryptedData);
        return ResponseEntity.ok(response);
    }
}