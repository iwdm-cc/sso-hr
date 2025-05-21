package com.saas.auth.controller;

import com.saas.auth.common.ApiResponse;
import com.saas.auth.dto.LoginRequest;
import com.saas.auth.dto.LoginResponse;
import com.saas.auth.security.JwtTokenProvider;
import com.saas.auth.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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

/**
 * 认证控制器
 */
@RestController
@RequestMapping("/api/auth")
@Tag(name = "认证管理", description = "用户登录认证相关操作")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    @Operation(summary = "用户登录")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        // 设置租户上下文
        userService.setCurrentTenant(loginRequest.getTenantId());
        
        // 认证
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 生成token
        String token = jwtTokenProvider.createToken(authentication, loginRequest.getTenantId());

        return ApiResponse.success(new LoginResponse(token));
    }

    @PostMapping("/logout")
    @Operation(summary = "用户登出")
    public ApiResponse<Void> logout() {
        // JWT是无状态的，服务端不需要做特殊处理
        // 客户端需要删除本地存储的token
        return ApiResponse.success();
    }
}
