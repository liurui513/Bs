package com.project.certificate.repository;

import com.project.certificate.custom.CommonRepository;
import com.project.certificate.entity.Journal;
import org.springframework.stereotype.Repository;

@Repository
public interface JournalRepository extends CommonRepository<Journal, Integer> {
}