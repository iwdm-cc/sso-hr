package com.saas.permission.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.saas.permission.dto.PermissionDTO;
import com.saas.permission.entity.Permission;
import com.saas.permission.entity.RolePermission;
import com.saas.permission.exception.BusinessException;
import com.saas.permission.mapper.PermissionMapper;
import com.saas.permission.mapper.RolePermissionMapper;
import com.saas.permission.service.PermissionService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 权限服务实现类
 */
@Service
public class PermissionServiceImpl extends ServiceImpl<PermissionMapper, Permission> implements PermissionService {

    @Autowired
    private PermissionMapper permissionMapper;

    @Autowired
    private RolePermissionMapper rolePermissionMapper;

    /**
     * 分页获取权限列表
     */
    @Override
    public IPage<Permission> getPermissionsByPage(String name, String code, String type, Integer current, Integer size) {
        Page<Permission> page = new Page<>(current, size);
        LambdaQueryWrapper<Permission> wrapper = new LambdaQueryWrapper<>();
        
        // 设置查询条件
        wrapper.like(StringUtils.isNotBlank(name), Permission::getName, name)
               .like(StringUtils.isNotBlank(code), Permission::getCode, code)
               .eq(StringUtils.isNotBlank(type), Permission::getType, type)
               .orderByAsc(Permission::getSort);
        
        return permissionMapper.selectPage(page, wrapper);
    }

    /**
     * 根据ID获取权限
     */
    @Override
    public Permission getPermissionById(Long id) {
        Permission permission = permissionMapper.selectById(id);
        if (permission == null) {
            throw new BusinessException("权限不存在");
        }
        return permission;
    }

    /**
     * 创建权限
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Permission createPermission(PermissionDTO permissionDTO) {
        // 检查权限代码是否存在
        LambdaQueryWrapper<Permission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Permission::getCode, permissionDTO.getCode());
        if (permissionMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("权限代码已存在");
        }
        
        // 检查父权限是否存在
        if (permissionDTO.getParentId() != null && permissionDTO.getParentId() > 0) {
            Permission parent = permissionMapper.selectById(permissionDTO.getParentId());
            if (parent == null) {
                throw new BusinessException("父权限不存在");
            }
            
            // 只有菜单类型的权限可以作为父权限
            if (!"menu".equals(parent.getType())) {
                throw new BusinessException("父权限必须是菜单类型");
            }
        }
        
        // 创建权限对象
        Permission permission = new Permission();
        permission.setName(permissionDTO.getName());
        permission.setCode(permissionDTO.getCode());
        permission.setType(permissionDTO.getType());
        permission.setUrl(permissionDTO.getUrl());
        permission.setParentId(permissionDTO.getParentId() != null ? permissionDTO.getParentId() : 0L);
        permission.setIcon(permissionDTO.getIcon());
        permission.setSort(permissionDTO.getSort());
        
        // 保存权限
        permissionMapper.insert(permission);
        
        return permission;
    }

    /**
     * 更新权限
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Permission updatePermission(Long id, PermissionDTO permissionDTO) {
        // 获取权限
        Permission permission = getPermissionById(id);
        
        // 检查权限代码是否存在
        if (!permission.getCode().equals(permissionDTO.getCode())) {
            LambdaQueryWrapper<Permission> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Permission::getCode, permissionDTO.getCode());
            if (permissionMapper.selectCount(wrapper) > 0) {
                throw new BusinessException("权限代码已存在");
            }
        }
        
        // 检查父权限是否存在
        if (permissionDTO.getParentId() != null && permissionDTO.getParentId() > 0) {
            // 不能选择自己作为父权限
            if (permissionDTO.getParentId().equals(id)) {
                throw new BusinessException("不能选择自己作为父权限");
            }
            
            Permission parent = permissionMapper.selectById(permissionDTO.getParentId());
            if (parent == null) {
                throw new BusinessException("父权限不存在");
            }
            
            // 只有菜单类型的权限可以作为父权限
            if (!"menu".equals(parent.getType())) {
                throw new BusinessException("父权限必须是菜单类型");
            }
            
            // 不能选择自己的子权限作为父权限
            List<Permission> children = getChildrenPermissions(id);
            if (children.stream().anyMatch(p -> p.getId().equals(permissionDTO.getParentId()))) {
                throw new BusinessException("不能选择子权限作为父权限");
            }
        }
        
        // 更新权限信息
        permission.setName(permissionDTO.getName());
        permission.setCode(permissionDTO.getCode());
        permission.setType(permissionDTO.getType());
        permission.setUrl(permissionDTO.getUrl());
        permission.setParentId(permissionDTO.getParentId() != null ? permissionDTO.getParentId() : 0L);
        permission.setIcon(permissionDTO.getIcon());
        permission.setSort(permissionDTO.getSort());
        
        // 保存权限
        permissionMapper.updateById(permission);
        
        return permission;
    }

    /**
     * 删除权限
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deletePermission(Long id) {
        // 获取权限
        Permission permission = getPermissionById(id);
        
        // 检查是否有子权限
        LambdaQueryWrapper<Permission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Permission::getParentId, id);
        if (permissionMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("存在子权限，不能删除");
        }
        
        // 删除权限角色关联
        LambdaQueryWrapper<RolePermission> rpWrapper = new LambdaQueryWrapper<>();
        rpWrapper.eq(RolePermission::getPermissionId, id);
        rolePermissionMapper.delete(rpWrapper);
        
        // 删除权限
        permissionMapper.deleteById(id);
    }

    /**
     * 获取所有权限（不分页）
     */
    @Override
    public List<Permission> getAllPermissions() {
        LambdaQueryWrapper<Permission> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(Permission::getSort);
        return permissionMapper.selectList(wrapper);
    }

    /**
     * 获取权限树
     */
    @Override
    public List<Map<String, Object>> getPermissionTree() {
        // 获取所有权限
        List<Permission> permissions = getAllPermissions();
        
        // 构建树形结构
        List<Map<String, Object>> tree = new ArrayList<>();
        Map<Long, Map<String, Object>> nodeMap = new HashMap<>();
        
        // 创建节点Map
        for (Permission permission : permissions) {
            Map<String, Object> node = new HashMap<>();
            node.put("id", permission.getId());
            node.put("name", permission.getName());
            node.put("code", permission.getCode());
            node.put("type", permission.getType());
            node.put("url", permission.getUrl());
            node.put("parentId", permission.getParentId());
            node.put("icon", permission.getIcon());
            node.put("sort", permission.getSort());
            node.put("children", new ArrayList<>());
            
            nodeMap.put(permission.getId(), node);
        }
        
        // 构建树
        for (Permission permission : permissions) {
            Map<String, Object> node = nodeMap.get(permission.getId());
            
            if (permission.getParentId() == 0) {
                // 根节点
                tree.add(node);
            } else {
                // 子节点
                Map<String, Object> parentNode = nodeMap.get(permission.getParentId());
                if (parentNode != null) {
                    ((List<Map<String, Object>>) parentNode.get("children")).add(node);
                }
            }
        }
        
        return tree;
    }

    /**
     * 根据角色ID列表获取权限列表
     */
    @Override
    public List<Permission> getPermissionsByRoleIds(List<Long> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            return Collections.emptyList();
        }
        return permissionMapper.selectPermissionsByRoleIds(roleIds);
    }

    /**
     * 获取指定权限的所有子权限
     */
    private List<Permission> getChildrenPermissions(Long permissionId) {
        LambdaQueryWrapper<Permission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Permission::getParentId, permissionId);
        List<Permission> directChildren = permissionMapper.selectList(wrapper);
        
        List<Permission> allChildren = new ArrayList<>(directChildren);
        for (Permission child : directChildren) {
            allChildren.addAll(getChildrenPermissions(child.getId()));
        }
        
        return allChildren;
    }
}
