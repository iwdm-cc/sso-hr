package com.saas.auth.context;

/**
 * 租户上下文，用于存储当前租户ID
 */
public class TenantContext {
    private static final ThreadLocal<Long> CURRENT_TENANT = new ThreadLocal<>();
    
    private TenantContext() {
        // 私有构造函数，防止实例化
    }
    
    /**
     * 设置当前租户ID
     */
    public static void setTenantId(Long tenantId) {
        CURRENT_TENANT.set(tenantId);
    }
    
    /**
     * 获取当前租户ID
     * 开发环境下，如果未设置租户ID，默认返回系统管理租户ID 1
     */
    public static Long getTenantId() {
        Long tenantId = CURRENT_TENANT.get();
        // 开发环境下，如果租户ID为空，默认使用ID为1的系统管理租户
        if (tenantId == null) {
            return 1L;
        }
        return tenantId;
    }
    
    /**
     * 清除当前租户ID
     */
    public static void clear() {
        CURRENT_TENANT.remove();
    }
}