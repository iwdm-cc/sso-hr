-- 租户表
CREATE TABLE IF NOT EXISTS sys_tenant (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL COMMENT '租户名称',
    code VARCHAR(50) NOT NULL COMMENT '租户编码',
    description VARCHAR(255) COMMENT '租户描述',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    create_time DATETIME NOT NULL COMMENT '创建时间',
    update_time DATETIME COMMENT '更新时间',
    UNIQUE INDEX uidx_code (code)
);

-- 用户表
CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码',
    name VARCHAR(50) COMMENT '姓名',
    avatar VARCHAR(255) COMMENT '头像',
    email VARCHAR(100) COMMENT '邮箱',
    phone VARCHAR(20) COMMENT '手机号',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    tenant_id BIGINT NOT NULL COMMENT '租户ID',
    create_time DATETIME NOT NULL COMMENT '创建时间',
    update_time DATETIME COMMENT '更新时间',
    UNIQUE INDEX uidx_username_tenant (username, tenant_id)
);

-- 角色表
CREATE TABLE IF NOT EXISTS sys_role (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL COMMENT '角色名称',
    code VARCHAR(50) NOT NULL COMMENT '角色编码',
    description VARCHAR(255) COMMENT '角色描述',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    tenant_id BIGINT NOT NULL COMMENT '租户ID',
    create_time DATETIME NOT NULL COMMENT '创建时间',
    update_time DATETIME COMMENT '更新时间',
    UNIQUE INDEX uidx_code_tenant (code, tenant_id)
);

-- 权限表
CREATE TABLE IF NOT EXISTS sys_permission (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL COMMENT '权限名称',
    code VARCHAR(50) NOT NULL COMMENT '权限编码',
    type VARCHAR(20) NOT NULL COMMENT '权限类型：menu-菜单，button-按钮，api-接口',
    parent_id BIGINT DEFAULT 0 COMMENT '父级ID',
    resource_path VARCHAR(255) COMMENT '资源路径',
    icon VARCHAR(100) COMMENT '图标',
    sort INT DEFAULT 0 COMMENT '排序',
    create_time DATETIME NOT NULL COMMENT '创建时间',
    update_time DATETIME COMMENT '更新时间',
    UNIQUE INDEX uidx_code (code)
);

-- 用户角色关系表
CREATE TABLE IF NOT EXISTS sys_user_role (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    tenant_id BIGINT NOT NULL COMMENT '租户ID',
    create_time DATETIME NOT NULL COMMENT '创建时间',
    UNIQUE INDEX uidx_user_role (user_id, role_id, tenant_id)
);

-- 角色权限关系表
CREATE TABLE IF NOT EXISTS sys_role_permission (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    role_id BIGINT NOT NULL COMMENT '角色ID',
    permission_id BIGINT NOT NULL COMMENT '权限ID',
    tenant_id BIGINT NOT NULL COMMENT '租户ID',
    create_time DATETIME NOT NULL COMMENT '创建时间',
    UNIQUE INDEX uidx_role_perm (role_id, permission_id, tenant_id)
);