-- 租户表
CREATE TABLE IF NOT EXISTS sys_tenant (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    code VARCHAR(50) NOT NULL,
    description VARCHAR(255),
    status SMALLINT DEFAULT 1,
    create_time TIMESTAMP NOT NULL,
    update_time TIMESTAMP,
    CONSTRAINT uidx_code UNIQUE (code)
);

-- 用户表
CREATE TABLE IF NOT EXISTS sys_user (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    password VARCHAR(100) NOT NULL,
    name VARCHAR(50),
    avatar VARCHAR(255),
    email VARCHAR(100),
    phone VARCHAR(20),
    status SMALLINT DEFAULT 1,
    tenant_id BIGINT NOT NULL,
    create_time TIMESTAMP NOT NULL,
    update_time TIMESTAMP,
    CONSTRAINT uidx_username_tenant UNIQUE (username, tenant_id)
);

-- 角色表
CREATE TABLE IF NOT EXISTS sys_role (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    code VARCHAR(50) NOT NULL,
    description VARCHAR(255),
    status SMALLINT DEFAULT 1,
    tenant_id BIGINT NOT NULL,
    create_time TIMESTAMP NOT NULL,
    update_time TIMESTAMP,
    CONSTRAINT uidx_code_tenant UNIQUE (code, tenant_id)
);

-- 权限表
CREATE TABLE IF NOT EXISTS sys_permission (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    code VARCHAR(50) NOT NULL,
    type VARCHAR(20) NOT NULL,
    parent_id BIGINT DEFAULT 0,
    resource_path VARCHAR(255),
    icon VARCHAR(100),
    sort INT DEFAULT 0,
    create_time TIMESTAMP NOT NULL,
    update_time TIMESTAMP,
    CONSTRAINT uidx_code UNIQUE (code)
);

-- 用户角色关系表
CREATE TABLE IF NOT EXISTS sys_user_role (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    tenant_id BIGINT NOT NULL,
    create_time TIMESTAMP NOT NULL,
    CONSTRAINT uidx_user_role UNIQUE (user_id, role_id, tenant_id)
);

-- 角色权限关系表
CREATE TABLE IF NOT EXISTS sys_role_permission (
    id BIGSERIAL PRIMARY KEY,
    role_id BIGINT NOT NULL,
    permission_id BIGINT NOT NULL,
    tenant_id BIGINT NOT NULL,
    create_time TIMESTAMP NOT NULL,
    CONSTRAINT uidx_role_perm UNIQUE (role_id, permission_id, tenant_id)
);