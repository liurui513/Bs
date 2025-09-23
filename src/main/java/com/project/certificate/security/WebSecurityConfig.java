package com.project.certificate.security;

import com.project.certificate.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	@Autowired
	private SysUserService sysUserService;

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth
			.userDetailsService(sysUserService)  	//设置用什么去查找用户,用什么对象去查找用户
			.passwordEncoder(new BCryptPasswordEncoder()); //设置用密码加密，实例化一个密码生成器
		
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
			http.csrf().disable();
		 	http.headers().frameOptions().disable();    //解决iframe与安全器兼容性问题
		 	http.logout()
		 		.logoutSuccessUrl("/logout2")     //指定安全退出跳转页面
		 		.invalidateHttpSession(false);		//安全退出不删除会话
			http.formLogin()
					.loginPage("/login").permitAll()//登录请求被拦截
					.successForwardUrl("/main")//设置默认登录成功跳转页面
	                .failureUrl("/login1?error");   //登录失败的页面
	        http.authorizeRequests().antMatchers("/static/**").permitAll();    //文件下的所有都能访问
	        http.authorizeRequests().antMatchers("/webjars/**").permitAll();
	        http.authorizeRequests().antMatchers("/login1/**").permitAll();
	        http.authorizeRequests().antMatchers("/login/**").permitAll();
	        http.authorizeRequests().antMatchers("/username").permitAll();
	        http.authorizeRequests().antMatchers("/str").permitAll();
	        http.authorizeRequests().antMatchers("/findNoticeById").permitAll();
			http.authorizeRequests().antMatchers("/question").permitAll();
	        http.logout().logoutUrl("/logout").permitAll();     //退出
	        http.authorizeRequests().anyRequest().authenticated();    //其他请求都需要认证
	}
	
}
