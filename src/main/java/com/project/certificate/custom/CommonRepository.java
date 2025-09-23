package com.project.certificate.custom;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.Optional;

@NoRepositoryBean
public interface CommonRepository<T,ID> extends JpaRepository<T, ID>,JpaSpecificationExecutor<T>{

}
