<template>
  <div class="role-form">
    <div class="page-header">
      <div class="page-title">{{ isEdit ? '编辑角色' : '创建角色' }}</div>
      <el-button @click="goBack">返回</el-button>
    </div>

    <el-card>
      <el-form :model="form" :rules="rules" ref="form" label-width="100px">
        <el-form-item label="角色名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入角色名称"></el-input>
        </el-form-item>
        <el-form-item label="角色编码" prop="code">
          <el-input v-model="form.code" placeholder="请输入角色编码"></el-input>
        </el-form-item>
        <el-form-item label="角色描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="4" placeholder="请输入角色描述"></el-input>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio label="1">启用</el-radio>
            <el-radio label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="权限">
          <el-tree
            ref="permTree"
            :data="permissionTree"
            show-checkbox
            node-key="id"
            :default-checked-keys="form.permissionIds"
            :props="{ label: 'name', children: 'children' }">
          </el-tree>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="submitForm" :loading="loading">保存</el-button>
          <el-button @click="goBack">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import { getRole, addRole, updateRole } from '@/api/role';
import { getPermissionTree } from '@/api/permission';

export default {
  name: 'RoleForm',
  data() {
    return {
      // 是否编辑模式
      isEdit: false,
      // 表单数据
      form: {
        id: undefined,
        name: '',
        code: '',
        description: '',
        status: '1',
        permissionIds: []
      },
      // 权限树数据
      permissionTree: [],
      // 表单验证规则
      rules: {
        name: [
          { required: true, message: '请输入角色名称', trigger: 'blur' }
        ],
        code: [
          { required: true, message: '请输入角色编码', trigger: 'blur' },
          { pattern: /^[A-Z_]+$/, message: '角色编码只能包含大写字母和下划线', trigger: 'blur' }
        ],
        status: [
          { required: true, message: '请选择状态', trigger: 'change' }
        ]
      },
      // 加载状态
      loading: false
    };
  },
  created() {
    this.loadPermissionTree();
    // 根据路由参数判断是否为编辑模式
    const roleId = this.$route.params.id;
    if (roleId) {
      this.isEdit = true;
      this.getRoleInfo(roleId);
    }
  },
  methods: {
    // 获取角色信息
    async getRoleInfo(roleId) {
      try {
        const response = await getRole(roleId);
        const role = response.data;
        this.form = {
          ...this.form,
          ...role
        };
      } catch (error) {
        console.error('获取角色信息失败:', error);
        this.$message.error('获取角色信息失败');
        this.goBack();
      }
    },
    
    // 加载权限树
    async loadPermissionTree() {
      try {
        const response = await getPermissionTree();
        this.permissionTree = response.data;
      } catch (error) {
        console.error('获取权限树失败:', error);
        this.$message.error('获取权限树失败');
      }
    },
    
    // 表单提交
    submitForm() {
      this.$refs.form.validate(async valid => {
        if (valid) {
          this.loading = true;
          
          // 获取选中的权限ID
          const checkedKeys = this.$refs.permTree.getCheckedKeys();
          const halfCheckedKeys = this.$refs.permTree.getHalfCheckedKeys();
          this.form.permissionIds = [...checkedKeys, ...halfCheckedKeys];
          
          try {
            if (this.isEdit) {
              await updateRole(this.form);
              this.$message.success('修改角色成功');
            } else {
              await addRole(this.form);
              this.$message.success('创建角色成功');
            }
            this.goBack();
          } catch (error) {
            console.error('保存角色失败:', error);
            let errMsg = '保存角色失败';
            if (error.response && error.response.data && error.response.data.message) {
              errMsg = error.response.data.message;
            }
            this.$message.error(errMsg);
          } finally {
            this.loading = false;
          }
        }
      });
    },
    
    // 返回角色列表
    goBack() {
      this.$router.push('/roles');
    }
  }
};
</script>

<style scoped>
.role-form {
  padding-bottom: 20px;
}
</style>
