package com.project.certificate.service;

import com.project.certificate.custom.CommonService;
import com.project.certificate.entity.LesseeAdmin;
import com.project.certificate.repository.LesseeAdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LesseeAdminService extends CommonService<LesseeAdmin, Integer> {
	@Autowired
	private LesseeAdminRepository lesseeAdminDAO;
}
