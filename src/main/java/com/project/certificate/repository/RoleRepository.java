package com.project.certificate.repository;

import com.project.certificate.custom.CommonRepository;
import com.project.certificate.entity.SysRole;
import org.springframework.stereotype.Repository;


@Repository
public interface RoleRepository extends CommonRepository<SysRole, Integer> {

}
