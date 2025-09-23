package com.project.certificate.service;

import java.util.ArrayList;
import java.util.List;

import com.project.certificate.custom.CommonService;
import com.project.certificate.entity.SysRole;
import com.project.certificate.entity.SysUser;
import com.project.certificate.repository.SysUserRepository;
import com.project.certificate.security.User2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class SysUserService extends CommonService<SysUser, Integer> implements UserDetailsService{
	@Autowired
	private SysUserRepository userDAO;
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		SysUser user= userDAO.findByUsername(username);
		if(user==null) {
			throw new UsernameNotFoundException(username);
		}
		List<GrantedAuthority> authorities=new ArrayList<>();//存储用户的权限
		List<SysRole> roles = user.getRoles();
		for (SysRole role : roles) {
			authorities.add(new SimpleGrantedAuthority(role.getCode()));//将用户角色添加到权限里
		}
		return new User2(user.getUsername(), user.getPassword(),user.getName(), authorities);
	}
	
	public SysUser findByUsername(String username) {
		return userDAO.findByUsername(username);
	}

	public SysUser findByName(String name) {
		return userDAO.findByName(name);
	}
	@Transactional
	public void syncMatchingUsers() {
		// 执行插入操作
		userDAO.insertMatchingUsers();
	}
}