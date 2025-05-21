package com.saas.permission.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.saas.permission.context.TenantContext;
import com.saas.permission.dto.TenantDTO;
import com.saas.permission.entity.*;
import com.saas.permission.exception.BusinessException;
import com.saas.permission.mapper.*;
import com.saas.permission.service.TenantService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 租户服务实现类
 */
@Service
public class TenantServiceImpl extends ServiceImpl<TenantMapper, Tenant> implements TenantService {

    @Autowired
    private TenantMapper tenantMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private PermissionMapper permissionMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Autowired
    private RolePermissionMapper rolePermissionMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * 分页获取租户列表
     */
    @Override
    public IPage<Tenant> getTenantsByPage(String name, Integer status, Integer current, Integer size) {
        Page<Tenant> page = new Page<>(current, size);
        LambdaQueryWrapper<Tenant> wrapper = new LambdaQueryWrapper<>();
        
        // 设置查询条件
        wrapper.like(StringUtils.isNotBlank(name), Tenant::getName, name)
               .eq(status != null, Tenant::getStatus, status)
               .orderByDesc(Tenant::getCreateTime);
        
        return tenantMapper.selectPage(page, wrapper);
    }

    /**
     * 根据ID获取租户
     */
    @Override
    public Tenant getTenantById(Long id) {
        Tenant tenant = tenantMapper.selectById(id);
        if (tenant == null) {
            throw new BusinessException("租户不存在");
        }
        return tenant;
    }

    /**
     * 创建租户
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Tenant createTenant(TenantDTO tenantDTO) {
        // 检查租户名称是否存在
        LambdaQueryWrapper<Tenant> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Tenant::getName, tenantDTO.getName());
        if (tenantMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("租户名称已存在");
        }
        
        // 创建租户对象
        Tenant tenant = new Tenant();
        tenant.setName(tenantDTO.getName());
        tenant.setDescription(tenantDTO.getDescription());
        tenant.setStatus(tenantDTO.getStatus());
        
        // 保存租户
        tenantMapper.insert(tenant);
        
        // 初始化租户数据
        initTenantData(tenant.getId());
        
        // 创建租户管理员
        createTenantAdmin(tenant.getId(), tenantDTO);
        
        return tenant;
    }

    /**
     * 更新租户
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Tenant updateTenant(Long id, TenantDTO tenantDTO) {
        // 获取租户
        Tenant tenant = getTenantById(id);
        
        // 检查租户名称是否存在
        if (!tenant.getName().equals(tenantDTO.getName())) {
            LambdaQueryWrapper<Tenant> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Tenant::getName, tenantDTO.getName());
            if (tenantMapper.selectCount(wrapper) > 0) {
                throw new BusinessException("租户名称已存在");
            }
        }
        
        // 更新租户信息
        tenant.setName(tenantDTO.getName());
        tenant.setDescription(tenantDTO.getDescription());
        tenant.setStatus(tenantDTO.getStatus());
        
        // 保存租户
        tenantMapper.updateById(tenant);
        
        return tenant;
    }

    /**
     * 删除租户
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteTenant(Long id) {
        // 获取租户
        Tenant tenant = getTenantById(id);
        
        // 删除租户数据
        // 删除用户角色关联
        LambdaQueryWrapper<UserRole> urWrapper = new LambdaQueryWrapper<>();
        urWrapper.eq(UserRole::getTenantId, id);
        userRoleMapper.delete(urWrapper);
        
        // 删除角色权限关联
        LambdaQueryWrapper<RolePermission> rpWrapper = new LambdaQueryWrapper<>();
        rpWrapper.eq(RolePermission::getTenantId, id);
        rolePermissionMapper.delete(rpWrapper);
        
        // 删除用户
        LambdaQueryWrapper<User> userWrapper = new LambdaQueryWrapper<>();
        userWrapper.eq(User::getTenantId, id);
        userMapper.delete(userWrapper);
        
        // 删除角色
        LambdaQueryWrapper<Role> roleWrapper = new LambdaQueryWrapper<>();
        roleWrapper.eq(Role::getTenantId, id);
        roleMapper.delete(roleWrapper);
        
        // 删除租户
        tenantMapper.deleteById(id);
    }

    /**
     * 获取所有租户（不分页）
     */
    @Override
    public List<Tenant> getAllTenants() {
        LambdaQueryWrapper<Tenant> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Tenant::getStatus, 1)  // 只返回启用的租户
               .orderByDesc(Tenant::getCreateTime);
        return tenantMapper.selectList(wrapper);
    }

    /**
     * 初始化租户数据
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void initTenantData(Long tenantId) {
        // 设置当前租户上下文
        TenantContext.setCurrentTenant(tenantId);
        
        // 初始化基本权限数据
        initPermissions();
        
        // 初始化基本角色数据
        initRoles(tenantId);
        
        // 清除租户上下文
        TenantContext.clear();
    }

    /**
     * 创建租户管理员
     */
    private void createTenantAdmin(Long tenantId, TenantDTO tenantDTO) {
        // 设置当前租户上下文
        TenantContext.setCurrentTenant(tenantId);
        
        try {
            // 检查用户名是否存在
            LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(User::getUsername, tenantDTO.getAdminUsername())
                   .eq(User::getTenantId, tenantId);
            if (userMapper.selectCount(wrapper) > 0) {
                throw new BusinessException("管理员用户名已存在");
            }
            
            // 创建管理员用户
            User user = new User();
            user.setUsername(tenantDTO.getAdminUsername());
            user.setPassword(passwordEncoder.encode(tenantDTO.getAdminPassword()));
            user.setName(tenantDTO.getAdminName());
            user.setEmail(tenantDTO.getAdminEmail());
            user.setStatus(1);
            user.setTenantId(tenantId);
            userMapper.insert(user);
            
            // 分配管理员角色
            LambdaQueryWrapper<Role> roleWrapper = new LambdaQueryWrapper<>();
            roleWrapper.eq(Role::getName, "admin")
                       .eq(Role::getTenantId, tenantId);
            Role adminRole = roleMapper.selectOne(roleWrapper);
            
            if (adminRole != null) {
                UserRole userRole = new UserRole();
                userRole.setUserId(user.getId());
                userRole.setRoleId(adminRole.getId());
                userRole.setTenantId(tenantId);
                userRoleMapper.insert(userRole);
            }
        } finally {
            // 清除租户上下文
            TenantContext.clear();
        }
    }

    /**
     * 初始化权限数据
     */
    private void initPermissions() {
        // 检查是否已经初始化
        if (permissionMapper.selectCount(null) > 0) {
            return;
        }
        
        // 创建基本权限数据
        List<Permission> permissions = new ArrayList<>();
        
        // 用户管理权限
        permissions.add(createPermission("用户管理", "user:view", "menu", "/user", 0L, "el-icon-user", 1));
        permissions.add(createPermission("创建用户", "user:create", "button", "/user/create", 1L, null, 1));
        permissions.add(createPermission("编辑用户", "user:update", "button", "/user/edit", 1L, null, 2));
        permissions.add(createPermission("删除用户", "user:delete", "button", "/user/delete", 1L, null, 3));
        permissions.add(createPermission("分配角色", "user:assign:role", "button", "/user/assign/role", 1L, null, 4));
        
        // 角色管理权限
        permissions.add(createPermission("角色管理", "role:view", "menu", "/role", 0L, "el-icon-s-check", 2));
        permissions.add(createPermission("创建角色", "role:create", "button", "/role/create", 6L, null, 1));
        permissions.add(createPermission("编辑角色", "role:update", "button", "/role/edit", 6L, null, 2));
        permissions.add(createPermission("删除角色", "role:delete", "button", "/role/delete", 6L, null, 3));
        permissions.add(createPermission("分配权限", "role:assign:permission", "button", "/role/assign/permission", 6L, null, 4));
        
        // 权限管理权限
        permissions.add(createPermission("权限管理", "permission:view", "menu", "/permission", 0L, "el-icon-lock", 3));
        permissions.add(createPermission("创建权限", "permission:create", "button", "/permission/create", 11L, null, 1));
        permissions.add(createPermission("编辑权限", "permission:update", "button", "/permission/edit", 11L, null, 2));
        permissions.add(createPermission("删除权限", "permission:delete", "button", "/permission/delete", 11L, null, 3));
        
        // 租户管理权限
        permissions.add(createPermission("租户管理", "tenant:view", "menu", "/tenant", 0L, "el-icon-office-building", 4));
        permissions.add(createPermission("创建租户", "tenant:create", "button", "/tenant/create", 15L, null, 1));
        permissions.add(createPermission("编辑租户", "tenant:update", "button", "/tenant/edit", 15L, null, 2));
        permissions.add(createPermission("删除租户", "tenant:delete", "button", "/tenant/delete", 15L, null, 3));
        permissions.add(createPermission("初始化租户", "tenant:init", "button", "/tenant/init", 15L, null, 4));
        
        // 保存权限
        for (Permission permission : permissions) {
            permissionMapper.insert(permission);
        }
    }

    /**
     * 创建权限对象
     */
    private Permission createPermission(String name, String code, String type, String url, Long parentId, String icon, Integer sort) {
        Permission permission = new Permission();
        permission.setName(name);
        permission.setCode(code);
        permission.setType(type);
        permission.setUrl(url);
        permission.setParentId(parentId);
        permission.setIcon(icon);
        permission.setSort(sort);
        permission.setCreateTime(new Date());
        permission.setUpdateTime(new Date());
        return permission;
    }

    /**
     * 初始化角色数据
     */
    private void initRoles(Long tenantId) {
        // 检查是否已经初始化
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Role::getTenantId, tenantId);
        if (roleMapper.selectCount(wrapper) > 0) {
            return;
        }
        
        // 创建管理员角色
        Role adminRole = new Role();
        adminRole.setName("admin");
        adminRole.setDescription("系统管理员");
        adminRole.setStatus(1);
        adminRole.setTenantId(tenantId);
        roleMapper.insert(adminRole);
        
        // 分配所有权限给管理员角色
        List<Permission> permissions = permissionMapper.selectList(null);
        for (Permission permission : permissions) {
            RolePermission rolePermission = new RolePermission();
            rolePermission.setRoleId(adminRole.getId());
            rolePermission.setPermissionId(permission.getId());
            rolePermission.setTenantId(tenantId);
            rolePermissionMapper.insert(rolePermission);
        }
        
        // 创建普通用户角色
        Role userRole = new Role();
        userRole.setName("user");
        userRole.setDescription("普通用户");
        userRole.setStatus(1);
        userRole.setTenantId(tenantId);
        roleMapper.insert(userRole);
        
        // 分配基本权限给普通用户角色
        List<String> userPermissionCodes = List.of("user:view");
        for (String code : userPermissionCodes) {
            LambdaQueryWrapper<Permission> permWrapper = new LambdaQueryWrapper<>();
            permWrapper.eq(Permission::getCode, code);
            Permission permission = permissionMapper.selectOne(permWrapper);
            if (permission != null) {
                RolePermission rolePermission = new RolePermission();
                rolePermission.setRoleId(userRole.getId());
                rolePermission.setPermissionId(permission.getId());
                rolePermission.setTenantId(tenantId);
                rolePermissionMapper.insert(rolePermission);
            }
        }
    }
}
