package com.saas.auth.security;

import com.saas.auth.entity.User;
import com.saas.auth.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * UserDetailsService实现
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 从请求上下文中获取租户ID，如果没有则默认为1（系统管理租户）
        Long tenantId = 1L;
        
        // 根据用户名和租户ID查询用户
        User user = userMapper.selectByUsernameAndTenantId(username, tenantId);
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在");
        }
        
        // 查询用户权限列表
        List<String> permissions = userMapper.selectUserPermissions(user.getId(), tenantId);
        
        // 转换为Spring Security的权限对象
        List<SimpleGrantedAuthority> authorities = permissions.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
        
        // 返回UserDetails对象
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.getStatus() == 1, // enabled
                true, // accountNonExpired
                true, // credentialsNonExpired
                true, // accountNonLocked
                authorities
        );
    }
}