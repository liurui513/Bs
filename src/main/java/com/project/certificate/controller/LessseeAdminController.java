package com.project.certificate.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.project.certificate.custom.AjaxResult;
import com.project.certificate.custom.JpgridUtils;
import com.project.certificate.entity.LesseeAdmin;
import com.project.certificate.entity.SysRole;
import com.project.certificate.entity.SysUser;
import com.project.certificate.repository.RoleRepository;
import com.project.certificate.service.LesseeAdminService;
import com.project.certificate.service.RoleService;
import com.project.certificate.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequestMapping(value="/lesseeAdmin")
public class LessseeAdminController {
	@Autowired
	private LesseeAdminService lesseeAdminDAO;
	@Autowired
	private SysUserService sysUserDAO;
	@Autowired
	private RoleService roleDAO;
	
	@RequestMapping(value="/all")
	public String all() {
		return "system/lesseeAdmin_all";
	}
	
	@RequestMapping(value="/update")
	public String update() {
		return "admin/update";
	}

	/* 
	 * 重置管理员密码 
	 */
	@RequestMapping(value="/cz")
	@ResponseBody
	public String cz(Integer id) {
		SysUser sysuser = sysUserDAO.findById(id);
		BCryptPasswordEncoder encoder=new BCryptPasswordEncoder();
		sysuser.setPassword(encoder.encode("111111"));
		sysUserDAO.save(sysuser);
		return "重置成功！";
	}

	/* 
	 * 判断用户名是否存在
	 */
	  @RequestMapping(value="/username")  
	  @ResponseBody 
	  public Boolean username(String username) { 
		  SysUser user = sysUserDAO.findByUsername(username); 
		  if(user == null) { 
			  return false; 
		}else {
			return true; 
		} 
	}

	/* 
	 * 显示所有公司/组织管理员
	 */
	@RequestMapping(value="/list")
	@ResponseBody
	public Object list(JpgridUtils form, String ou, String name) {
		Pageable pageable = form.buildPageable();
		Page<LesseeAdmin> page = null;
		Specification<LesseeAdmin> spec = new Specification<LesseeAdmin>() {
			@Override
			public Predicate toPredicate(Root<LesseeAdmin> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
				List<Predicate> rules = new ArrayList<>();
				if(StringUtils.hasText(name)) {
					rules.add(criteriaBuilder.like(root.get("name"), "%"+name+"%"));
				}else if(StringUtils.hasText(ou)) {
					rules.add(criteriaBuilder.like(root.get("ou"), "%"+ou+"%"));
				}
				return criteriaBuilder.and(rules.toArray(new Predicate[rules.size()]));
			}			
		};
		page = lesseeAdminDAO.findAll(spec, pageable);
		HashMap<String , Object> result = form.getPageResult(page);
		return result;
	}
	
	//添加公司/组织管理员
	@RequestMapping(value="/save")
	@ResponseBody
	public Object save(LesseeAdmin model) {
		BCryptPasswordEncoder encoder=new BCryptPasswordEncoder();   //加密类
		SysRole role=roleDAO.findById(2);
		model.setPassword(encoder.encode("111111"));
		model.getRoles().add(role);
		lesseeAdminDAO.save(model);
		return AjaxResult.build(true, "OK");
	}
	
	
}
