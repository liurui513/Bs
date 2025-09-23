package com.project.certificate.service;

import com.project.certificate.custom.CommonService;
import com.project.certificate.entity.Lessee;
import com.project.certificate.entity.Question;
import com.project.certificate.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class QuestionService  extends CommonService<Question, Integer> {
    @Autowired
    private QuestionRepository questionDAO;

    public Question findByQuestion(String question) {
        return questionDAO.findByQuestion(question);
    }

}