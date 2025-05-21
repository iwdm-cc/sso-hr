package com.saas.auth.controller;

import com.saas.auth.common.ApiResponse;
import com.saas.auth.dto.RoleDTO;
import com.saas.auth.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 角色管理控制器
 */
@RestController
@RequestMapping("/api/role")
@Tag(name = "角色管理", description = "角色的CRUD操作")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @GetMapping("/list")
    @Operation(summary = "获取角色列表")
    @PreAuthorize("hasAuthority('role:list')")
    public ApiResponse<Map<String, Object>> list(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页记录数") @RequestParam(defaultValue = "10") int pageSize,
            @Parameter(description = "关键词") @RequestParam(required = false) String keyword,
            @Parameter(description = "状态") @RequestParam(required = false) Boolean status) {
        return ApiResponse.success(roleService.getRoleList(page, pageSize, keyword, status));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取角色详情")
    @PreAuthorize("hasAuthority('role:view')")
    public ApiResponse<RoleDTO> getById(@PathVariable Long id) {
        return ApiResponse.success(roleService.getRoleById(id));
    }

    @PostMapping
    @Operation(summary = "创建角色")
    @PreAuthorize("hasAuthority('role:create')")
    public ApiResponse<Void> create(@Valid @RequestBody RoleDTO roleDTO) {
        roleService.createRole(roleDTO);
        return ApiResponse.success();
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新角色")
    @PreAuthorize("hasAuthority('role:update')")
    public ApiResponse<Void> update(@PathVariable Long id, @Valid @RequestBody RoleDTO roleDTO) {
        roleDTO.setId(id);
        roleService.updateRole(roleDTO);
        return ApiResponse.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除角色")
    @PreAuthorize("hasAuthority('role:delete')")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        roleService.deleteRole(id);
        return ApiResponse.success();
    }

    @PostMapping("/{roleId}/permissions")
    @Operation(summary = "分配权限")
    @PreAuthorize("hasAuthority('role:assign')")
    public ApiResponse<Void> assignPermissions(
            @PathVariable Long roleId,
            @RequestBody Map<String, List<Long>> permissionIds) {
        roleService.assignPermissions(roleId, permissionIds.get("permissionIds"));
        return ApiResponse.success();
    }

    @GetMapping("/{roleId}/permissions")
    @Operation(summary = "获取角色权限")
    @PreAuthorize("hasAuthority('role:view')")
    public ApiResponse<List<Long>> getRolePermissions(@PathVariable Long roleId) {
        return ApiResponse.success(roleService.getRolePermissionIds(roleId));
    }
}
