package com.saas.permission.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.saas.permission.dto.RoleDTO;
import com.saas.permission.entity.Role;
import com.saas.permission.service.RoleService;
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
 * 角色管理控制器
 */
@Api(tags = "角色管理")
@RestController
@RequestMapping("/api/roles")
public class RoleController {

    @Autowired
    private RoleService roleService;

    /**
     * 获取角色列表（分页）
     */
    @ApiOperation("获取角色列表")
    @GetMapping
    @PreAuthorize("hasAuthority('role:view')")
    public Map<String, Object> getRoles(
            @ApiParam(value = "角色名称") @RequestParam(required = false) String name,
            @ApiParam(value = "状态") @RequestParam(required = false) Integer status,
            @ApiParam(value = "当前页") @RequestParam(defaultValue = "1") Integer current,
            @ApiParam(value = "每页大小") @RequestParam(defaultValue = "10") Integer size) {
        IPage<Role> page = roleService.getRolesByPage(name, status, current, size);
        return ResponseUtil.success(page);
    }

    /**
     * 获取角色详情
     */
    @ApiOperation("获取角色详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('role:view')")
    public Map<String, Object> getRoleById(@PathVariable Long id) {
        Role role = roleService.getRoleById(id);
        return ResponseUtil.success(role);
    }

    /**
     * 创建角色
     */
    @ApiOperation("创建角色")
    @PostMapping
    @PreAuthorize("hasAuthority('role:create')")
    public Map<String, Object> createRole(@Valid @RequestBody RoleDTO roleDTO) {
        Role role = roleService.createRole(roleDTO);
        return ResponseUtil.success(role);
    }

    /**
     * 更新角色
     */
    @ApiOperation("更新角色")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('role:update')")
    public Map<String, Object> updateRole(@PathVariable Long id, @Valid @RequestBody RoleDTO roleDTO) {
        Role role = roleService.updateRole(id, roleDTO);
        return ResponseUtil.success(role);
    }

    /**
     * 删除角色
     */
    @ApiOperation("删除角色")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('role:delete')")
    public Map<String, Object> deleteRole(@PathVariable Long id) {
        roleService.deleteRole(id);
        return ResponseUtil.success();
    }

    /**
     * 获取角色权限
     */
    @ApiOperation("获取角色权限")
    @GetMapping("/{roleId}/permissions")
    @PreAuthorize("hasAuthority('role:view')")
    public Map<String, Object> getRolePermissions(@PathVariable Long roleId) {
        List<Map<String, Object>> permissions = roleService.getRolePermissions(roleId);
        return ResponseUtil.success(permissions);
    }

    /**
     * 分配角色权限
     */
    @ApiOperation("分配角色权限")
    @PostMapping("/{roleId}/permissions")
    @PreAuthorize("hasAuthority('role:assign:permission')")
    public Map<String, Object> assignPermissions(
            @PathVariable Long roleId,
            @RequestBody Map<String, List<Long>> permissionIds) {
        roleService.assignPermissions(roleId, permissionIds.get("permissionIds"));
        return ResponseUtil.success();
    }

    /**
     * 获取所有角色（不分页）
     */
    @ApiOperation("获取所有角色")
    @GetMapping("/all")
    @PreAuthorize("hasAuthority('role:view')")
    public Map<String, Object> getAllRoles() {
        List<Role> roles = roleService.getAllRoles();
        return ResponseUtil.success(roles);
    }
}
