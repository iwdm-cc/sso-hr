package com.saas.auth.sso;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * SSO 服务端配置
 */
@Configuration
public class SsoServerConfig implements WebMvcConfigurer {
    // 使用简化配置，直接在控制器中实现SSO逻辑
}