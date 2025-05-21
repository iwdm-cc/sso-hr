<template>
  <div class="app-container">
    <el-form ref="form" :model="form" :rules="rules" label-width="120px">
      <el-form-item label="角色名称" prop="name">
        <el-input v-model="form.name" placeholder="请输入角色名称" />
      </el-form-item>
      <el-form-item label="描述" prop="description">
        <el-input v-model="form.description" type="textarea" placeholder="请输入角色描述" />
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
  name: 'RoleEdit',
  data() {
    return {
      form: {
        id: undefined,
        name: '',
        description: '',
        status: true
      },
      rules: {
        name: [
          { required: true, message: '请输入角色名称', trigger: 'blur' },
          { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' }
        ],
        description: [
          { max: 200, message: '长度不能超过200个字符', trigger: 'blur' }
        ]
      },
      isEdit: false
    }
  },
  created() {
    const id = this.$route.params.id
    if (id) {
      this.isEdit = true
      this.fetchRole(id)
    }
  },
  methods: {
    fetchRole(id) {
      this.$store.dispatch('role/getRoleById', id).then(role => {
        this.form = { ...role }
      })
    },
    onSubmit() {
      this.$refs.form.validate((valid) => {
        if (valid) {
          const action = this.isEdit ? 'updateRole' : 'createRole'
          this.$store.dispatch(`role/${action}`, this.form).then(() => {
            this.$message({
              message: `${this.isEdit ? '更新' : '创建'}成功`,
              type: 'success'
            })
            this.$router.push('/role/list')
          })
        } else {
          console.log('表单校验失败')
          return false
        }
      })
    },
    onCancel() {
      this.$router.push('/role/list')
    }
  }
}
</script>
