package com.saas.auth.sso;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Sa-Token SSO 服务端配置
 */
@Configuration
public class SsoServerConfig implements WebMvcConfigurer {
    
    /**
     * 配置SSO相关参数
     * 注意：由于当前API兼容性问题，我们在启动时进行配置
     */
    @Bean
    public void configSso() {
        // 我们将在SsoServerController中直接处理SSO逻辑
        // 不使用Sa-Token的内置配置，以避免API兼容性问题
    }
}