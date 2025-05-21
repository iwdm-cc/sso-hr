package com.saas.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.saas.auth.dto.RoleDTO;
import com.saas.auth.entity.Role;
import com.saas.auth.entity.RolePermission;
import com.saas.auth.exception.CustomException;
import com.saas.auth.mapper.RoleMapper;
import com.saas.auth.mapper.RolePermissionMapper;
import com.saas.auth.security.TenantContext;
import com.saas.auth.service.RoleService;
import org.springframework.beans.BeanUtils;
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
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private RolePermissionMapper rolePermissionMapper;

    @Override
    public Map<String, Object> getRoleList(int page, int pageSize, String keyword, Boolean status) {
        Page<Role> pageParam = new Page<>(page, pageSize);
        Long tenantId = TenantContext.getCurrentTenant();
        Integer statusValue = (status != null) ? (status ? 1 : 0) : null;
        IPage<Role> pageResult = roleMapper.selectRolePage(pageParam, keyword, statusValue, tenantId);
        
        List<RoleDTO> roleDTOs = pageResult.getRecords().stream().map(role -> {
            RoleDTO roleDTO = new RoleDTO();
            BeanUtils.copyProperties(role, roleDTO);
            return roleDTO;
        }).collect(Collectors.toList());
        
        Map<String, Object> result = new HashMap<>();
        result.put("list", roleDTOs);
        result.put("total", pageResult.getTotal());
        return result;
    }

    @Override
    public RoleDTO getRoleById(Long id) {
        Role role = roleMapper.selectById(id);
        if (role == null || !role.getTenantId().equals(TenantContext.getCurrentTenant())) {
            throw new CustomException("角色不存在");
        }
        
        RoleDTO roleDTO = new RoleDTO();
        BeanUtils.copyProperties(role, roleDTO);
        return roleDTO;
    }

    @Override
    @Transactional
    public void createRole(RoleDTO roleDTO) {
        Role role = new Role();
        BeanUtils.copyProperties(roleDTO, role);
        
        // 设置租户ID
        role.setTenantId(TenantContext.getCurrentTenant());
        
        roleMapper.insert(role);
    }

    @Override
    @Transactional
    public void updateRole(RoleDTO roleDTO) {
        Role role = roleMapper.selectById(roleDTO.getId());
        if (role == null || !role.getTenantId().equals(TenantContext.getCurrentTenant())) {
            throw new CustomException("角色不存在");
        }
        
        BeanUtils.copyProperties(roleDTO, role);
        roleMapper.updateById(role);
    }

    @Override
    @Transactional
    public void deleteRole(Long id) {
        Role role = roleMapper.selectById(id);
        if (role == null || !role.getTenantId().equals(TenantContext.getCurrentTenant())) {
            throw new CustomException("角色不存在");
        }
        
        // 删除角色权限关联
        QueryWrapper<RolePermission> wrapper = new QueryWrapper<>();
        wrapper.eq("role_id", id);
        rolePermissionMapper.delete(wrapper);
        
        // 删除角色
        roleMapper.deleteById(id);
    }

    @Override
    @Transactional
    public void assignPermissions(Long roleId, List<Long> permissionIds) {
        Role role = roleMapper.selectById(roleId);
        if (role == null || !role.getTenantId().equals(TenantContext.getCurrentTenant())) {
            throw new CustomException("角色不存在");
        }
        
        // 删除角色权限关联
        QueryWrapper<RolePermission> wrapper = new QueryWrapper<>();
        wrapper.eq("role_id", roleId);
        rolePermissionMapper.delete(wrapper);
        
        // 添加新的权限关联
        if (permissionIds != null && !permissionIds.isEmpty()) {
            for (Long permissionId : permissionIds) {
                RolePermission rolePermission = new RolePermission();
                rolePermission.setRoleId(roleId);
                rolePermission.setPermissionId(permissionId);
                rolePermission.setTenantId(TenantContext.getCurrentTenant());
                rolePermissionMapper.insert(rolePermission);
            }
        }
    }

    @Override
    public List<RoleDTO> getUserRoles(Long userId) {
        List<Role> roles = roleMapper.selectRolesByUserId(userId, TenantContext.getCurrentTenant());
        return roles.stream().map(role -> {
            RoleDTO roleDTO = new RoleDTO();
            BeanUtils.copyProperties(role, roleDTO);
            return roleDTO;
        }).collect(Collectors.toList());
    }

    @Override
    public List<Long> getRolePermissionIds(Long roleId) {
        Role role = roleMapper.selectById(roleId);
        if (role == null || !role.getTenantId().equals(TenantContext.getCurrentTenant())) {
            throw new CustomException("角色不存在");
        }
        
        return roleMapper.selectRolesByUserId(roleId, TenantContext.getCurrentTenant())
                .stream()
                .map(Role::getId)
                .collect(Collectors.toList());
    }
}
