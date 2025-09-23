package com.project.certificate.repository;

import java.util.List;
import java.util.Optional;

import com.project.certificate.custom.CommonRepository;
import com.project.certificate.entity.Lessee;
import org.springframework.stereotype.Repository;

@Repository
public interface LesseeRepository extends CommonRepository<Lessee, Integer> {

	public Lessee findByName(String username);

	public List<Lessee> findByLesseeAdminId(Integer id);

	public Lessee findByLinkman(String username);
}
