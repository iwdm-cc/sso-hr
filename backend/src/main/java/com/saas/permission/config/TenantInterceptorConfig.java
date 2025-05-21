package com.saas.permission.config;

import com.saas.permission.interceptor.TenantInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 租户拦截器配置类
 */
@Configuration
public class TenantInterceptorConfig implements WebMvcConfigurer {

    @Autowired
    private TenantInterceptor tenantInterceptor;

    /**
     * 添加租户拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(tenantInterceptor)
                // 拦截所有请求
                .addPathPatterns("/**")
                // 不拦截认证相关接口和Swagger相关接口
                .excludePathPatterns(
                        "/api/auth/**",
                        "/swagger-ui.html",
                        "/swagger-resources/**",
                        "/v2/api-docs",
                        "/webjars/**"
                );
    }
}
