package com.saas.client.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import cn.dev33.satoken.sso.SaSsoHandle;
import cn.dev33.satoken.stp.StpUtil;

/**
 * SSO客户端控制器
 */
@RestController
public class ClientController {

    /**
     * 首页
     */
    @RequestMapping("/")
    public ModelAndView index() {
        ModelAndView view = new ModelAndView("index.html");
        view.addObject("isLogin", StpUtil.isLogin());
        if (StpUtil.isLogin()) {
            // 用户已登录，可以获取用户信息
            view.addObject("userId", StpUtil.getLoginIdAsString());
        }
        return view;
    }

    /**
     * 受保护的资源页面，需要登录才能访问
     */
    @RequestMapping("/protected")
    public ModelAndView protectedResource() {
        // 检查是否已登录
        if (!StpUtil.isLogin()) {
            return new ModelAndView("redirect:/sso/login");
        }
        
        ModelAndView view = new ModelAndView("protected.html");
        view.addObject("userId", StpUtil.getLoginIdAsString());
        view.addObject("token", StpUtil.getTokenValue());
        
        return view;
    }

    /*
     * SSO相关接口
     */
    
    /**
     * 配置SSO相关路由
     */
    @RequestMapping("/sso/*")
    public Object ssoRequest() {
        return SaSsoHandle.clientRequest();
    }
}