package com.saas.auth.sso;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.saas.auth.common.ApiResponse;
import com.saas.auth.context.TenantContext;
import com.saas.auth.entity.User;
import com.saas.auth.mapper.UserMapper;
import com.saas.auth.service.UserService;
import com.saas.auth.util.JwtUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * SSO单点登录服务端控制器
 * 简化实现版本，主要功能包括：
 * 1. 提供登录认证
 * 2. 提供用户信息查询
 * 3. 提供单点登出
 */
@Slf4j
@RestController
@RequestMapping("/sso")
public class SsoServerController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    /**
     * SSO-Server端：处理认证请求
     */
    @GetMapping("/auth")
    public ModelAndView auth(@RequestParam(required = false) String redirect) {
        // 重定向到登录页面
        String loginUrl = "/api/sso/sso-login.html";
        if (redirect != null && !redirect.isEmpty()) {
            loginUrl += "?back=" + redirect;
        }
        
        return new ModelAndView("redirect:" + loginUrl);
    }
    
    /**
     * 处理登录请求
     */
    @PostMapping(value = "/doLogin", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ApiResponse doLogin(@RequestParam String username, @RequestParam String password,
                              @RequestParam(required = false) String back) {
        try {
            // 设置租户上下文，使用系统默认租户进行认证
            TenantContext.setTenantId(1L);
            
            // 校验用户名和密码
            User user = userMapper.selectByUsernameAndTenantId(username, TenantContext.getTenantId());
            if (user == null) {
                return ApiResponse.error("用户不存在");
            }
            
            // 此处简化了密码验证，实际应用中应该进行加密比对
            if ("123456".equals(password)) {
                // 生成JWT令牌
                String token = jwtUtil.generateToken(user.getUsername(), user.getTenantId());
                
                // 如果有回调地址，返回令牌用于SSO重定向
                if (back != null && !back.isEmpty()) {
                    return ApiResponse.success(token);
                }
                
                return ApiResponse.success("登录成功");
            }
            
            return ApiResponse.error("密码错误");
        } catch (Exception e) {
            log.error("SSO登录失败", e);
            return ApiResponse.error("登录失败: " + e.getMessage());
        }
    }
    
    /**
     * 查询用户信息
     */
    @GetMapping("/userinfo")
    public ApiResponse userinfo(@RequestParam String token) {
        try {
            // 验证JWT令牌
            String username = jwtUtil.getUsernameFromToken(token);
            Long tenantId = jwtUtil.getTenantIdFromToken(token);
            
            if (username == null || tenantId == null) {
                return ApiResponse.error("无效的令牌");
            }
            
            User user = userMapper.selectByUsernameAndTenantId(username, tenantId);
            if (user == null) {
                return ApiResponse.error("用户不存在");
            }
            
            // 返回用户信息给客户端
            return ApiResponse.success(user);
        } catch (Exception e) {
            log.error("SSO获取用户信息失败", e);
            return ApiResponse.error("获取用户信息失败: " + e.getMessage());
        }
    }
    
    /**
     * 单点登出
     */
    @GetMapping("/logout")
    public ModelAndView logout(@RequestParam(required = false) String redirect) {
        // 如果有回调地址，跳转回去
        if (redirect != null && !redirect.isEmpty()) {
            return new ModelAndView("redirect:" + redirect);
        }
        
        // 否则跳转到登录页
        return new ModelAndView("redirect:/api/sso/sso-login.html");
    }
}