package com.project.certificate.service;

import com.project.certificate.custom.CommonService;
import com.project.certificate.entity.Notice;
import com.project.certificate.repository.NoticeReposity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NoticeService extends CommonService<Notice, Integer> {

	@Autowired
	private NoticeReposity noticeDAO;

	public Notice findBynoticetitle(String noticetitle) {
		return noticeDAO.findBynoticetitle(noticetitle);
	}

}
