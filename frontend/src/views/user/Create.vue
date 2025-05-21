<template>
  <div class="page-container">
    <div class="page-header">
      <h2>创建用户</h2>
      <el-button @click="goBack">返回</el-button>
    </div>
    
    <div class="form-container">
      <el-form :model="userForm" :rules="rules" ref="userForm" label-width="100px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="userForm.username" placeholder="请输入用户名"></el-input>
        </el-form-item>
        
        <el-form-item label="密码" prop="password">
          <el-input v-model="userForm.password" type="password" placeholder="请输入密码"></el-input>
        </el-form-item>
        
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="userForm.confirmPassword" type="password" placeholder="请再次输入密码"></el-input>
        </el-form-item>
        
        <el-form-item label="姓名" prop="name">
          <el-input v-model="userForm.name" placeholder="请输入姓名"></el-input>
        </el-form-item>
        
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="userForm.email" placeholder="请输入邮箱"></el-input>
        </el-form-item>
        
        <el-form-item label="电话" prop="phone">
          <el-input v-model="userForm.phone" placeholder="请输入电话"></el-input>
        </el-form-item>
        
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="userForm.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
        
        <el-form-item label="角色" prop="roleIds">
          <el-select v-model="userForm.roleIds" multiple placeholder="请选择角色" style="width: 100%">
            <el-option
              v-for="item in roles"
              :key="item.id"
              :label="item.name"
              :value="item.id">
            </el-option>
          </el-select>
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
import { getAllRoles } from '@/api/role';

export default {
  name: 'UserCreate',
  data() {
    // 确认密码验证
    const validateConfirmPassword = (rule, value, callback) => {
      if (value !== this.userForm.password) {
        callback(new Error('两次输入的密码不一致'));
      } else {
        callback();
      }
    };
    
    return {
      userForm: {
        username: '',
        password: '',
        confirmPassword: '',
        name: '',
        email: '',
        phone: '',
        status: 1,
        roleIds: []
      },
      rules: {
        username: [
          { required: true, message: '请输入用户名', trigger: 'blur' },
          { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' },
          { min: 6, message: '密码长度至少6个字符', trigger: 'blur' }
        ],
        confirmPassword: [
          { required: true, message: '请再次输入密码', trigger: 'blur' },
          { validator: validateConfirmPassword, trigger: 'blur' }
        ],
        name: [
          { required: true, message: '请输入姓名', trigger: 'blur' }
        ],
        email: [
          { required: true, message: '请输入邮箱', trigger: 'blur' },
          { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
        ],
        phone: [
          { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
        ],
        status: [
          { required: true, message: '请选择状态', trigger: 'change' }
        ]
      },
      roles: [],
      loading: false
    };
  },
  created() {
    this.fetchRoles();
  },
  methods: {
    // 获取所有角色
    async fetchRoles() {
      try {
        const response = await getAllRoles();
        this.roles = response.data;
      } catch (error) {
        console.error('Failed to fetch roles:', error);
        this.$message.error('获取角色数据失败');
      }
    },
    
    // 提交表单
    submitForm() {
      this.$refs.userForm.validate(async valid => {
        if (valid) {
          this.loading = true;
          try {
            await this.$store.dispatch('user/createUser', this.userForm);
            this.$message.success('创建用户成功');
            this.goBack();
          } catch (error) {
            console.error('Failed to create user:', error);
            this.$message.error('创建用户失败: ' + (error.message || '未知错误'));
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
      this.$refs.userForm.resetFields();
    },
    
    // 返回用户列表
    goBack() {
      this.$router.push('/user');
    }
  }
};
</script>
