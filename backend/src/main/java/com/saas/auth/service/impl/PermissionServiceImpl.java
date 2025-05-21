package com.saas.auth.service.impl;

import com.saas.auth.dto.PermissionDTO;
import com.saas.auth.entity.Permission;
import com.saas.auth.exception.CustomException;
import com.saas.auth.mapper.PermissionMapper;
import com.saas.auth.security.TenantContext;
import com.saas.auth.service.PermissionService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 权限服务实现类
 */
@Service
public class PermissionServiceImpl implements PermissionService {

    @Autowired
    private PermissionMapper permissionMapper;

    @Override
    public List<PermissionDTO> getPermissionList(String type, String keyword) {
        List<Permission> permissions = permissionMapper.selectPermissionList(type, keyword);
        return permissions.stream().map(permission -> {
            PermissionDTO permissionDTO = new PermissionDTO();
            BeanUtils.copyProperties(permission, permissionDTO);
            return permissionDTO;
        }).collect(Collectors.toList());
    }

    @Override
    public PermissionDTO getPermissionById(Long id) {
        Permission permission = permissionMapper.selectById(id);
        if (permission == null) {
            throw new CustomException("权限不存在");
        }
        
        PermissionDTO permissionDTO = new PermissionDTO();
        BeanUtils.copyProperties(permission, permissionDTO);
        return permissionDTO;
    }

    @Override
    @Transactional
    public void createPermission(PermissionDTO permissionDTO) {
        Permission permission = new Permission();
        BeanUtils.copyProperties(permissionDTO, permission);
        permissionMapper.insert(permission);
    }

    @Override
    @Transactional
    public void updatePermission(PermissionDTO permissionDTO) {
        Permission permission = permissionMapper.selectById(permissionDTO.getId());
        if (permission == null) {
            throw new CustomException("权限不存在");
        }
        
        BeanUtils.copyProperties(permissionDTO, permission);
        permissionMapper.updateById(permission);
    }

    @Override
    @Transactional
    public void deletePermission(Long id) {
        Permission permission = permissionMapper.selectById(id);
        if (permission == null) {
            throw new CustomException("权限不存在");
        }
        
        permissionMapper.deleteById(id);
    }

    @Override
    public List<PermissionDTO> getRolePermissions(Long roleId) {
        List<Permission> permissions = permissionMapper.selectPermissionsByRoleId(roleId, TenantContext.getCurrentTenant());
        return permissions.stream().map(permission -> {
            PermissionDTO permissionDTO = new PermissionDTO();
            BeanUtils.copyProperties(permission, permissionDTO);
            return permissionDTO;
        }).collect(Collectors.toList());
    }
}
