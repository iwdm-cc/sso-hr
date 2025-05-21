package com.saas.auth.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * 租户DTO
 */
@Data
public class TenantDTO {

    /**
     * ID
     */
    private Long id;

    /**
     * 租户名称
     */
    @NotBlank(message = "租户名称不能为空")
    @Size(min = 2, max = 50, message = "租户名称长度必须在2-50个字符之间")
    private String name;

    /**
     * 租户代码
     */
    @NotBlank(message = "租户代码不能为空")
    @Size(min = 2, max = 20, message = "租户代码长度必须在2-20个字符之间")
    @Pattern(regexp = "^[a-z0-9_]+$", message = "租户代码只能包含小写字母、数字和下划线")
    private String code;

    /**
     * 租户描述
     */
    @Size(max = 200, message = "租户描述长度不能超过200个字符")
    private String description;

    /**
     * 状态
     */
    private Boolean status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
