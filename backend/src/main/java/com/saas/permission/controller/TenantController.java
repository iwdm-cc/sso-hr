package com.saas.permission.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.saas.permission.dto.TenantDTO;
import com.saas.permission.entity.Tenant;
import com.saas.permission.service.TenantService;
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
 * 租户管理控制器
 */
@Api(tags = "租户管理")
@RestController
@RequestMapping("/api/tenants")
public class TenantController {

    @Autowired
    private TenantService tenantService;

    /**
     * 获取租户列表（分页）
     */
    @ApiOperation("获取租户列表")
    @GetMapping
    @PreAuthorize("hasAuthority('tenant:view')")
    public Map<String, Object> getTenants(
            @ApiParam(value = "租户名称") @RequestParam(required = false) String name,
            @ApiParam(value = "状态") @RequestParam(required = false) Integer status,
            @ApiParam(value = "当前页") @RequestParam(defaultValue = "1") Integer current,
            @ApiParam(value = "每页大小") @RequestParam(defaultValue = "10") Integer size) {
        IPage<Tenant> page = tenantService.getTenantsByPage(name, status, current, size);
        return ResponseUtil.success(page);
    }

    /**
     * 获取租户详情
     */
    @ApiOperation("获取租户详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('tenant:view')")
    public Map<String, Object> getTenantById(@PathVariable Long id) {
        Tenant tenant = tenantService.getTenantById(id);
        return ResponseUtil.success(tenant);
    }

    /**
     * 创建租户
     */
    @ApiOperation("创建租户")
    @PostMapping
    @PreAuthorize("hasAuthority('tenant:create')")
    public Map<String, Object> createTenant(@Valid @RequestBody TenantDTO tenantDTO) {
        Tenant tenant = tenantService.createTenant(tenantDTO);
        return ResponseUtil.success(tenant);
    }

    /**
     * 更新租户
     */
    @ApiOperation("更新租户")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('tenant:update')")
    public Map<String, Object> updateTenant(@PathVariable Long id, @Valid @RequestBody TenantDTO tenantDTO) {
        Tenant tenant = tenantService.updateTenant(id, tenantDTO);
        return ResponseUtil.success(tenant);
    }

    /**
     * 删除租户
     */
    @ApiOperation("删除租户")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('tenant:delete')")
    public Map<String, Object> deleteTenant(@PathVariable Long id) {
        tenantService.deleteTenant(id);
        return ResponseUtil.success();
    }

    /**
     * 获取所有租户（不分页）
     */
    @ApiOperation("获取所有租户")
    @GetMapping("/all")
    public Map<String, Object> getAllTenants() {
        List<Tenant> tenants = tenantService.getAllTenants();
        return ResponseUtil.success(tenants);
    }

    /**
     * 初始化租户数据
     */
    @ApiOperation("初始化租户数据")
    @PostMapping("/{tenantId}/init")
    @PreAuthorize("hasAuthority('tenant:init')")
    public Map<String, Object> initTenantData(@PathVariable Long tenantId) {
        tenantService.initTenantData(tenantId);
        return ResponseUtil.success();
    }
}
