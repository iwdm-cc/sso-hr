package com.saas.auth.sso;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import cn.dev33.satoken.config.SaSsoConfig;
import cn.dev33.satoken.sso.SaSsoProcessor;
import cn.dev33.satoken.sso.SaSsoHandle;

/**
 * Sa-Token SSO 服务端配置
 */
@Configuration
public class SsoServerConfig implements WebMvcConfigurer {
    
    /**
     * 配置SSO相关参数
     */
    @Bean
    public SaSsoConfig ssoConfig() {
        SaSsoConfig config = SaSsoProcessor.instance.getConfig();
        // 配置：允许的授权回调地址
        config.setAllowUrl("http://localhost:9001/client/sso/login")
            // 打开单点注销功能
            .setIsSlo(true)
            // 配置SSO认证秘钥
            .setSecretkey("kSMamcEH24EGI39xfcNJOFXl2hsL9bLVt");
        return config;
    }
}