<template>
  <div class="page-container">
    <div class="page-header">
      <h2>创建租户</h2>
      <el-button @click="goBack">返回</el-button>
    </div>
    
    <div class="form-container">
      <el-form :model="tenantForm" :rules="rules" ref="tenantForm" label-width="100px">
        <el-form-item label="租户名称" prop="name">
          <el-input v-model="tenantForm.name" placeholder="请输入租户名称"></el-input>
        </el-form-item>
        
        <el-form-item label="租户描述" prop="description">
          <el-input 
            v-model="tenantForm.description" 
            type="textarea" 
            :rows="3" 
            placeholder="请输入租户描述">
          </el-input>
        </el-form-item>
        
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="tenantForm.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
        
        <el-form-item label="管理员信息" style="margin-bottom: 0;"></el-form-item>
        
        <el-form-item label="用户名" prop="adminUsername">
          <el-input v-model="tenantForm.adminUsername" placeholder="请输入管理员用户名"></el-input>
        </el-form-item>
        
        <el-form-item label="密码" prop="adminPassword">
          <el-input v-model="tenantForm.adminPassword" type="password" placeholder="请输入管理员密码"></el-input>
        </el-form-item>
        
        <el-form-item label="姓名" prop="adminName">
          <el-input v-model="tenantForm.adminName" placeholder="请输入管理员姓名"></el-input>
        </el-form-item>
        
        <el-form-item label="邮箱" prop="adminEmail">
          <el-input v-model="tenantForm.adminEmail" placeholder="请输入管理员邮箱"></el-input>
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
export default {
  name: 'TenantCreate',
  data() {
    return {
      tenantForm: {
        name: '',
        description: '',
        status: 1,
        adminUsername: '',
        adminPassword: '',
        adminName: '',
        adminEmail: ''
      },
      rules: {
        name: [
          { required: true, message: '请输入租户名称', trigger: 'blur' },
          { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
        ],
        description: [
          { max: 200, message: '长度不能超过 200 个字符', trigger: 'blur' }
        ],
        status: [
          { required: true, message: '请选择状态', trigger: 'change' }
        ],
        adminUsername: [
          { required: true, message: '请输入管理员用户名', trigger: 'blur' },
          { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
        ],
        adminPassword: [
          { required: true, message: '请输入管理员密码', trigger: 'blur' },
          { min: 6, message: '密码长度至少6个字符', trigger: 'blur' }
        ],
        adminName: [
          { required: true, message: '请输入管理员姓名', trigger: 'blur' }
        ],
        adminEmail: [
          { required: true, message: '请输入管理员邮箱', trigger: 'blur' },
          { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
        ]
      },
      loading: false
    };
  },
  methods: {
    // 提交表单
    submitForm() {
      this.$refs.tenantForm.validate(async valid => {
        if (valid) {
          this.loading = true;
          try {
            await this.$store.dispatch('tenant/createTenant', this.tenantForm);
            this.$message.success('创建租户成功');
            this.goBack();
          } catch (error) {
            console.error('Failed to create tenant:', error);
            this.$message.error('创建租户失败: ' + (error.message || '未知错误'));
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
      this.$refs.tenantForm.resetFields();
    },
    
    // 返回租户列表
    goBack() {
      this.$router.push('/tenant');
    }
  }
};
</script>
