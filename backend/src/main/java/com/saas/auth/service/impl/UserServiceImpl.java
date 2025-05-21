package com.saas.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.saas.auth.dto.RoleDTO;
import com.saas.auth.dto.TenantDTO;
import com.saas.auth.dto.UserDTO;
import com.saas.auth.entity.Tenant;
import com.saas.auth.entity.User;
import com.saas.auth.entity.UserRole;
import com.saas.auth.exception.CustomException;
import com.saas.auth.mapper.TenantMapper;
import com.saas.auth.mapper.UserMapper;
import com.saas.auth.mapper.UserRoleMapper;
import com.saas.auth.security.TenantContext;
import com.saas.auth.service.RoleService;
import com.saas.auth.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 用户服务实现类
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Autowired
    private TenantMapper tenantMapper;

    @Autowired
    private RoleService roleService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDTO getUserByUsername(String username, Long tenantId) {
        User user = userMapper.selectByUsernameAndTenantId(username, tenantId);
        if (user == null) {
            return null;
        }
        UserDTO userDTO = new UserDTO();
        BeanUtils.copyProperties(user, userDTO);
        return userDTO;
    }

    @Override
    public Map<String, Object> getUserList(int page, int pageSize, String keyword, Boolean status) {
        Page<User> pageParam = new Page<>(page, pageSize);
        Long tenantId = TenantContext.getCurrentTenant();
        Integer statusValue = (status != null) ? (status ? 1 : 0) : null;
        IPage<User> pageResult = userMapper.selectUserPage(pageParam, keyword, statusValue, tenantId);
        
        List<UserDTO> userDTOs = pageResult.getRecords().stream().map(user -> {
            UserDTO userDTO = new UserDTO();
            BeanUtils.copyProperties(user, userDTO);
            return userDTO;
        }).collect(Collectors.toList());
        
        Map<String, Object> result = new HashMap<>();
        result.put("list", userDTOs);
        result.put("total", pageResult.getTotal());
        return result;
    }

    @Override
    public UserDTO getUserById(Long id) {
        User user = userMapper.selectById(id);
        if (user == null || !user.getTenantId().equals(TenantContext.getCurrentTenant())) {
            throw new CustomException("用户不存在");
        }
        
        UserDTO userDTO = new UserDTO();
        BeanUtils.copyProperties(user, userDTO);
        
        // 获取用户角色ID列表
        List<Long> roleIds = roleService.getUserRoles(id).stream()
                .map(RoleDTO::getId)
                .collect(Collectors.toList());
        userDTO.setRoleIds(roleIds);
        
        return userDTO;
    }

    @Override
    @Transactional
    public void createUser(UserDTO userDTO) {
        // 检查用户名是否已存在
        User existUser = userMapper.selectByUsernameAndTenantId(userDTO.getUsername(), TenantContext.getCurrentTenant());
        if (existUser != null) {
            throw new CustomException("用户名已存在");
        }
        
        User user = new User();
        BeanUtils.copyProperties(userDTO, user);
        
        // 加密密码
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        
        // 设置租户ID
        user.setTenantId(TenantContext.getCurrentTenant());
        
        userMapper.insert(user);
        
        // 分配角色
        if (userDTO.getRoleIds() != null && !userDTO.getRoleIds().isEmpty()) {
            assignRoles(user.getId(), userDTO.getRoleIds());
        }
    }

    @Override
    @Transactional
    public void updateUser(UserDTO userDTO) {
        User user = userMapper.selectById(userDTO.getId());
        if (user == null || !user.getTenantId().equals(TenantContext.getCurrentTenant())) {
            throw new CustomException("用户不存在");
        }
        
        // 检查用户名是否已存在（排除自身）
        User existUser = userMapper.selectByUsernameAndTenantId(userDTO.getUsername(), TenantContext.getCurrentTenant());
        if (existUser != null && !existUser.getId().equals(userDTO.getId())) {
            throw new CustomException("用户名已存在");
        }
        
        BeanUtils.copyProperties(userDTO, user);
        
        // 如果密码不为空，则加密密码
        if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        }
        
        userMapper.updateById(user);
        
        // 分配角色
        if (userDTO.getRoleIds() != null) {
            assignRoles(user.getId(), userDTO.getRoleIds());
        }
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        User user = userMapper.selectById(id);
        if (user == null || !user.getTenantId().equals(TenantContext.getCurrentTenant())) {
            throw new CustomException("用户不存在");
        }
        
        // 删除用户角色关联
        QueryWrapper<UserRole> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", id);
        userRoleMapper.delete(wrapper);
        
        // 删除用户
        userMapper.deleteById(id);
    }

    @Override
    @Transactional
    public void assignRoles(Long userId, List<Long> roleIds) {
        User user = userMapper.selectById(userId);
        if (user == null || !user.getTenantId().equals(TenantContext.getCurrentTenant())) {
            throw new CustomException("用户不存在");
        }
        
        // 删除用户角色关联
        QueryWrapper<UserRole> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        userRoleMapper.delete(wrapper);
        
        // 添加新的角色关联
        if (roleIds != null && !roleIds.isEmpty()) {
            for (Long roleId : roleIds) {
                UserRole userRole = new UserRole();
                userRole.setUserId(userId);
                userRole.setRoleId(roleId);
                userRole.setTenantId(TenantContext.getCurrentTenant());
                userRoleMapper.insert(userRole);
            }
        }
    }

    @Override
    public Map<String, Object> getCurrentUserInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Long tenantId = TenantContext.getCurrentTenant();
        
        User user = userMapper.selectByUsernameAndTenantId(username, tenantId);
        if (user == null) {
            throw new CustomException("用户不存在");
        }
        
        UserDTO userDTO = new UserDTO();
        BeanUtils.copyProperties(user, userDTO);
        userDTO.setPassword(null); // 不返回密码
        
        // 获取租户信息
        Tenant tenant = tenantMapper.selectById(tenantId);
        TenantDTO tenantDTO = new TenantDTO();
        if (tenant != null) {
            BeanUtils.copyProperties(tenant, tenantDTO);
        }
        
        // 获取用户权限
        List<String> permissions = getUserPermissions(user.getId());
        
        Map<String, Object> result = new HashMap<>();
        result.put("user", userDTO);
        result.put("tenant", tenantDTO);
        result.put("permissions", permissions);
        
        return result;
    }

    @Override
    public List<String> getUserPermissions(Long userId) {
        return userMapper.selectUserPermissions(userId, TenantContext.getCurrentTenant());
    }

    @Override
    public void setCurrentTenant(Long tenantId) {
        TenantContext.setCurrentTenant(tenantId);
    }
}
