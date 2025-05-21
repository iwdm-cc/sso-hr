package com.saas.permission.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.saas.permission.context.TenantContext;
import com.saas.permission.dto.RoleDTO;
import com.saas.permission.entity.Permission;
import com.saas.permission.entity.Role;
import com.saas.permission.entity.RolePermission;
import com.saas.permission.exception.BusinessException;
import com.saas.permission.mapper.RoleMapper;
import com.saas.permission.mapper.RolePermissionMapper;
import com.saas.permission.service.PermissionService;
import com.saas.permission.service.RoleService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 角色服务实现类
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private RolePermissionMapper rolePermissionMapper;

    @Autowired
    private PermissionService permissionService;

    /**
     * 分页获取角色列表
     */
    @Override
    public IPage<Role> getRolesByPage(String name, Integer status, Integer current, Integer size) {
        Page<Role> page = new Page<>(current, size);
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        
        // 设置租户条件
        Long tenantId = TenantContext.getCurrentTenant();
        wrapper.eq(Role::getTenantId, tenantId);
        
        // 设置查询条件
        wrapper.like(StringUtils.isNotBlank(name), Role::getName, name)
               .eq(status != null, Role::getStatus, status)
               .orderByDesc(Role::getCreateTime);
        
        return roleMapper.selectPage(page, wrapper);
    }

    /**
     * 根据ID获取角色
     */
    @Override
    public Role getRoleById(Long id) {
        Role role = roleMapper.selectById(id);
        if (role == null) {
            throw new BusinessException("角色不存在");
        }
        
        // 检查租户
        Long tenantId = TenantContext.getCurrentTenant();
        if (!role.getTenantId().equals(tenantId)) {
            throw new BusinessException("无权访问该角色");
        }
        
        return role;
    }

    /**
     * 创建角色
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Role createRole(RoleDTO roleDTO) {
        // 检查角色名称是否存在
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Role::getName, roleDTO.getName())
               .eq(Role::getTenantId, TenantContext.getCurrentTenant());
        if (roleMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("角色名称已存在");
        }
        
        // 创建角色对象
        Role role = new Role();
        role.setName(roleDTO.getName());
        role.setDescription(roleDTO.getDescription());
        role.setStatus(roleDTO.getStatus());
        role.setTenantId(TenantContext.getCurrentTenant());
        
        // 保存角色
        roleMapper.insert(role);
        
        // 分配权限
        if (roleDTO.getPermissionIds() != null && !roleDTO.getPermissionIds().isEmpty()) {
            assignPermissions(role.getId(), roleDTO.getPermissionIds());
        }
        
        return role;
    }

    /**
     * 更新角色
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Role updateRole(Long id, RoleDTO roleDTO) {
        // 获取角色
        Role role = getRoleById(id);
        
        // 如果是管理员角色，不允许修改名称和状态
        if ("admin".equals(role.getName())) {
            role.setDescription(roleDTO.getDescription());
        } else {
            // 检查角色名称是否存在
            if (!role.getName().equals(roleDTO.getName())) {
                LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(Role::getName, roleDTO.getName())
                       .eq(Role::getTenantId, TenantContext.getCurrentTenant());
                if (roleMapper.selectCount(wrapper) > 0) {
                    throw new BusinessException("角色名称已存在");
                }
            }
            
            // 更新角色信息
            role.setName(roleDTO.getName());
            role.setDescription(roleDTO.getDescription());
            role.setStatus(roleDTO.getStatus());
        }
        
        // 保存角色
        roleMapper.updateById(role);
        
        return role;
    }

    /**
     * 删除角色
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteRole(Long id) {
        // 获取角色
        Role role = getRoleById(id);
        
        // 如果是管理员角色，不允许删除
        if ("admin".equals(role.getName())) {
            throw new BusinessException("不能删除管理员角色");
        }
        
        // 删除角色权限关联
        LambdaQueryWrapper<RolePermission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RolePermission::getRoleId, id);
        rolePermissionMapper.delete(wrapper);
        
        // 删除角色
        roleMapper.deleteById(id);
    }

    /**
     * 获取角色权限
     */
    @Override
    public List<Map<String, Object>> getRolePermissions(Long roleId) {
        // 获取角色
        getRoleById(roleId);
        
        // 获取角色权限
        List<Permission> permissions = roleMapper.selectPermissionsByRoleId(roleId);
        
        // 构建结果
        return permissions.stream().map(permission -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", permission.getId());
            map.put("name", permission.getName());
            map.put("code", permission.getCode());
            map.put("type", permission.getType());
            return map;
        }).collect(Collectors.toList());
    }

    /**
     * 分配角色权限
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignPermissions(Long roleId, List<Long> permissionIds) {
        // 获取角色
        getRoleById(roleId);
        
        // 删除旧的权限关联
        LambdaQueryWrapper<RolePermission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RolePermission::getRoleId, roleId);
        rolePermissionMapper.delete(wrapper);
        
        // 添加新的权限关联
        if (permissionIds != null && !permissionIds.isEmpty()) {
            Long tenantId = TenantContext.getCurrentTenant();
            for (Long permissionId : permissionIds) {
                // 检查权限是否存在
                Permission permission = permissionService.getPermissionById(permissionId);
                if (permission == null) {
                    throw new BusinessException("权限不存在");
                }
                
                RolePermission rolePermission = new RolePermission();
                rolePermission.setRoleId(roleId);
                rolePermission.setPermissionId(permissionId);
                rolePermission.setTenantId(tenantId);
                rolePermissionMapper.insert(rolePermission);
            }
        }
    }

    /**
     * 获取所有角色（不分页）
     */
    @Override
    public List<Role> getAllRoles() {
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Role::getTenantId, TenantContext.getCurrentTenant())
               .orderByDesc(Role::getCreateTime);
        return roleMapper.selectList(wrapper);
    }

    /**
     * 根据用户ID获取角色列表
     */
    @Override
    public List<Role> getRolesByUserId(Long userId) {
        return roleMapper.selectRolesByUserId(userId);
    }
}
