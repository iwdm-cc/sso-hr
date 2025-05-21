package com.saas.auth.security;

import com.saas.auth.dto.UserDTO;
import com.saas.auth.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户详情服务实现类
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 从当前上下文中获取租户ID
        Long tenantId = TenantContext.getCurrentTenant();
        if (tenantId == null) {
            throw new UsernameNotFoundException("租户ID不能为空");
        }

        // 根据用户名和租户ID查询用户
        UserDTO userDTO = userService.getUserByUsername(username, tenantId);
        if (userDTO == null) {
            throw new UsernameNotFoundException("用户名或密码错误");
        }

        // 获取用户权限
        List<String> permissions = userService.getUserPermissions(userDTO.getId());
        
        // 将权限字符串转换为GrantedAuthority对象
        List<GrantedAuthority> authorities = permissions.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        // 返回UserDetails对象
        return new User(
                userDTO.getUsername(),
                userDTO.getPassword(),
                userDTO.getStatus(), // 是否启用
                true, // 账号未过期
                true, // 凭证未过期
                true, // 账号未锁定
                authorities
        );
    }
}
