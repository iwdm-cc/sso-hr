package com.saas.auth.controller;

import com.saas.auth.common.ApiResponse;
import com.saas.auth.dto.UserDTO;
import com.saas.auth.entity.User;
import com.saas.auth.service.UserService;
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
 * 用户管理控制器
 */
@RestController
@RequestMapping("/user")
@Tag(name = "用户管理", description = "用户的CRUD操作")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/info")
    @Operation(summary = "获取当前用户信息")
    public ApiResponse<Map<String, Object>> getCurrentUserInfo() {
        return ApiResponse.success(userService.getCurrentUserInfo());
    }

    @GetMapping("/list")
    @Operation(summary = "获取用户列表")
    // 暂时移除权限验证，方便前端访问数据
    public ApiResponse<Map<String, Object>> list(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页记录数") @RequestParam(defaultValue = "10") int pageSize,
            @Parameter(description = "关键词") @RequestParam(required = false) String keyword,
            @Parameter(description = "状态") @RequestParam(required = false) Boolean status) {
        return ApiResponse.success(userService.getUserList(page, pageSize, keyword, status));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取用户详情")
    @PreAuthorize("hasAuthority('user:view')")
    public ApiResponse<UserDTO> getById(@PathVariable Long id) {
        return ApiResponse.success(userService.getUserById(id));
    }

    @PostMapping
    @Operation(summary = "创建用户")
    @PreAuthorize("hasAuthority('user:create')")
    public ApiResponse<Void> create(@Valid @RequestBody UserDTO userDTO) {
        userService.createUser(userDTO);
        return ApiResponse.success();
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新用户")
    @PreAuthorize("hasAuthority('user:update')")
    public ApiResponse<Void> update(@PathVariable Long id, @Valid @RequestBody UserDTO userDTO) {
        userDTO.setId(id);
        userService.updateUser(userDTO);
        return ApiResponse.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除用户")
    @PreAuthorize("hasAuthority('user:delete')")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        userService.deleteUser(id);
        return ApiResponse.success();
    }

    @PostMapping("/{userId}/roles")
    @Operation(summary = "分配角色")
    @PreAuthorize("hasAuthority('user:assign')")
    public ApiResponse<Void> assignRoles(
            @PathVariable Long userId,
            @RequestBody Map<String, List<Long>> roleIds) {
        userService.assignRoles(userId, roleIds.get("roleIds"));
        return ApiResponse.success();
    }
}
