package com.project.certificate.controller;

import com.project.certificate.custom.AjaxResult;
import com.project.certificate.custom.JpgridUtils;
import com.project.certificate.entity.Question;
import com.project.certificate.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Controller
@RequestMapping("/question")
public class QuestionController {
    @Autowired
    private QuestionService questionDAO;

    @RequestMapping(value="/all")
    public String all() {
        return "admin/question_all";
    }

    @RequestMapping(value="/add")
    public String add() {
        return "user/question_add";
    }

    /*
     * 添加
     */
    @RequestMapping(value="/save")
    @ResponseBody
    public Object save(Question model) {
        Date dateTime=new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date=df.format(dateTime);
        model.setReleasedate(date);
        questionDAO.save(model);
        return AjaxResult.build(true, "OK");
    }

    /*
     * 查询
     */
    @RequestMapping(value="/list")
    @ResponseBody
    public Object list(JpgridUtils form, String question) {
        Pageable pageable = form.buildPageable();
        Page<Question> page = null;
        Specification<Question> spec = new Specification<Question>() {
            @Override
            public Predicate toPredicate(Root<Question> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                List<Predicate> rules = new ArrayList<>();
                if(StringUtils.hasText(question)) {
                    rules.add(criteriaBuilder.like(root.get("question"), "%"+question+"%"));
                }
                return criteriaBuilder.and(rules.toArray(new Predicate[rules.size()]));
            }
        };
        page = questionDAO.findAll(spec, pageable);
        HashMap<String , Object> result = form.getPageResult(page);
        return result;
    }

}
