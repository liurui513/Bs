package com.project.certificate.repository;

import com.project.certificate.custom.CommonRepository;
import com.project.certificate.entity.SysUser;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
public interface SysUserRepository extends CommonRepository<SysUser, Integer> {

	public SysUser findByUsername(String username);
	public SysUser findByName(String name);

	@Modifying
	@Query(value = "INSERT INTO user (id, lessee_id) " +
			"SELECT su.id, le.id FROM sys_user su " +
			"JOIN lessee le ON su.name = le.linkman " +
			"ON DUPLICATE KEY UPDATE lessee_id = VALUES(lessee_id)",
			nativeQuery = true)
	int insertMatchingUsers();
}
