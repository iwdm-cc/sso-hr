package com.saas.permission.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.saas.permission.dto.PermissionDTO;
import com.saas.permission.entity.Permission;
import com.saas.permission.service.PermissionService;
import com.saas.permission.util.ResponseUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 权限管理控制器
 */
@Api(tags = "权限管理")
@RestController
@RequestMapping("/api/permissions")
public class PermissionController {

    @Autowired
    private PermissionService permissionService;

    /**
     * 获取权限列表（分页）
     */
    @ApiOperation("获取权限列表")
    @GetMapping
    @PreAuthorize("hasAuthority('permission:view')")
    public Map<String, Object> getPermissions(
            @ApiParam(value = "权限名称") @RequestParam(required = false) String name,
            @ApiParam(value = "权限代码") @RequestParam(required = false) String code,
            @ApiParam(value = "权限类型") @RequestParam(required = false) String type,
            @ApiParam(value = "当前页") @RequestParam(defaultValue = "1") Integer current,
            @ApiParam(value = "每页大小") @RequestParam(defaultValue = "10") Integer size) {
        IPage<Permission> page = permissionService.getPermissionsByPage(name, code, type, current, size);
        return ResponseUtil.success(page);
    }

    /**
     * 获取权限详情
     */
    @ApiOperation("获取权限详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('permission:view')")
    public Map<String, Object> getPermissionById(@PathVariable Long id) {
        Permission permission = permissionService.getPermissionById(id);
        return ResponseUtil.success(permission);
    }

    /**
     * 创建权限
     */
    @ApiOperation("创建权限")
    @PostMapping
    @PreAuthorize("hasAuthority('permission:create')")
    public Map<String, Object> createPermission(@Valid @RequestBody PermissionDTO permissionDTO) {
        Permission permission = permissionService.createPermission(permissionDTO);
        return ResponseUtil.success(permission);
    }

    /**
     * 更新权限
     */
    @ApiOperation("更新权限")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('permission:update')")
    public Map<String, Object> updatePermission(@PathVariable Long id, @Valid @RequestBody PermissionDTO permissionDTO) {
        Permission permission = permissionService.updatePermission(id, permissionDTO);
        return ResponseUtil.success(permission);
    }

    /**
     * 删除权限
     */
    @ApiOperation("删除权限")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('permission:delete')")
    public Map<String, Object> deletePermission(@PathVariable Long id) {
        permissionService.deletePermission(id);
        return ResponseUtil.success();
    }

    /**
     * 获取所有权限（不分页）
     */
    @ApiOperation("获取所有权限")
    @GetMapping("/all")
    @PreAuthorize("hasAuthority('permission:view')")
    public Map<String, Object> getAllPermissions() {
        List<Permission> permissions = permissionService.getAllPermissions();
        return ResponseUtil.success(permissions);
    }

    /**
     * 获取权限树
     */
    @ApiOperation("获取权限树")
    @GetMapping("/tree")
    @PreAuthorize("hasAuthority('permission:view')")
    public Map<String, Object> getPermissionTree() {
        List<Map<String, Object>> permissionTree = permissionService.getPermissionTree();
        return ResponseUtil.success(permissionTree);
    }
}
