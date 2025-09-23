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
import com.project.certificate.entity.*;
import com.project.certificate.repository.RoleRepository;
import com.project.certificate.security.UserUtils;
import com.project.certificate.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value="/user")
public class UserController {
	@Autowired
	private UserService userDAO;
	@Autowired
	private LesseeService lesseeDAO;
	@Autowired
	private UserUtils userUtils;
	@Autowired
	private RoleRepository roleDAO;
	@Autowired
	private SysUserService sysuserDAO;
	@Autowired
	private JournalService journalDAO;
	/* 
	 * 管理员查看所有用户信息
	 */
	@RequestMapping(value="/all")
	public String all(ModelMap map) {
		SysUser sysuser = userUtils.getUser();
		List<Lessee> list = lesseeDAO.findByLesseeAdminId(sysuser.getId());
		map.put("lessee", list);
		return "admin/user_all";
	}

	/*
	*用户查看自己的信息
	*/
	@RequestMapping(value="/all1")
	public String all1(ModelMap model) {
		SysUser sysuser = userUtils.getUser();
		Lessee lessee = lesseeDAO.findByLinkman(sysuser.getName());
		model.addAttribute("user", sysuser);
		model.addAttribute("lessee",lessee);
		return "user/user";
	}


	@RequestMapping(value="/list1")
	@ResponseBody
	public Object  list1(JpgridUtils form) {
		SysUser sysuser = userUtils.getUser();
		Lessee lessee = lesseeDAO.findByLinkman(sysuser.getName());
		User user = userDAO.findById(sysuser.getId());
			Pageable pageable = form.buildPageable();
			Page<User> page = null;
			Specification<User> spec = new Specification<User>() {//实现匿名内部类，重构查询条件
				@Override
				public Predicate toPredicate(Root<User> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
					List<Predicate> rules = new ArrayList<>();//存储查询条件
					return criteriaBuilder.and(rules.toArray(new Predicate[rules.size()]));
					//将所有条件采用and的形式连接起来
				}
			};
			page = userDAO.findAll(spec, pageable);
			HashMap<String , Object> result = form.getPageResult(page);//将分页信息封装到HashMap中
			return result;
		}




	/*
	 *  验证用户名是否已添加 
	 */
	@RequestMapping(value="/username")
	@ResponseBody
	public Boolean username(String username) {
		SysUser sysuser = sysuserDAO.findByUsername(username);
		if(sysuser == null) {
			return false;
		}else {
			return true;
		}
	}
	
	/* 
	 * 用户修改密码 
	 */
	@RequestMapping(value="/update")
	public String update() {
		return "user/update";
	}
	
	/*
	 *  查询用户
	 */
	@RequestMapping(value="/list")
	@ResponseBody
	public Object list(JpgridUtils form, String name, String username) {
		SysUser sysuser = userUtils.getUser();
		Pageable pageable = form.buildPageable();
		Page<User> page = null;
		Specification<User> spec = new Specification<User>() {//实现匿名内部类，重构查询条件
			@Override
			public Predicate toPredicate(Root<User> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
				List<Predicate> rules = new ArrayList<>();//存储查询条件
				if(StringUtils.hasText(name)) {
					rules.add(criteriaBuilder.like(root.get("name"), "%"+name+"%"));
				}else if(StringUtils.hasText(username)) {
					rules.add(criteriaBuilder.like(root.get("username"), "%"+username+"%"));
				}
				rules.add(criteriaBuilder.equal(root.get("lessee").get("lesseeAdmin").get("id"), sysuser.getId()));
				//lesseeAdmin的id==sysuser的id，只有管理员才能查询所有用户的信息
				return criteriaBuilder.and(rules.toArray(new Predicate[rules.size()]));
				//将所有条件采用and的形式连接起来
			}			
		};
		page = userDAO.findAll(spec, pageable);
		HashMap<String , Object> result = form.getPageResult(page);//将分页信息封装到HashMap中
		return result;
	}
	
	/* 
	 * 重置密码 
	 */
	@RequestMapping(value="/cz")
	@ResponseBody
	public String cz(Integer id) {
		SysUser sysuser = sysuserDAO.findById(id);
		BCryptPasswordEncoder encoder=new BCryptPasswordEncoder();
		sysuser.setPassword(encoder.encode("111111"));
		sysuserDAO.save(sysuser);
		return "重置成功！";
	}
	
	/* 
	 * 新增用户 
	 */
	@RequestMapping(value="/save")
	@ResponseBody
	public Object save(User model) {
		BCryptPasswordEncoder encoder=new BCryptPasswordEncoder();   //加密类
		SysRole role=roleDAO.findById(3).get();
		SysUser user = userUtils.getUser();
		if((model.getPassword() == null || model.getPassword() == "")) {
			model.setPassword(encoder.encode("111111"));
			model.setSf(1);
			System.out.println("添加用户");
			/*JournalUtil.log(user.getUsername(), "添加用户");*/
			//添加用户日志
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
			String data = df.format(new Date());
			Journal journal = new Journal();
			journal.setDate(data);
			journal.setUsername(user.getUsername());
			journal.setOperationName("添加用户");
			System.out.println(journal.getOperationName());
			journalDAO.save(journal);
		}
		model.getRoles().add(role);
		userDAO.save(model);
		return AjaxResult.build(true, "OK");
	}


	/* 
	 * 级联删除用户 
	 */
	@RequestMapping(value="/delete")
	@ResponseBody
	public void delete(String ids) {
		String[] id = ids.split(","); 
		for(int i = 0; i < id.length; i++) {
			userDAO.deleteById(Integer.parseInt(id[i]));
		}
	}

}
