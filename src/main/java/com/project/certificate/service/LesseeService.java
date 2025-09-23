package com.project.certificate.service;

import java.util.List;

import com.project.certificate.custom.CommonService;
import com.project.certificate.entity.Lessee;
import com.project.certificate.repository.LesseeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LesseeService extends CommonService<Lessee, Integer> {
	@Autowired
	private LesseeRepository lesseeDAO;

	public Lessee findByName(String username) {
		return lesseeDAO.findByName(username);
	}

	public List<Lessee> findByLesseeAdminId(Integer id) {
		return lesseeDAO.findByLesseeAdminId(id);
	}

    public Lessee findByLinkman(String username) {
		return lesseeDAO.findByLinkman(username);
    }
}
