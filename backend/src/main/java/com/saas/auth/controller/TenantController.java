package com.saas.auth.controller;

import com.saas.auth.common.ApiResponse;
import com.saas.auth.dto.TenantDTO;
import com.saas.auth.service.TenantService;
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
 * 租户管理控制器
 */
@RestController
@RequestMapping("/api/tenant")
@Tag(name = "租户管理", description = "租户的CRUD操作")
public class TenantController {

    @Autowired
    private TenantService tenantService;

    @GetMapping("/list")
    @Operation(summary = "获取租户列表")
    // 确保租户列表接口可以不受限制访问
    public ApiResponse<Map<String, Object>> list(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页记录数") @RequestParam(defaultValue = "10") int pageSize,
            @Parameter(description = "关键词") @RequestParam(required = false) String keyword,
            @Parameter(description = "状态") @RequestParam(required = false) Boolean status) {
        return ApiResponse.success(tenantService.getTenantList(page, pageSize, keyword, status));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取租户详情")
    @PreAuthorize("hasAuthority('tenant:view')")
    public ApiResponse<TenantDTO> getById(@PathVariable Long id) {
        return ApiResponse.success(tenantService.getTenantById(id));
    }

    @PostMapping
    @Operation(summary = "创建租户")
    @PreAuthorize("hasAuthority('tenant:create')")
    public ApiResponse<Void> create(@Valid @RequestBody TenantDTO tenantDTO) {
        tenantService.createTenant(tenantDTO);
        return ApiResponse.success();
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新租户")
    @PreAuthorize("hasAuthority('tenant:update')")
    public ApiResponse<Void> update(@PathVariable Long id, @Valid @RequestBody TenantDTO tenantDTO) {
        tenantDTO.setId(id);
        tenantService.updateTenant(tenantDTO);
        return ApiResponse.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除租户")
    @PreAuthorize("hasAuthority('tenant:delete')")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        tenantService.deleteTenant(id);
        return ApiResponse.success();
    }

    @PostMapping("/switch/{id}")
    @Operation(summary = "切换租户")
    public ApiResponse<Void> switchTenant(@PathVariable Long id) {
        tenantService.switchTenant(id);
        return ApiResponse.success();
    }
}
