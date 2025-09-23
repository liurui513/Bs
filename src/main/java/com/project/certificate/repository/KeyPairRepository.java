package com.project.certificate.repository;

import com.project.certificate.custom.CommonRepository;
import com.project.certificate.entity.KeyPairEntity;
import org.springframework.stereotype.Repository;

import java.security.KeyPair;
import java.util.List;

@Repository
public interface KeyPairRepository extends CommonRepository<KeyPairEntity, Integer> {

}