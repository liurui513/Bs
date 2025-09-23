package com.project.certificate.service;

import com.project.certificate.custom.CommonService;
import com.project.certificate.entity.Invoice;
import com.project.certificate.entity.Lessee;
import com.project.certificate.entity.User;
import com.project.certificate.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService extends CommonService<User, Integer> {
	@Autowired
	private UserRepository userDAO;
}
