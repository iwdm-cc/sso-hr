<template>
  <div class="app-container">
    <el-form ref="form" :model="form" :rules="rules" label-width="120px">
      <el-form-item label="用户名" prop="username">
        <el-input v-model="form.username" placeholder="请输入用户名" />
      </el-form-item>
      <el-form-item label="密码" prop="password" v-if="!isEdit">
        <el-input v-model="form.password" placeholder="请输入密码" show-password />
      </el-form-item>
      <el-form-item label="姓名" prop="name">
        <el-input v-model="form.name" placeholder="请输入姓名" />
      </el-form-item>
      <el-form-item label="邮箱" prop="email">
        <el-input v-model="form.email" placeholder="请输入邮箱" />
      </el-form-item>
      <el-form-item label="电话" prop="phone">
        <el-input v-model="form.phone" placeholder="请输入电话" />
      </el-form-item>
      <el-form-item label="状态">
        <el-switch v-model="form.status" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="onSubmit">{{ isEdit ? '更新' : '创建' }}</el-button>
        <el-button @click="onCancel">取消</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
export default {
  name: 'UserEdit',
  data() {
    return {
      form: {
        id: undefined,
        username: '',
        password: '',
        name: '',
        email: '',
        phone: '',
        status: true
      },
      rules: {
        username: [
          { required: true, message: '请输入用户名', trigger: 'blur' },
          { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' },
          { min: 6, message: '密码长度不能少于6个字符', trigger: 'blur' }
        ],
        name: [
          { required: true, message: '请输入姓名', trigger: 'blur' }
        ],
        email: [
          { required: true, message: '请输入邮箱地址', trigger: 'blur' },
          { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
        ],
        phone: [
          { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
        ]
      },
      isEdit: false
    }
  },
  created() {
    const id = this.$route.params.id
    if (id) {
      this.isEdit = true
      this.fetchUser(id)
    }
  },
  methods: {
    fetchUser(id) {
      this.$store.dispatch('user/getUserById', id).then(user => {
        this.form = { ...user }
        // 密码不回显
        this.form.password = ''
      })
    },
    onSubmit() {
      this.$refs.form.validate((valid) => {
        if (valid) {
          const action = this.isEdit ? 'updateUser' : 'createUser'
          // 如果是编辑模式且密码为空，则不提交密码
          const formData = { ...this.form }
          if (this.isEdit && !formData.password) {
            delete formData.password
          }
          
          this.$store.dispatch(`user/${action}`, formData).then(() => {
            this.$message({
              message: `${this.isEdit ? '更新' : '创建'}成功`,
              type: 'success'
            })
            this.$router.push('/user/list')
          })
        } else {
          console.log('表单校验失败')
          return false
        }
      })
    },
    onCancel() {
      this.$router.push('/user/list')
    }
  }
}
</script>
