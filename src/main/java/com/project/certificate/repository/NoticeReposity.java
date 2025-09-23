package com.project.certificate.repository;

import com.project.certificate.custom.CommonRepository;
import com.project.certificate.entity.Notice;
import org.springframework.stereotype.Repository;

@Repository
public interface NoticeReposity extends CommonRepository<Notice, Integer> {
	
	public Notice findBynoticetitle(String noticetitle);
	
}
