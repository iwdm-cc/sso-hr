package com.saas.permission.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.saas.permission.context.TenantContext;
import com.saas.permission.dto.UserDTO;
import com.saas.permission.entity.Permission;
import com.saas.permission.entity.Role;
import com.saas.permission.entity.User;
import com.saas.permission.entity.UserRole;
import com.saas.permission.exception.BusinessException;
import com.saas.permission.mapper.UserMapper;
import com.saas.permission.mapper.UserRoleMapper;
import com.saas.permission.service.PermissionService;
import com.saas.permission.service.RoleService;
import com.saas.permission.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 用户服务实现类
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Autowired
    private RoleService roleService;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * 获取当前用户信息
     */
    @Override
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return getUserByUsername(username);
    }

    /**
     * 分页获取用户列表
     */
    @Override
    public IPage<User> getUsersByPage(String username, String name, Integer status, Integer current, Integer size) {
        Page<User> page = new Page<>(current, size);
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        
        // 设置租户条件
        Long tenantId = TenantContext.getCurrentTenant();
        wrapper.eq(User::getTenantId, tenantId);
        
        // 设置查询条件
        wrapper.like(StringUtils.isNotBlank(username), User::getUsername, username)
               .like(StringUtils.isNotBlank(name), User::getName, name)
               .eq(status != null, User::getStatus, status)
               .orderByDesc(User::getCreateTime);
        
        return userMapper.selectPage(page, wrapper);
    }

    /**
     * 根据ID获取用户
     */
    @Override
    public User getUserById(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        // 检查租户
        Long tenantId = TenantContext.getCurrentTenant();
        if (!user.getTenantId().equals(tenantId)) {
            throw new BusinessException("无权访问该用户");
        }
        
        return user;
    }

    /**
     * 创建用户
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public User createUser(UserDTO userDTO) {
        // 检查用户名是否存在
        User existUser = getUserByUsername(userDTO.getUsername());
        if (existUser != null) {
            throw new BusinessException("用户名已存在");
        }
        
        // 创建用户对象
        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setName(userDTO.getName());
        user.setEmail(userDTO.getEmail());
        user.setPhone(userDTO.getPhone());
        user.setStatus(userDTO.getStatus());
        user.setTenantId(TenantContext.getCurrentTenant());
        
        // 保存用户
        userMapper.insert(user);
        
        // 分配角色
        if (userDTO.getRoleIds() != null && !userDTO.getRoleIds().isEmpty()) {
            assignRoles(user.getId(), userDTO.getRoleIds());
        }
        
        return user;
    }

    /**
     * 更新用户
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public User updateUser(Long id, UserDTO userDTO) {
        // 获取用户
        User user = getUserById(id);
        
        // 检查用户名是否存在
        if (!user.getUsername().equals(userDTO.getUsername())) {
            User existUser = getUserByUsername(userDTO.getUsername());
            if (existUser != null) {
                throw new BusinessException("用户名已存在");
            }
        }
        
        // 更新用户信息
        user.setUsername(userDTO.getUsername());
        if (StringUtils.isNotBlank(userDTO.getPassword())) {
            user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        }
        user.setName(userDTO.getName());
        user.setEmail(userDTO.getEmail());
        user.setPhone(userDTO.getPhone());
        user.setStatus(userDTO.getStatus());
        
        // 保存用户
        userMapper.updateById(user);
        
        // 更新角色
        if (userDTO.getRoleIds() != null) {
            assignRoles(id, userDTO.getRoleIds());
        }
        
        return user;
    }

    /**
     * 删除用户
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteUser(Long id) {
        // 获取用户
        User user = getUserById(id);
        
        // 如果是管理员用户，不允许删除
        if ("admin".equals(user.getUsername())) {
            throw new BusinessException("不能删除管理员用户");
        }
        
        // 删除用户角色关联
        LambdaQueryWrapper<UserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserRole::getUserId, id);
        userRoleMapper.delete(wrapper);
        
        // 删除用户
        userMapper.deleteById(id);
    }

    /**
     * 获取用户角色
     */
    @Override
    public List<Map<String, Object>> getUserRoles(Long userId) {
        // 获取用户
        getUserById(userId);
        
        // 获取用户角色
        List<Role> roles = roleService.getRolesByUserId(userId);
        
        // 构建结果
        return roles.stream().map(role -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", role.getId());
            map.put("name", role.getName());
            map.put("description", role.getDescription());
            return map;
        }).collect(Collectors.toList());
    }

    /**
     * 分配用户角色
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignRoles(Long userId, List<Long> roleIds) {
        // 获取用户
        getUserById(userId);
        
        // 删除旧的角色关联
        LambdaQueryWrapper<UserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserRole::getUserId, userId);
        userRoleMapper.delete(wrapper);
        
        // 添加新的角色关联
        if (roleIds != null && !roleIds.isEmpty()) {
            Long tenantId = TenantContext.getCurrentTenant();
            for (Long roleId : roleIds) {
                // 检查角色是否存在且属于当前租户
                Role role = roleService.getRoleById(roleId);
                if (role == null || !role.getTenantId().equals(tenantId)) {
                    throw new BusinessException("角色不存在或无权访问");
                }
                
                UserRole userRole = new UserRole();
                userRole.setUserId(userId);
                userRole.setRoleId(roleId);
                userRole.setTenantId(tenantId);
                userRoleMapper.insert(userRole);
            }
        }
    }

    /**
     * 根据用户名获取用户
     */
    @Override
    public User getUserByUsername(String username) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        
        // 添加租户条件
        Long tenantId = TenantContext.getCurrentTenant();
        if (tenantId != null) {
            wrapper.eq(User::getTenantId, tenantId);
        }
        
        return userMapper.selectOne(wrapper);
    }

    /**
     * 获取所有用户（不分页）
     */
    @Override
    public List<User> getAllUsers() {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getTenantId, TenantContext.getCurrentTenant())
               .orderByDesc(User::getCreateTime);
        return userMapper.selectList(wrapper);
    }

    /**
     * 查询用户权限码列表
     */
    @Override
    public List<String> getUserPermissionCodes(Long userId) {
        // 获取用户角色
        List<Role> roles = roleService.getRolesByUserId(userId);
        if (roles.isEmpty()) {
            return Collections.emptyList();
        }
        
        // 获取角色权限
        List<Long> roleIds = roles.stream().map(Role::getId).collect(Collectors.toList());
        List<Permission> permissions = permissionService.getPermissionsByRoleIds(roleIds);
        
        // 提取权限代码
        return permissions.stream()
                .map(Permission::getCode)
                .distinct()
                .collect(Collectors.toList());
    }
}
