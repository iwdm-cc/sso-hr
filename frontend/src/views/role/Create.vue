<template>
  <div class="page-container">
    <div class="page-header">
      <h2>创建角色</h2>
      <el-button @click="goBack">返回</el-button>
    </div>
    
    <div class="form-container">
      <el-form :model="roleForm" :rules="rules" ref="roleForm" label-width="100px">
        <el-form-item label="角色名称" prop="name">
          <el-input v-model="roleForm.name" placeholder="请输入角色名称"></el-input>
        </el-form-item>
        
        <el-form-item label="角色描述" prop="description">
          <el-input 
            v-model="roleForm.description" 
            type="textarea" 
            :rows="3" 
            placeholder="请输入角色描述">
          </el-input>
        </el-form-item>
        
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="roleForm.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
        
        <el-form-item label="权限" prop="permissionIds">
          <el-tree
            ref="permissionTree"
            :data="permissionTree"
            show-checkbox
            node-key="id"
            :props="{
              label: 'name',
              children: 'children'
            }">
          </el-tree>
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" @click="submitForm" :loading="loading">创建</el-button>
          <el-button @click="resetForm">重置</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script>
import { getAllPermissions } from '@/api/permission';

export default {
  name: 'RoleCreate',
  data() {
    return {
      roleForm: {
        name: '',
        description: '',
        status: 1
      },
      rules: {
        name: [
          { required: true, message: '请输入角色名称', trigger: 'blur' },
          { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
        ],
        description: [
          { max: 200, message: '长度不能超过 200 个字符', trigger: 'blur' }
        ],
        status: [
          { required: true, message: '请选择状态', trigger: 'change' }
        ]
      },
      permissionTree: [],
      loading: false
    };
  },
  created() {
    this.fetchPermissions();
  },
  methods: {
    // 获取所有权限
    async fetchPermissions() {
      try {
        const response = await getAllPermissions();
        this.buildPermissionTree(response.data);
      } catch (error) {
        console.error('Failed to fetch permissions:', error);
        this.$message.error('获取权限数据失败');
      }
    },
    
    // 构建权限树
    buildPermissionTree(permissions) {
      const permMap = {};
      const tree = [];
      
      // 第一次遍历，将所有权限放入map
      permissions.forEach(perm => {
        permMap[perm.id] = {
          ...perm,
          children: []
        };
      });
      
      // 第二次遍历，构建树
      permissions.forEach(perm => {
        const currentNode = permMap[perm.id];
        
        if (perm.parentId === 0 || !perm.parentId) {
          // 根节点
          tree.push(currentNode);
        } else {
          // 子节点
          const parentNode = permMap[perm.parentId];
          if (parentNode) {
            parentNode.children.push(currentNode);
          }
        }
      });
      
      this.permissionTree = tree;
    },
    
    // 提交表单
    submitForm() {
      this.$refs.roleForm.validate(async valid => {
        if (valid) {
          this.loading = true;
          
          // 获取选中的权限
          const checkedKeys = this.$refs.permissionTree.getCheckedKeys();
          const halfCheckedKeys = this.$refs.permissionTree.getHalfCheckedKeys();
          const allCheckedKeys = [...checkedKeys, ...halfCheckedKeys];
          
          try {
            // 创建角色
            const response = await this.$store.dispatch('role/createRole', this.roleForm);
            const roleId = response.data.id;
            
            // 分配权限
            if (allCheckedKeys.length > 0) {
              await this.$store.dispatch('role/assignPermissions', {
                roleId: roleId,
                permissionIds: allCheckedKeys
              });
            }
            
            this.$message.success('创建角色成功');
            this.goBack();
          } catch (error) {
            console.error('Failed to create role:', error);
            this.$message.error('创建角色失败: ' + (error.message || '未知错误'));
          } finally {
            this.loading = false;
          }
        } else {
          return false;
        }
      });
    },
    
    // 重置表单
    resetForm() {
      this.$refs.roleForm.resetFields();
      this.$refs.permissionTree.setCheckedKeys([]);
    },
    
    // 返回角色列表
    goBack() {
      this.$router.push('/role');
    }
  }
};
</script>
