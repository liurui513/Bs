package com.project.certificate.service;

import com.project.certificate.custom.CommonService;
import com.project.certificate.entity.KeyPairEntity;
import com.project.certificate.entity.SysUser;
import com.project.certificate.repository.KeyPairRepository;
import com.project.certificate.security.UserUtils;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.crypto.Cipher;
import java.io.*;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Date;

@Service
public class KeyPairService extends CommonService<KeyPairEntity, Integer> {

    @Autowired
    private KeyPairRepository keyPairDAO;

    @Autowired
    private UserUtils userUtils;

    public void generateAndSaveKeyPair() {
        try {
            // 生成 RSA 密钥对
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();

            // 将密钥对转换为 Base64 字符串
            String publicKey = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
            String privateKey = Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());

            // 保存到数据库
            KeyPairEntity keyPairEntity = new KeyPairEntity();
            keyPairEntity.setPublicKey(publicKey);
            keyPairEntity.setPrivateKey(privateKey);
            keyPairEntity.setCreatedTime(LocalDateTime.now());
            SysUser sysuser = userUtils.getUser();
            keyPairEntity.setSysUser(sysuser);
            keyPairDAO.save(keyPairEntity);
            // 保存到文件
            savePublicKeyToFile("public_key.pem", publicKey);
            savePrivateKeyToFile("private_key.pem", privateKey);
        } catch (NoSuchAlgorithmException | IOException e) {
            throw new RuntimeException("Error generating RSA key pair", e);
        }
    }

    private void  savePublicKeyToFile(String fileName, String publicKey)throws IOException{
        try (FileOutputStream fos = new FileOutputStream(fileName);
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos))) {
            // 写入公钥
            writer.write("-----BEGIN PUBLIC KEY-----");
            writer.newLine();
            writer.write(publicKey);
            writer.newLine();
            writer.write("-----END PUBLIC KEY-----");
            writer.newLine();
        }
    }
    private void  savePrivateKeyToFile(String fileName, String privateKey)throws IOException{
        try (FileOutputStream fos = new FileOutputStream(fileName);
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos))) {
            // 写入公钥
            writer.write("-----BEGIN PRIVATE KEY-----");
            writer.newLine();
            writer.write(privateKey);
            writer.newLine();
            writer.write("-----END PRIVATE KEY-----");
            writer.newLine();
        }
    }
  /*  // 获取 RSA 密钥对
    public KeyPairEntity getRSAKeyPair(Integer id) {
        return keyPairDAO.findById(id).orElseThrow(() -> new RuntimeException("Key pair not found"));
    }*/
    @Value("${security.publickey}")
    private String publicKeyStr;

    @Value("${security.privatekey}")
    private String privateKeyStr;
    // 使用 RSA 公钥加密数据
    public String encryptWithRSA(String data) throws Exception {
    /*    KeyPairEntity keyPairEntity = getRSAKeyPair(1);
        PublicKey publicKey = KeyFactory.getInstance("RSA")
                .generatePublic(new X509EncodedKeySpec(Base64.getDecoder().decode(keyPairEntity.getPublicKey())));
    */    Cipher cipher = Cipher.getInstance("RSA");

        byte[] keyBytes = Base64.getDecoder().decode(publicKeyStr);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        //return kf.generatePublic(spec);

        cipher.init(Cipher.ENCRYPT_MODE, kf.generatePublic(spec));
        byte[] encryptedData = cipher.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(encryptedData);
    }

    // 使用 RSA 私钥解密数据
    public String decryptWithRSA(String encryptedDataBase64) throws Exception {
       /* KeyPairEntity keyPairEntity = getRSAKeyPair(1);
        PrivateKey privateKey = KeyFactory.getInstance("RSA")
                .generatePrivate(new PKCS8EncodedKeySpec(Base64.getDecoder().decode(keyPairEntity.getPrivateKey())));
     */   Cipher cipher = Cipher.getInstance("RSA");

        byte[] keyBytes = Base64.getDecoder().decode(privateKeyStr);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        //return kf.generatePrivate(spec);

        cipher.init(Cipher.DECRYPT_MODE, kf.generatePrivate(spec));
        byte[] decryptedData = cipher.doFinal(Base64.getDecoder().decode(encryptedDataBase64));
        return new String(decryptedData);
    }

}