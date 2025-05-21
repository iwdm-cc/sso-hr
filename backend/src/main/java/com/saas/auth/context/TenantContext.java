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
     */
    public static Long getTenantId() {
        return CURRENT_TENANT.get();
    }
    
    /**
     * 清除当前租户ID
     */
    public static void clear() {
        CURRENT_TENANT.remove();
    }
}