<template>
  <div class="app-container">
    <el-form ref="form" :model="form" :rules="rules" label-width="120px">
      <el-form-item label="租户名称" prop="name">
        <el-input v-model="form.name" placeholder="请输入租户名称" />
      </el-form-item>
      <el-form-item label="租户代码" prop="code">
        <el-input v-model="form.code" placeholder="请输入租户代码" :disabled="isEdit" />
      </el-form-item>
      <el-form-item label="描述" prop="description">
        <el-input v-model="form.description" type="textarea" placeholder="请输入租户描述" />
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
  name: 'TenantEdit',
  data() {
    return {
      form: {
        id: undefined,
        name: '',
        code: '',
        description: '',
        status: true
      },
      rules: {
        name: [
          { required: true, message: '请输入租户名称', trigger: 'blur' },
          { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
        ],
        code: [
          { required: true, message: '请输入租户代码', trigger: 'blur' },
          { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' },
          { pattern: /^[a-z0-9_]+$/, message: '只能包含小写字母、数字和下划线', trigger: 'blur' }
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
      this.fetchTenant(id)
    }
  },
  methods: {
    fetchTenant(id) {
      this.$store.dispatch('tenant/getTenantById', id).then(tenant => {
        this.form = { ...tenant }
      })
    },
    onSubmit() {
      this.$refs.form.validate((valid) => {
        if (valid) {
          const action = this.isEdit ? 'updateTenant' : 'createTenant'
          this.$store.dispatch(`tenant/${action}`, this.form).then(() => {
            this.$message({
              message: `${this.isEdit ? '更新' : '创建'}成功`,
              type: 'success'
            })
            this.$router.push('/tenant/list')
          })
        } else {
          console.log('表单校验失败')
          return false
        }
      })
    },
    onCancel() {
      this.$router.push('/tenant/list')
    }
  }
}
</script>
