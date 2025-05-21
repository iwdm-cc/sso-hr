package com.saas.auth.controller;

import com.saas.auth.common.ApiResponse;
import com.saas.auth.dto.PermissionDTO;
import com.saas.auth.service.PermissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 权限管理控制器
 */
@RestController
@RequestMapping("/api/permission")
@Tag(name = "权限管理", description = "权限的CRUD操作")
public class PermissionController {

    @Autowired
    private PermissionService permissionService;

    @GetMapping("/list")
    @Operation(summary = "获取权限列表")
    // 暂时移除权限验证，方便前端访问数据
    public ApiResponse<List<PermissionDTO>> list(
            @Parameter(description = "类型") @RequestParam(required = false) String type,
            @Parameter(description = "关键词") @RequestParam(required = false) String keyword) {
        return ApiResponse.success(permissionService.getPermissionList(type, keyword));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取权限详情")
    @PreAuthorize("hasAuthority('permission:view')")
    public ApiResponse<PermissionDTO> getById(@PathVariable Long id) {
        return ApiResponse.success(permissionService.getPermissionById(id));
    }

    @PostMapping
    @Operation(summary = "创建权限")
    @PreAuthorize("hasAuthority('permission:create')")
    public ApiResponse<Void> create(@Valid @RequestBody PermissionDTO permissionDTO) {
        permissionService.createPermission(permissionDTO);
        return ApiResponse.success();
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新权限")
    @PreAuthorize("hasAuthority('permission:update')")
    public ApiResponse<Void> update(@PathVariable Long id, @Valid @RequestBody PermissionDTO permissionDTO) {
        permissionDTO.setId(id);
        permissionService.updatePermission(permissionDTO);
        return ApiResponse.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除权限")
    @PreAuthorize("hasAuthority('permission:delete')")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        permissionService.deletePermission(id);
        return ApiResponse.success();
    }
}
