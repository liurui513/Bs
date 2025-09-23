package com.project.certificate.controller;

import com.project.certificate.custom.JpgridUtils;
import com.project.certificate.entity.Journal;
import com.project.certificate.service.JournalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.persistence.criteria.Predicate;

@Controller
@RequestMapping(value="/journal")
public class JournalController {
    @Autowired
    private JournalService journalDAO;

    @RequestMapping(value="/all")
    public String all() {
        return "system/journal_all";
    }

    /*
     * 查询用户操作日志
     */
    @RequestMapping(value="/list")
    @ResponseBody
    public Object list(JpgridUtils form, String operationName, String date, String username) {
        Pageable pageable = form.buildPageableDesc();
        Page<Journal> page = null;
        Specification<Journal> spec = new Specification<Journal>() {
            @Override
            public Predicate toPredicate(Root<Journal> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                List<Predicate> rules = new ArrayList<>();
                if(StringUtils.hasText(date)) {
                    rules.add(criteriaBuilder.like(root.get("date"), "%"+date+"%"));
                }else if(StringUtils.hasText(operationName)) {
                    rules.add(criteriaBuilder.like(root.get("operationName"), "%"+operationName+"%"));
                }else if(StringUtils.hasText(username)) {
                    rules.add(criteriaBuilder.like(root.get("username"), "%"+username+"%"));
                }
                return criteriaBuilder.and(rules.toArray(new Predicate[rules.size()]));
            }
        };
        page = journalDAO.findAll(spec, pageable);
        HashMap<String , Object> result = form.getPageResult(page);
        return result;
    }
}

