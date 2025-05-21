package com.saas.auth.interceptor;

import com.saas.auth.context.TenantContext;
import com.saas.auth.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 租户拦截器，用于设置当前请求的租户ID
 */
@Component
public class TenantInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String header = request.getHeader("Authorization");
        
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            try {
                Long tenantId = jwtUtil.getTenantIdFromToken(token);
                TenantContext.setTenantId(tenantId);
            } catch (Exception e) {
                // 如果解析失败，不设置租户ID
            }
        }
        
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // 请求完成后清除租户上下文
        TenantContext.clear();
    }
}