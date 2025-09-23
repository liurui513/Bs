package com.project.certificate.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpSession;

import com.project.certificate.custom.JpgridUtils;
import com.project.certificate.entity.*;
import com.project.certificate.security.UserUtils;
import com.project.certificate.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class LoginController {
	@Autowired
	private UserUtils userUtils;
	@Autowired
	private SysUserService sysuserDAO;
	@Autowired
	private RoleService roleDAO;
	@Autowired
	private LesseeAdminService lesseeAdminDAO;

	@Autowired
	private UserService userDAO;
	@Autowired
	private NoticeService noticeDAO;
	@Autowired
	private AccreditService accreditDAO;
	@Autowired
	private  QuestionService questionDAO;
	@Autowired
	private JournalService journalDAO;

	/*
	 * 根据id查询公告详情，且返回到indexdetail.html页面上
	 */
	@RequestMapping(value="/findNoticeById")
	public String findNoticeById(Integer id,ModelMap map) {
		Notice notice=noticeDAO.findById(id);
		map.put("notice", notice);
		return "indexdetail";
	}

	@GetMapping(value="/login")
	public String login(ModelMap map) {
		List<Notice> noticeList=noticeDAO.findAll();
		List<Question> questionList = questionDAO.findAll();
		List<SysUser> list = sysuserDAO.findAll();
		if(list.size() == 0) {
			test();
		}
		map.put("noticeList", noticeList);
		map.put("questionList",questionList);
		return "index";
	}
	
	/* 
	 * 判断用户名是否存在 
	 */
	  @RequestMapping(value="/username")
	  @ResponseBody 
	  public Boolean username(String username) { 
		  SysUser sysuser=sysuserDAO.findByUsername(username); 
		  if(sysuser == null) { 
			  return false; 
		  } 
		  else{ 
			  return true; 
		  } 
	}
	
	/* 
	 * 判断原密码是否正确 
	 */
	@RequestMapping(value="/pwd")
	@ResponseBody
	public Boolean pwd(String password) {
		SysUser sysuser = userUtils.getUser();
		BCryptPasswordEncoder encoder=new BCryptPasswordEncoder();//验证密码
		boolean f = encoder.matches(password,sysuser.getPassword());
		return f;
	}
	
	/* 
	 * 修改密码
	 */
	  @RequestMapping(value="/xg") 
	  public String xg(String pad) { 
		  SysUser sysuser =userUtils.getUser(); 
		  BCryptPasswordEncoder encoder=new BCryptPasswordEncoder(); 
		  sysuser.setPassword(encoder.encode(pad));
		  sysuserDAO.save(sysuser); 
		  return "redirect:logout"; 
	}
	 
	/* 
	 * 跳转到登录页面 
	 */
	@RequestMapping(value="login1")
	public String login1() {
		return "login";
	}


	/* 
	 * 登录成功跳转日志 
	 */
	@RequestMapping(value="/main")
	public String main(ModelMap map) {
		List<Notice> noticeList=noticeDAO.findAll();
		map.put("noticeList", noticeList);
		List<Question> questionList = questionDAO.findAll();
		map.put("questionList",questionList);
		SysUser sysuser = userUtils.getUser();
		System.out.println("登入");
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
		String data = df.format(new Date());
		Journal journal = new Journal();
		journal.setDate(data);
		journal.setUsername(sysuser.getUsername());
		journal.setOperationName("登入");
		System.out.println(journal.getOperationName());
		journalDAO.save(journal);
		return "redirect:main1";
	}
	
	/* 
	 * 登录成功跳转到欢迎页面 
	 */
	@RequestMapping(value="/main1")
	public String main1(HttpSession session,ModelMap map) {
		List<Notice> noticeList=noticeDAO.findAll();
		map.put("noticeList", noticeList);
		List<Question> questionList = questionDAO.findAll();
		map.put("questionList",questionList);
		SysUser sysuser = userUtils.getUser();
		if(sysuser.getSf() == null) {
			return "main";//进入管理员的界面
		}else {
			User user1 = userDAO.findById(sysuser.getId());//普通用户--name
			Accredit accredit = accreditDAO.findByLesseeId(user1.getLessee().getId());
			String data = accredit.getOverDate();
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			String data1 = df.format(new Date());
			try {
				Date bt=df.parse(data); 
				Date et=df.parse(data1);
				if (bt.before(et)){ //bt（开始时间）是否在 et（结束时间）之前。
					session.setAttribute("str", "贵公司/组织授权已到期");
					return "redirect:logout";
				}else {
					session.removeAttribute("str");
					return "index";
				}
			} catch (ParseException e) {
				e.printStackTrace();
			} 
			return null;
		}
	}
	
	@RequestMapping(value="/str")
	@ResponseBody
	public String str(HttpSession session) {
		session.removeAttribute("str");
		return "ok";
	}

	/* 
	 * 退出登录 
	 */
	@RequestMapping(value="/logout2")
	public String logout2(HttpSession session,ModelMap map) {
		List<Notice> noticeList=noticeDAO.findAll();
		map.put("noticeList", noticeList);
		List<Question> questionList = questionDAO.findAll();
		map.put("questionList",questionList);
		String str = (String) session.getAttribute("str");
		if(str == null) {
			return "index";
		}else {
			return "redirect:login1";
		}
	}
	
	/* 
	 * 退出日志 
	 */
	@RequestMapping(value="/logout1")
	public String logout1() {
		SysUser user = userUtils.getUser();
		System.out.println("登出");
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
		String data = df.format(new Date());
		Journal journal = new Journal();
		journal.setDate(data);
		journal.setUsername(user.getUsername());
		journal.setOperationName("登出");
		System.out.println(journal.getOperationName());
		journalDAO.save(journal);
		return "redirect:logout";
	}
	
	
	/* 
	 * 数据库初始时没有数据，先添加数据 
	 */
	private void test() {
		BCryptPasswordEncoder encoder=new BCryptPasswordEncoder();   //密码加密
		SysRole role = new SysRole();
		role.setName("系统管理员");
		role.setCode("ROLE_SYSTEM");
		roleDAO.save(role);
		SysRole role2 = new SysRole();
		role2.setName("租户管理员");
		role2.setCode("ROLE_ADMIN");
		roleDAO.save(role2);
		SysRole role3 = new SysRole();
		role3.setName("用户");
		role3.setCode("ROLE_USER");
		roleDAO.save(role3);
		
		SysUser sysuser = new SysUser();
		sysuser.setUsername("system");
		sysuser.setPassword(encoder.encode("system"));   //encode密码加密
		sysuser.getRoles().add(role);
		sysuserDAO.save(sysuser);
		
		LesseeAdmin lesseeAdmin = new LesseeAdmin();
		lesseeAdmin.setUsername("admin");
		lesseeAdmin.setPhone("123456");
		lesseeAdmin.setOu("内蒙古所属教育局");
		lesseeAdmin.setName("管理员");
		lesseeAdmin.setCn("内蒙古教育局");
		lesseeAdmin.setO("内蒙古教育局");
		lesseeAdmin.setC("CN");
		lesseeAdmin.setSt("内蒙古");
		lesseeAdmin.setL("内蒙古");
		lesseeAdmin.setPassword(encoder.encode("admin"));
		lesseeAdmin.getRoles().add(role2);
		lesseeAdminDAO.save(lesseeAdmin);
	}
	
}
