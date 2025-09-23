package com.project.certificate.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.constraints.Null;

import com.project.certificate.custom.JpgridUtils;
import com.project.certificate.entity.*;
import com.project.certificate.security.UserUtils;
import com.project.certificate.service.*;
import org.apache.commons.lang.ObjectUtils;
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
@RequestMapping(value="/lessee")
public class LesseeController {
	@Autowired
	private LesseeService lesseeDAO;
	@Autowired
	private UserUtils userUtils;
	@Autowired
	private LesseeAdminService lesseeAdminDAO;
	@Autowired
	private AccreditService accreditDAO;
	@Autowired
	private SysUserService sysuserDAO;
	@Autowired
	private JournalService journalDAO;
	
	@RequestMapping(value="/all")
	public String all() {
		return "admin/lessee_all";
	}
	
	/* 
	 * 注册公司/组织 
	 */
	@RequestMapping(value="/add")
	public String add() {
		return "admin/lessee_add";
	}
	
	/* 
	 * 查询公司/组织管理 
	 */
	@RequestMapping(value="/list")
	@ResponseBody
	public Object list(JpgridUtils form, String name, String linkman) {
		SysUser user = userUtils.getUser();
		Pageable pageable = form.buildPageable();
		Page<Lessee> page = null;
		Specification<Lessee> spec = new Specification<Lessee>() {
			@Override
			public Predicate toPredicate(Root<Lessee> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
				List<Predicate> rules = new ArrayList<>();
				if(StringUtils.hasText(name)) {
					rules.add(criteriaBuilder.like(root.get("name"), "%"+name+"%"));
				}else if(StringUtils.hasText(linkman)) {
					rules.add(criteriaBuilder.like(root.get("linkman"), "%"+linkman+"%"));
				}
				rules.add(criteriaBuilder.equal(root.get("lesseeAdmin").get("id"), user.getId()));
				return criteriaBuilder.and(rules.toArray(new Predicate[rules.size()]));
			}			
		};
		page = lesseeDAO.findAll(spec, pageable);
		HashMap<String , Object> result = form.getPageResult(page);
		return result;
	}
	
	/* 
	 * 统计证书数量
	 */
	@RequestMapping(value="/list1")
	@ResponseBody
	public Object list1(JpgridUtils form, String name, String ou, String linkman) {
		Pageable pageable = form.buildPageable();
		Page<Lessee> page = null;
		Specification<Lessee> spec = new Specification<Lessee>() {
			@Override
			public Predicate toPredicate(Root<Lessee> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
				List<Predicate> rules = new ArrayList<>();
				if(StringUtils.hasText(name)) {
					rules.add(criteriaBuilder.like(root.get("name"), "%"+name+"%"));
				}else if(StringUtils.hasText(ou)) {
					rules.add(criteriaBuilder.like(root.get("lesseeAdmin").get("ou"), "%"+ou+"%"));
				}else if(StringUtils.hasText(linkman)) {
					rules.add(criteriaBuilder.like(root.get("linkman"), "%"+linkman+"%"));
				}
				return criteriaBuilder.and(rules.toArray(new Predicate[rules.size()]));
			}			
		};
		page = lesseeDAO.findAll(spec, pageable);
		HashMap<String , Object> result = form.getPageResult(page);
		return result;
	}
	
	/* 
	 * 判断公司/组织名称是否存在 
	 */
	@RequestMapping(value="/username")
	@ResponseBody
	public Boolean username(String username) {
		Lessee lessee = lesseeDAO.findByName(username);
		if(lessee == null) {
			return false;
		}else {
			return true;
		}
	}
	
	/* 
	 * 注册时公司/组织管理员名称是否正确
	 */
	@RequestMapping(value="/gly")
	@ResponseBody
	public Boolean gly(String username) {
		SysUser sysuser = sysuserDAO.findByUsername(username);
		boolean f = true;
		if(sysuser == null){
			return f = false;
		}
		return f;
	}
	
	/* 
	 * 添加公司/组织 
	 */
	@RequestMapping(value="save")
	public String save(Lessee lessee) {
		//在这添加到user表中
		//System.out.println(lessee.getLinkman()+"1111111111111111111");//linkname==sys_user中的name
		//User  user = userDAO.findByLessee(lessee);
		if( sysuserDAO.findByName(lessee.getName())==null){
			System.out.println("没有这个人");
		}
		sysuserDAO.syncMatchingUsers();
		//System.out.println("在user表中插入数据");
		//添加公司/组织
		SysUser sysuser = userUtils.getUser();
		LesseeAdmin lesseeAdmin = lesseeAdminDAO.findById(sysuser.getId());
		lessee.setNumber(0);
		lessee.setLesseeAdmin(lesseeAdmin);
		lessee.setO(lessee.getName());
		lessee.setC(lesseeAdmin.getC());
		lessee.setL(lesseeAdmin.getL());
		lessee.setSt(lesseeAdmin.getSt());
		lessee.setOu(lesseeAdmin.getOu());
		lesseeDAO.save(lessee);
		//添加一年授权
		Accredit accredit = new Accredit();
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		accredit.setLessee(lessee);
		accredit.setMonth(12);
		accredit.setBeginDate(sdf.format(date));
		accredit.setCost(0);
		Date newDate = stepMonth(date, 12);//
		accredit.setOverDate(sdf.format(newDate));
		accreditDAO.save(accredit);
		System.out.println("注册公司/组织");
		//添加日志
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
		String data = df.format(new Date());
		Journal journal = new Journal();
		journal.setDate(data);
		journal.setUsername(sysuser.getUsername());
		journal.setOperationName("注册公司/组织");
		System.out.println(journal.getOperationName());
		journalDAO.save(journal);
		return "redirect:add";
	}

	/* 
	 * 在原有时间上添加几个月 
	 */
	public static Date stepMonth(Date sourceDate, int month) {
        Calendar c = Calendar.getInstance();//默认使用当前时区和语言环境
        c.setTime(sourceDate);
        c.add(Calendar.MONTH, month);
        return c.getTime();
    }

}
