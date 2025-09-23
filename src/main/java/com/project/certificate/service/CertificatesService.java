package com.project.certificate.service;

import com.project.certificate.custom.CertificateAlreadyRevokedException;
import com.project.certificate.custom.CertificateNotFoundException;
import com.project.certificate.custom.CommonService;
import com.project.certificate.custom.CrlNotFoundException;
import com.project.certificate.entity.CertificateRevocationList;
import com.project.certificate.entity.Certificates;
import com.project.certificate.entity.RevokedCertificateEntry;
import com.project.certificate.repository.CertificateRevocationListRepository;
import com.project.certificate.repository.CertificatesRepository;
import com.project.certificate.repository.RevokedCertificateEntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CertificatesService extends CommonService<Certificates, Integer> {
    @Autowired
    private CertificatesRepository certificatesDAO;

    public Certificates findByInvoiceid(Integer id) {
        return certificatesDAO.findByInvoiceid(id);
    }
    public void deleteByInvoiceid(Integer id){ certificatesDAO.deleteByInvoiceid(id);}


    private final CertificateRevocationListRepository crlRepository;
    private final RevokedCertificateEntryRepository revokedEntryRepository;

    // 构造器注入
    public CertificatesService(CertificatesRepository certificatesRepository,
                               CertificateRevocationListRepository crlRepository,
                               RevokedCertificateEntryRepository revokedEntryRepository) {
        this.certificatesDAO= certificatesRepository;
        this.crlRepository = crlRepository;
        this.revokedEntryRepository = revokedEntryRepository;
    }


    /*
     * 吊销指定序列号的证书
     */
    public void revokeCertificate(String serialNumber, String reason) {
        // 查找证书，不存在则抛出异常
        Certificates certificate = certificatesDAO.findBySerialNumber(serialNumber)
                .orElseThrow(() -> new CertificateNotFoundException("Certificate not found with serial: " + serialNumber));
        //System.out.println("111111111111");
        // 检查证书是否已吊销
        if (certificate.isStatus()) {
            //System.out.println("22222222222222222222");
            throw new CertificateAlreadyRevokedException("Certificate already revoked");
        }
        //System.out.println("3333333333333");
        // 更新证书吊销状态
        certificate.setStatus(true);
        certificate.setRevocationDate(new Date());
        certificate.setRevocationReason(reason);
        certificatesDAO.save(certificate);  // 保存到数据库
        //System.out.println("4444444444444444");
        // 将吊销证书添加到CRL
        addToCrl(certificate);
    }

    // 私有方法：将吊销证书添加到CRL
    private void addToCrl(Certificates certificate) {
        String issuer = certificate.getIssuerDn();
        // 查找或创建CRL
        //System.out.println("555555555555555555");
        CertificateRevocationList crl = crlRepository.findByIssuer(issuer)
                .orElseGet(() -> {
                    CertificateRevocationList newCrl = new CertificateRevocationList();
                    newCrl.setIssuer(issuer);
                    newCrl.setThisUpdate(new Date());
                    newCrl.setNextUpdate(calculateNextUpdateDate());
                    newCrl.setCrlNumber(generateCrlNumber());
                    return crlRepository.save(newCrl);  // 保存新创建的CRL
                });
        //System.out.println("66666666666666666666666666666");
        // 创建吊销条目
        RevokedCertificateEntry entry = new RevokedCertificateEntry();
        entry.setSerialNumber(certificate.getSerialNumber());
        entry.setRevocationDate(certificate.getRevocationDate());
        entry.setRevocationReason(certificate.getRevocationReason());
        entry.setCrl(crl);  // 设置关联的CRL
        revokedEntryRepository.save(entry);  // 保存条目
    }

    // 私有方法：计算下次CRL更新时间(默认为7天后)
    private Date calculateNextUpdateDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 7); // 当前日期加7天
        return calendar.getTime();
    }

    // 私有方法：生成CRL编号(使用UUID)
    private String generateCrlNumber() {
        return UUID.randomUUID().toString();  // 生成唯一标识
    }
}
