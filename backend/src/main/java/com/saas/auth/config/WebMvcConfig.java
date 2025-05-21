package com.saas.auth.config;

import com.saas.auth.interceptor.TenantInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private com.saas.auth.interceptor.TenantInterceptor jwtTenantInterceptor;
    
    @Autowired
    private com.saas.auth.config.TenantInterceptor headerTenantInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 添加JWT租户拦截器，从JWT令牌中提取租户ID
        registry.addInterceptor(jwtTenantInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/auth/login", "/auth/register", "/h2-console/**");
        
        // 添加请求头租户拦截器，从请求头中提取租户ID
        registry.addInterceptor(headerTenantInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/auth/login", "/auth/register", "/h2-console/**");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .exposedHeaders("Authorization")
                .maxAge(3600);
    }
}