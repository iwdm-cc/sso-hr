package com.saas.auth.controller;

import com.saas.auth.common.ApiResponse;
import com.saas.auth.dto.LoginRequest;
import com.saas.auth.dto.LoginResponse;
import com.saas.auth.entity.Tenant;
import com.saas.auth.entity.User;
import com.saas.auth.mapper.TenantMapper;
import com.saas.auth.mapper.UserMapper;
import com.saas.auth.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * 认证控制器
 */
@RestController
@RequestMapping("/auth")
public class AuthController {
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private TenantMapper tenantMapper;
    
    /**
     * 用户登录
     */
    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        // 1. 查询租户信息
        Tenant tenant = tenantMapper.selectByCode(loginRequest.getTenantCode());
        if (tenant == null || tenant.getStatus() != 1) {
            return ApiResponse.error("租户不存在或已被禁用");
        }
        
        // 2. 进行认证
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        // 3. 查询用户信息
        User user = userMapper.selectByUsernameAndTenantId(loginRequest.getUsername(), tenant.getId());
        
        // 4. 生成JWT令牌
        String token = jwtUtil.generateToken((org.springframework.security.core.userdetails.UserDetails)authentication.getPrincipal(), tenant.getId());
        
        // 5. 查询用户权限
        List<String> permissions = userMapper.selectUserPermissions(user.getId(), tenant.getId());
        
        // 6. 构建返回结果
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setUserId(user.getId());
        loginResponse.setUsername(user.getUsername());
        loginResponse.setName(user.getName());
        loginResponse.setAvatar(user.getAvatar());
        loginResponse.setTenantId(tenant.getId());
        loginResponse.setTenantName(tenant.getName());
        loginResponse.setPermissions(permissions);
        loginResponse.setToken(token);
        
        return ApiResponse.success(loginResponse);
    }
}