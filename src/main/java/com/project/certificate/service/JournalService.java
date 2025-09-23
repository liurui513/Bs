package com.project.certificate.service;

import com.project.certificate.custom.CommonService;
import com.project.certificate.entity.Journal;
import com.project.certificate.repository.JournalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class JournalService extends CommonService<Journal, Integer> {
    @Autowired
    private JournalRepository journalDAO;
}
