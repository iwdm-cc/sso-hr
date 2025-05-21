<template>
  <div class="app-container">
    <el-form ref="form" :model="form" :rules="rules" label-width="120px">
      <el-form-item label="上级权限">
        <el-cascader
          v-model="form.parentId"
          :options="permissionOptions"
          :props="{ checkStrictly: true, value: 'id', label: 'name' }"
          clearable
          placeholder="请选择上级权限"
        />
      </el-form-item>
      <el-form-item label="权限名称" prop="name">
        <el-input v-model="form.name" placeholder="请输入权限名称" />
      </el-form-item>
      <el-form-item label="权限代码" prop="code">
        <el-input v-model="form.code" placeholder="请输入权限代码" />
      </el-form-item>
      <el-form-item label="资源路径" prop="resourcePath">
        <el-input v-model="form.resourcePath" placeholder="请输入资源路径" />
      </el-form-item>
      <el-form-item label="权限类型" prop="type">
        <el-select v-model="form.type" placeholder="请选择权限类型">
          <el-option
            v-for="item in typeOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="onSubmit">{{ isEdit ? '更新' : '创建' }}</el-button>
        <el-button @click="onCancel">取消</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'

export default {
  name: 'PermissionEdit',
  data() {
    return {
      form: {
        id: undefined,
        parentId: null,
        name: '',
        code: '',
        resourcePath: '',
        type: 'MENU'
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
          { required: true, message: '请选择权限类型', trigger: 'change' }
        ]
      },
      typeOptions: [
        { label: '菜单', value: 'MENU' },
        { label: '按钮', value: 'BUTTON' },
        { label: 'API', value: 'API' }
      ],
      isEdit: false
    }
  },
  computed: {
    ...mapGetters([
      'permissionList'
    ]),
    permissionOptions() {
      // 构建级联选择器的数据
      return this.buildPermissionTree(this.permissionList.filter(item => item.id !== this.form.id))
    }
  },
  created() {
    this.$store.dispatch('permission/getPermissions')
    
    const id = this.$route.params.id
    if (id) {
      this.isEdit = true
      this.fetchPermission(id)
    }
  },
  methods: {
    buildPermissionTree(permissions) {
      // 通过parentId构建树形结构
      const tree = []
      const map = {}
      
      // 先把所有节点放到map中
      permissions.forEach(item => {
        map[item.id] = {
          ...item,
          children: []
        }
      })
      
      // 构建树形结构
      permissions.forEach(item => {
        const node = map[item.id]
        if (item.parentId === 0 || !item.parentId) {
          // 根节点
          tree.push(node)
        } else {
          // 子节点，添加到父节点的children
          if (map[item.parentId]) {
            map[item.parentId].children.push(node)
          }
        }
      })
      
      return tree
    },
    fetchPermission(id) {
      this.$store.dispatch('permission/getPermissionById', id).then(permission => {
        this.form = { ...permission }
      })
    },
    onSubmit() {
      this.$refs.form.validate((valid) => {
        if (valid) {
          const action = this.isEdit ? 'updatePermission' : 'createPermission'
          this.$store.dispatch(`permission/${action}`, this.form).then(() => {
            this.$message({
              message: `${this.isEdit ? '更新' : '创建'}成功`,
              type: 'success'
            })
            this.$router.push('/permission/list')
          })
        } else {
          console.log('表单校验失败')
          return false
        }
      })
    },
    onCancel() {
      this.$router.push('/permission/list')
    }
  }
}
</script>
