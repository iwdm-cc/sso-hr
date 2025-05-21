package com.saas.auth.sso;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.saas.auth.common.ApiResponse;
import com.saas.auth.context.TenantContext;
import com.saas.auth.dto.UserDTO;
import com.saas.auth.entity.User;
import com.saas.auth.mapper.UserMapper;
import com.saas.auth.service.UserService;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.sso.SaSsoHandle;
import cn.dev33.satoken.sso.SaSsoProcessor;
import cn.dev33.satoken.sso.SaSsoUtil;
import cn.dev33.satoken.stp.StpUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * SSO单点登录服务端控制器
 */
@Slf4j
@RestController
@RequestMapping("/sso")
public class SsoServerController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private UserMapper userMapper;
    
    /**
     * SSO-Server端：处理所有SSO相关请求
     */
    @RequestMapping("/*")
    public Object ssoRequest() {
        // 使用自定义登录页面，这里我们通过返回重定向来处理
        // 检查是否是认证请求
        if (SaHolder.getRequest().getParam("mode") != null && SaHolder.getRequest().getParam("mode").equals("simple")) {
            // 重定向到自定义登录页
            String loginUrl = "/api/sso/sso-login.html" 
                + "?back=" + SaHolder.getRequest().getParam("back")
                + "&type=" + SaHolder.getRequest().getParam("type");
            SaHolder.getResponse().redirect(loginUrl);
            return null;
        }
        
        return SaSsoHandle.serverRequest();
    }
    
    /**
     * 配置SSO登录回调逻辑
     */
    @RequestMapping(value = "/doLogin", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ApiResponse doLogin(String username, String password) {
        try {
            // 设置租户上下文，使用系统默认租户进行认证
            TenantContext.setTenantId(1L);
            
            // 校验用户名和密码
            User user = userMapper.selectByUsernameAndTenantId(username, TenantContext.getTenantId());
            if (user == null) {
                return ApiResponse.error("用户不存在");
            }
            
            // 此处简化了密码验证，实际应用中应该进行加密比对
            // 目前为了测试方便，暂时简化验证过程
            if ("123456".equals(password)) {
                // 登录成功，使用 user id 作为登录标识
                StpUtil.login(user.getId());
                
                // 在登录成功后，如果是通过SSO认证而来的，则要重定向回客户端应用
                String back = SaHolder.getRequest().getParam("back");
                if(back != null && !back.isEmpty()) {
                    // 获取ticket并返回给前端用于跳转
                    String ticket = SaSsoUtil.createTicket(user.getId().toString());
                    return ApiResponse.success(ticket);
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
    @RequestMapping("/userinfo")
    public Object userinfo(String loginId) {
        try {
            Long userId = Long.valueOf(loginId);
            User user = userMapper.selectById(userId);
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
}