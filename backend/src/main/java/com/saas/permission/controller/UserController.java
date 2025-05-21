package com.saas.permission.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.saas.permission.dto.UserDTO;
import com.saas.permission.entity.User;
import com.saas.permission.service.UserService;
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
 * 用户管理控制器
 */
@Api(tags = "用户管理")
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 获取当前用户信息
     */
    @ApiOperation("获取当前用户信息")
    @GetMapping("/current")
    public Map<String, Object> getCurrentUser() {
        User user = userService.getCurrentUser();
        return ResponseUtil.success(user);
    }

    /**
     * 获取用户列表（分页）
     */
    @ApiOperation("获取用户列表")
    @GetMapping
    @PreAuthorize("hasAuthority('user:view')")
    public Map<String, Object> getUsers(
            @ApiParam(value = "用户名") @RequestParam(required = false) String username,
            @ApiParam(value = "姓名") @RequestParam(required = false) String name,
            @ApiParam(value = "状态") @RequestParam(required = false) Integer status,
            @ApiParam(value = "当前页") @RequestParam(defaultValue = "1") Integer current,
            @ApiParam(value = "每页大小") @RequestParam(defaultValue = "10") Integer size) {
        IPage<User> page = userService.getUsersByPage(username, name, status, current, size);
        return ResponseUtil.success(page);
    }

    /**
     * 获取用户详情
     */
    @ApiOperation("获取用户详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('user:view')")
    public Map<String, Object> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return ResponseUtil.success(user);
    }

    /**
     * 创建用户
     */
    @ApiOperation("创建用户")
    @PostMapping
    @PreAuthorize("hasAuthority('user:create')")
    public Map<String, Object> createUser(@Valid @RequestBody UserDTO userDTO) {
        User user = userService.createUser(userDTO);
        return ResponseUtil.success(user);
    }

    /**
     * 更新用户
     */
    @ApiOperation("更新用户")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('user:update')")
    public Map<String, Object> updateUser(@PathVariable Long id, @Valid @RequestBody UserDTO userDTO) {
        User user = userService.updateUser(id, userDTO);
        return ResponseUtil.success(user);
    }

    /**
     * 删除用户
     */
    @ApiOperation("删除用户")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('user:delete')")
    public Map<String, Object> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseUtil.success();
    }

    /**
     * 获取用户角色
     */
    @ApiOperation("获取用户角色")
    @GetMapping("/{userId}/roles")
    @PreAuthorize("hasAuthority('user:view')")
    public Map<String, Object> getUserRoles(@PathVariable Long userId) {
        List<Map<String, Object>> roles = userService.getUserRoles(userId);
        return ResponseUtil.success(roles);
    }

    /**
     * 分配用户角色
     */
    @ApiOperation("分配用户角色")
    @PostMapping("/{userId}/roles")
    @PreAuthorize("hasAuthority('user:assign:role')")
    public Map<String, Object> assignRoles(
            @PathVariable Long userId,
            @RequestBody Map<String, List<Long>> roleIds) {
        userService.assignRoles(userId, roleIds.get("roleIds"));
        return ResponseUtil.success();
    }

    /**
     * 获取所有用户（不分页）
     */
    @ApiOperation("获取所有用户")
    @GetMapping("/all")
    @PreAuthorize("hasAuthority('user:view')")
    public Map<String, Object> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseUtil.success(users);
    }
}
