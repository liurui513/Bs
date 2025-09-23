package com.project.certificate.repository;

import com.project.certificate.custom.CommonRepository;
import com.project.certificate.entity.Accredit;
import org.springframework.stereotype.Repository;

@Repository
public interface AccreditRepository extends CommonRepository<Accredit, Integer> {

	public Accredit findByLesseeId(Integer id);

}
