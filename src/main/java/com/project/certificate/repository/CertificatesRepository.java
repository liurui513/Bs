package com.project.certificate.repository;

import com.project.certificate.custom.CommonRepository;
import com.project.certificate.entity.Certificates;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.List;
import java.util.Optional;

@Repository
public interface  CertificatesRepository extends CommonRepository<Certificates, Integer> {
    public Certificates findByInvoiceid(Integer id);

    public void deleteByInvoiceid(Integer id);

    // 根据序列号查找证书
    Optional<Certificates> findBySerialNumber(String serialNumber);  // 返回Optional避免空指针

    // 根据吊销状态查找证书列表
   // List<Certificates> findByStatus(boolean status);  // true查找已吊销，false查找未吊销
}
