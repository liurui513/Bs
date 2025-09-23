package com.project.certificate.repository;


import com.project.certificate.custom.CommonRepository;
import com.project.certificate.entity.CertificateRevocationList;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CertificateRevocationListRepository extends CommonRepository<CertificateRevocationList, Long> {
    // 根据颁发者查找CRL
    Optional<CertificateRevocationList> findByIssuer(String issuer);  // 颁发者通常是CA的DN
}