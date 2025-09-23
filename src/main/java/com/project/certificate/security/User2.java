package com.project.certificate.security;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;


public class User2 extends User {
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public User2(String username, String password, String name, Collection<? extends GrantedAuthority> authorities) {//泛型集合，权限列表
		super(username, password, authorities);//用于UserDetails的实现中的必备属性
		this.name = name;
	}
	
}
