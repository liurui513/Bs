package com.project.certificate.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.project.certificate.custom.AjaxResult;
import com.project.certificate.custom.JpgridUtils;
import com.project.certificate.entity.Notice;
import com.project.certificate.service.NoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value="/notice")
public class NoticeController {
	@Autowired
	private NoticeService noticeDAO;
	
	@RequestMapping(value="/all")
	public String all() {
		return "admin/notice_all";
	}

	@RequestMapping(value="/add")
	public String add() {
		return "admin/notice_add";
	}
	/*
	 *  验证文档标题是否已添加 
	 */
	@RequestMapping(value="/noticetitle")
	@ResponseBody
	public Boolean username(String noticetitle) {
		Notice title = noticeDAO.findBynoticetitle(noticetitle);
		if(title == null) {
			return false;
		}else {
			return true;
		}
	}
	
	/* 
	 * 查询
	 */
	@RequestMapping(value="/list")
	@ResponseBody
	public Object list(JpgridUtils form, String noticetitle) {
		Pageable pageable = form.buildPageable();
		Page<Notice> page = null;
		Specification<Notice> spec = new Specification<Notice>() {
			@Override
			public Predicate toPredicate(Root<Notice> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
				List<Predicate> rules = new ArrayList<>();
				if(StringUtils.hasText(noticetitle)) {
					rules.add(criteriaBuilder.like(root.get("noticetitle"), "%"+noticetitle+"%"));
				}
				return criteriaBuilder.and(rules.toArray(new Predicate[rules.size()]));
			}			
		};
		page = noticeDAO.findAll(spec, pageable);
		HashMap<String , Object> result = form.getPageResult(page);
		return result;
	}
	
	
	/* 
	 * 添加
	 */
	@RequestMapping(value="/save")
	@ResponseBody
	public Object save(Notice model) {
		Date dateTime=new Date();
	    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    String date=df.format(dateTime);
		model.setReleasedate(date);
		noticeDAO.save(model);
		return AjaxResult.build(true, "OK");
	}
	
	/* 
	 * 删除
	 */
	@RequestMapping(value="/delete")
	@ResponseBody
	public void delete(String ids) {
		String[] id = ids.split(","); 
		for(int i = 0; i < id.length; i++) {
			noticeDAO.deleteById(Integer.parseInt(id[i]));
		}
	}
}
