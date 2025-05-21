package com.saas.client.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import cn.dev33.satoken.stp.StpUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * SSO客户端控制器
 */
@Slf4j
@RestController
public class ClientController {

    @Value("${sa-token.sso.auth-url}")
    private String ssoServerUrl;
    
    private final RestTemplate restTemplate = new RestTemplate();
    
    // 保存登录状态和用户信息的简单Map（实际使用中应该使用更强大的方案）
    private Map<String, Object> tokenUserMap = new HashMap<>();

    /**
     * 首页
     */
    @RequestMapping("/")
    public ModelAndView index() {
        ModelAndView view = new ModelAndView("index.html");
        // 检查session中是否有登录状态
        boolean isLogin = tokenUserMap.size() > 0 || StpUtil.isLogin();
        view.addObject("isLogin", isLogin);
        
        if (isLogin) {
            // 如果使用sa-token登录
            if (StpUtil.isLogin()) {
                view.addObject("userId", StpUtil.getLoginIdAsString());
            } 
            // 如果使用自定义token登录
            else if (tokenUserMap.containsKey("userId")) {
                view.addObject("userId", tokenUserMap.get("userId"));
            }
        }
        return view;
    }

    /**
     * 受保护的资源页面，需要登录才能访问
     */
    @RequestMapping("/protected")
    public ModelAndView protectedResource() {
        // 检查是否已登录
        boolean isLogin = tokenUserMap.size() > 0 || StpUtil.isLogin();
        if (!isLogin) {
            return new ModelAndView("redirect:/sso/login");
        }
        
        ModelAndView view = new ModelAndView("protected.html");
        
        // 设置用户信息
        if (StpUtil.isLogin()) {
            view.addObject("userId", StpUtil.getLoginIdAsString());
            view.addObject("token", StpUtil.getTokenValue());
        } else if (tokenUserMap.containsKey("userId")) {
            view.addObject("userId", tokenUserMap.get("userId"));
            view.addObject("token", tokenUserMap.get("token"));
        }
        
        return view;
    }

    /*
     * SSO相关接口
     */
    
    /**
     * SSO登录
     */
    @GetMapping("/sso/login")
    public RedirectView ssoLogin() {
        // 如果已经登录，直接返回首页
        if (StpUtil.isLogin() || tokenUserMap.size() > 0) {
            return new RedirectView("/client/");
        }
        
        // 跳转到SSO认证中心
        String redirectUrl = ssoServerUrl + "?redirect=" + "http://localhost:9001/client/sso/callback";
        return new RedirectView(redirectUrl);
    }
    
    /**
     * SSO回调处理
     */
    @GetMapping("/sso/callback")
    public RedirectView ssoCallback(@RequestParam(required = false) String token, 
                                  @RequestParam(required = false) String userId) {
        log.info("收到SSO回调，token: {}, userId: {}", token, userId);
        
        if (token != null && !token.isEmpty()) {
            // 保存令牌和用户ID
            tokenUserMap.put("token", token);
            tokenUserMap.put("userId", userId);
            
            // 登录成功，重定向到首页
            return new RedirectView("/client/");
        }
        
        // 登录失败，可以添加错误处理逻辑
        return new RedirectView("/client/?loginError=true");
    }
    
    /**
     * SSO登出
     */
    @GetMapping("/sso/logout")
    public RedirectView ssoLogout() {
        // 清除本地登录状态
        if (StpUtil.isLogin()) {
            StpUtil.logout();
        }
        
        // 清除自定义token存储
        tokenUserMap.clear();
        
        // 跳转到SSO服务器注销
        String redirectUrl = "http://localhost:8000/api/sso/logout?redirect=http://localhost:9001/client/";
        return new RedirectView(redirectUrl);
    }
}