package com.project.certificate.service;

import com.project.certificate.custom.CommonService;
import com.project.certificate.entity.Accredit;
import com.project.certificate.repository.AccreditRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccreditService extends CommonService<Accredit, Integer> {
	@Autowired
	private AccreditRepository accreditDAO;

	public Accredit findByLesseeId(Integer id) {
		return accreditDAO.findByLesseeId(id);
	}
}
