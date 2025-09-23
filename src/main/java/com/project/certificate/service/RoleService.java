package com.project.certificate.service;

import com.project.certificate.custom.CommonService;
import com.project.certificate.entity.SysRole;
import com.project.certificate.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoleService extends CommonService<SysRole, Integer> {
	@Autowired
	private RoleRepository roleDAO;
}
