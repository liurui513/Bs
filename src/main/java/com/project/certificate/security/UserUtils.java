package com.project.certificate.security;

import com.project.certificate.entity.SysUser;
import com.project.certificate.repository.SysUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;


//获取当前登录用户信息
@Component
public class UserUtils {
	@Autowired
	private SysUserRepository userDAO;
	
	public SysUser getUser() {
		UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String username = userDetails.getUsername();
		SysUser user = userDAO.findByUsername(username);
		return user;
	}
}
