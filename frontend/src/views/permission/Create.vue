<template>
  <div class="page-container">
    <div class="page-header">
      <h2>创建权限</h2>
      <el-button @click="goBack">返回</el-button>
    </div>
    
    <div class="form-container">
      <el-form :model="permissionForm" :rules="rules" ref="permissionForm" label-width="100px">
        <el-form-item label="权限名称" prop="name">
          <el-input v-model="permissionForm.name" placeholder="请输入权限名称"></el-input>
        </el-form-item>
        
        <el-form-item label="权限代码" prop="code">
          <el-input v-model="permissionForm.code" placeholder="请输入权限代码"></el-input>
        </el-form-item>
        
        <el-form-item label="类型" prop="type">
          <el-select v-model="permissionForm.type" placeholder="请选择类型" style="width: 100%">
            <el-option label="菜单" value="menu"></el-option>
            <el-option label="按钮" value="button"></el-option>
            <el-option label="API" value="api"></el-option>
          </el-select>
        </el-form-item>
        
        <el-form-item label="资源路径" prop="url">
          <el-input v-model="permissionForm.url" placeholder="请输入资源路径"></el-input>
        </el-form-item>
        
        <el-form-item label="父权限" prop="parentId">
          <el-select v-model="permissionForm.parentId" placeholder="请选择父权限" style="width: 100%" filterable clearable>
            <el-option :key="0" label="无" :value="0"></el-option>
            <el-option
              v-for="item in parentPermissions"
              :key="item.id"
              :label="item.name"
              :value="item.id">
            </el-option>
          </el-select>
        </el-form-item>
        
        <el-form-item label="排序" prop="sort">
          <el-input-number v-model="permissionForm.sort" :min="0" :max="999"></el-input-number>
        </el-form-item>
        
        <el-form-item label="图标" prop="icon" v-if="permissionForm.type === 'menu'">
          <el-input v-model="permissionForm.icon" placeholder="请输入图标类名"></el-input>
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
  name: 'PermissionCreate',
  data() {
    return {
      permissionForm: {
        name: '',
        code: '',
        type: 'menu',
        url: '',
        parentId: 0,
        sort: 0,
        icon: ''
      },
      rules: {
        name: [
          { required: true, message: '请输入权限名称', trigger: 'blur' },
          { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
        ],
        code: [
          { required: true, message: '请输入权限代码', trigger: 'blur' },
          { min: 2, max: 100, message: '长度在 2 到 100 个字符', trigger: 'blur' }
        ],
        type: [
          { required: true, message: '请选择类型', trigger: 'change' }
        ],
        url: [
          { required: true, message: '请输入资源路径', trigger: 'blur' }
        ],
        sort: [
          { required: true, message: '请输入排序', trigger: 'blur' }
        ]
      },
      parentPermissions: [],
      loading: false
    };
  },
  created() {
    this.fetchParentPermissions();
  },
  methods: {
    // 获取可作为父权限的权限列表
    async fetchParentPermissions() {
      try {
        const response = await getAllPermissions();
        // 只有菜单类型的权限才能作为父权限
        this.parentPermissions = response.data.filter(p => p.type === 'menu');
      } catch (error) {
        console.error('Failed to fetch parent permissions:', error);
        this.$message.error('获取父权限数据失败');
      }
    },
    
    // 提交表单
    submitForm() {
      this.$refs.permissionForm.validate(async valid => {
        if (valid) {
          this.loading = true;
          try {
            await this.$store.dispatch('permission/createPermission', this.permissionForm);
            this.$message.success('创建权限成功');
            this.goBack();
          } catch (error) {
            console.error('Failed to create permission:', error);
            this.$message.error('创建权限失败: ' + (error.message || '未知错误'));
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
      this.$refs.permissionForm.resetFields();
    },
    
    // 返回权限列表
    goBack() {
      this.$router.push('/permission');
    }
  }
};
</script>
