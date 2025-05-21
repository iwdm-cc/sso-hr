package com.saas.auth.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

/**
 * Sa-Token 与 Spring Security 整合配置
 */
@Configuration
public class SaTokenSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // 允许所有请求通过，后续使用Sa-Token进行权限控制
        http
            .csrf().disable() 
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
            .authorizeRequests()
            .antMatchers("/sso/**").permitAll() // 确保SSO相关路径可以被访问
            .antMatchers("/auth/**").permitAll() // 保留原有的权限
            .antMatchers("/h2-console/**").permitAll()
            .antMatchers("/swagger-ui/**").permitAll()
            .antMatchers("/swagger-resources/**").permitAll()
            .antMatchers("/v3/api-docs/**").permitAll()
            .antMatchers("/v2/api-docs/**").permitAll()
            .antMatchers("/**", "OPTIONS").permitAll()
            // 以下是允许访问的主要功能模块
            .antMatchers("/user/**").permitAll()
            .antMatchers("/role/**").permitAll()
            .antMatchers("/permission/**").permitAll()
            .antMatchers("/tenant/**").permitAll()
            .anyRequest().permitAll(); // 允许所有请求，通过Sa-Token自行控制
    }
}