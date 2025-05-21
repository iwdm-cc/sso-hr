package com.saas.permission.service.impl;

import com.saas.permission.context.TenantContext;
import com.saas.permission.dto.LoginDTO;
import com.saas.permission.dto.LoginResponseDTO;
import com.saas.permission.entity.User;
import com.saas.permission.exception.BusinessException;
import com.saas.permission.service.AuthService;
import com.saas.permission.service.UserService;
import com.saas.permission.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 认证服务实现类
 */
@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 用户登录
     */
    @Override
    public LoginResponseDTO login(LoginDTO loginDTO) {
        // 设置租户上下文
        TenantContext.setCurrentTenant(loginDTO.getTenantId());
        
        try {
            // 验证用户名和密码
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDTO.getUsername(), loginDTO.getPassword())
            );
            
            // 设置认证信息
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            // 生成JWT令牌
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String token = jwtUtil.generateToken(userDetails.getUsername(), loginDTO.getTenantId());
            
            // 获取用户信息
            User user = userService.getUserByUsername(loginDTO.getUsername());
            
            // 更新最后登录时间
            user.setLastLoginTime(new Date());
            userService.updateById(user);
            
            // 返回登录响应
            return new LoginResponseDTO(token, user);
        } catch (BadCredentialsException e) {
            throw new BusinessException("用户名或密码错误");
        } finally {
            // 清除租户上下文
            TenantContext.clear();
        }
    }

    /**
     * 用户登出
     */
    @Override
    public void logout() {
        // 清除认证信息
        SecurityContextHolder.clearContext();
    }

    /**
     * 刷新Token
     */
    @Override
    public String refreshToken() {
        // 获取当前用户信息
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new BusinessException("用户未登录");
        }
        
        String username = authentication.getName();
        Long tenantId = TenantContext.getCurrentTenant();
        
        // 生成新的令牌
        return jwtUtil.generateToken(username, tenantId);
    }

    /**
     * 修改密码
     */
    @Override
    public void changePassword(String oldPassword, String newPassword) {
        // 获取当前用户
        User user = userService.getCurrentUser();
        
        // 验证旧密码
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BusinessException("旧密码不正确");
        }
        
        // 修改密码
        user.setPassword(passwordEncoder.encode(newPassword));
        userService.updateById(user);
    }
}
