package com.activiti7.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;


@Configuration
public class ActivitiSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private LoginSuccessHandler loginSuccessHandler;
    @Autowired
    private LoginFailureHandler loginFailureHandler;


    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http
                .formLogin()
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .successHandler(loginSuccessHandler)
                .failureHandler(loginFailureHandler)
                //.defaultSuccessUrl("/hello")
                //.successForwardUrl("success.html")
                //.failureForwardUrl("failure.html")
                .and()
                .authorizeRequests()
                .antMatchers("/login", "/demo-login.html", "/demo-login1.html", "/layuimini/page/login-1.html").permitAll()
                .anyRequest()
                .permitAll()
                .and().logout().permitAll()
                .and().csrf().disable()
                .headers().frameOptions().disable();//全部页面不验证

    }


}
