package com.project.certificate.repository;

import com.project.certificate.custom.CommonRepository;
import com.project.certificate.entity.RevokedCertificateEntry;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RevokedCertificateEntryRepository extends CommonRepository<RevokedCertificateEntry, Long> {

    // 根据序列号查找吊销条目
    //Optional<RevokedCertificateEntry> findBySerialNumber(String serialNumber);  // 检查证书是否在CRL中
}

