package com.saas.permission.controller;

import com.saas.permission.dto.LoginDTO;
import com.saas.permission.dto.LoginResponseDTO;
import com.saas.permission.service.AuthService;
import com.saas.permission.util.ResponseUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

/**
 * 认证控制器
 */
@Api(tags = "认证管理")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * 登录
     */
    @ApiOperation("用户登录")
    @PostMapping("/login")
    public Map<String, Object> login(@Valid @RequestBody LoginDTO loginDTO) {
        LoginResponseDTO response = authService.login(loginDTO);
        return ResponseUtil.success(response);
    }

    /**
     * 登出
     */
    @ApiOperation("用户登出")
    @PostMapping("/logout")
    public Map<String, Object> logout() {
        authService.logout();
        return ResponseUtil.success();
    }

    /**
     * 刷新Token
     */
    @ApiOperation("刷新Token")
    @PostMapping("/refresh")
    public Map<String, Object> refreshToken() {
        String newToken = authService.refreshToken();
        return ResponseUtil.success(newToken);
    }

    /**
     * 修改密码
     */
    @ApiOperation("修改密码")
    @PostMapping("/password")
    public Map<String, Object> changePassword(
            @RequestParam String oldPassword,
            @RequestParam String newPassword) {
        authService.changePassword(oldPassword, newPassword);
        return ResponseUtil.success();
    }
}
