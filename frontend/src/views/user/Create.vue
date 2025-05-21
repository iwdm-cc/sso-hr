<template>
  <div class="page-container">
    <div class="page-header">
      <h2>创建用户</h2>
      <el-button type="primary" @click="goBack">返回用户列表</el-button>
    </div>
    
    <el-form :model="userForm" :rules="userRules" ref="userForm" label-width="100px" class="user-form">
      <el-form-item label="用户名" prop="username">
        <el-input v-model="userForm.username" placeholder="请输入用户名"></el-input>
      </el-form-item>
      
      <el-form-item label="密码" prop="password">
        <el-input v-model="userForm.password" type="password" placeholder="请输入密码"></el-input>
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
      
      <el-form-item>
        <el-button type="primary" @click="submitForm" :loading="submitting">创建</el-button>
        <el-button @click="resetForm">重置</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
export default {
  name: 'UserCreate',
  data() {
    return {
      userForm: {
        username: '',
        password: '',
        name: '',
        email: '',
        phone: '',
        status: 1
      },
      userRules: {
        username: [
          { required: true, message: '请输入用户名', trigger: 'blur' },
          { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' },
          { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur' }
        ],
        name: [
          { required: true, message: '请输入姓名', trigger: 'blur' }
        ],
        email: [
          { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
        ]
      },
      submitting: false
    };
  },
  methods: {
    goBack() {
      this.$router.push('/user');
    },
    submitForm() {
      this.$refs.userForm.validate(valid => {
        if (valid) {
          this.submitting = true;
          this.$store.dispatch('user/createUser', this.userForm)
            .then(() => {
              this.$message.success('用户创建成功');
              this.$router.push('/user');
            })
            .catch(error => {
              console.error('创建用户失败:', error);
              this.$message.error('创建用户失败');
            })
            .finally(() => {
              this.submitting = false;
            });
        } else {
          return false;
        }
      });
    },
    resetForm() {
      this.$refs.userForm.resetFields();
    }
  }
};
</script>

<style scoped>
.user-form {
  max-width: 500px;
  margin-top: 20px;
}
</style>