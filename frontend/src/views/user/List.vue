<template>
  <div class="app-container">
    <div class="filter-container">
      <el-input
        v-model="listQuery.keyword"
        placeholder="用户名/姓名/邮箱"
        style="width: 200px;"
        class="filter-item"
        @keyup.enter.native="handleFilter"
      />
      <el-select v-model="listQuery.status" placeholder="状态" clearable style="width: 120px" class="filter-item">
        <el-option v-for="item in statusOptions" :key="item.value" :label="item.label" :value="item.value" />
      </el-select>
      <el-button v-waves class="filter-item" type="primary" icon="el-icon-search" @click="handleFilter">
        搜索
      </el-button>
      <el-button class="filter-item" style="margin-left: 10px;" type="primary" icon="el-icon-plus" @click="handleCreate">
        添加
      </el-button>
    </div>

    <el-table
      v-loading="listLoading"
      :data="list"
      element-loading-text="加载中..."
      border
      fit
      highlight-current-row
    >
      <el-table-column label="ID" align="center" width="80">
        <template slot-scope="{row}">
          <span>{{ row.id }}</span>
        </template>
      </el-table-column>
      <el-table-column label="用户名" align="center">
        <template slot-scope="{row}">
          <span>{{ row.username }}</span>
        </template>
      </el-table-column>
      <el-table-column label="姓名" align="center">
        <template slot-scope="{row}">
          <span>{{ row.name }}</span>
        </template>
      </el-table-column>
      <el-table-column label="邮箱" align="center">
        <template slot-scope="{row}">
          <span>{{ row.email }}</span>
        </template>
      </el-table-column>
      <el-table-column label="电话" align="center">
        <template slot-scope="{row}">
          <span>{{ row.phone }}</span>
        </template>
      </el-table-column>
      <el-table-column label="状态" class-name="status-col" align="center">
        <template slot-scope="{row}">
          <el-tag :type="row.status | statusFilter">
            {{ row.status ? '启用' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" width="180">
        <template slot-scope="{row}">
          <span>{{ row.createTime }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="250" class-name="small-padding fixed-width">
        <template slot-scope="{row}">
          <el-button type="primary" size="mini" @click="handleUpdate(row)">
            编辑
          </el-button>
          <el-button size="mini" type="success" @click="handleAssignRole(row)">
            分配角色
          </el-button>
          <el-button v-if="row.status" size="mini" type="warning" @click="handleModifyStatus(row, false)">
            禁用
          </el-button>
          <el-button v-else size="mini" type="info" @click="handleModifyStatus(row, true)">
            启用
          </el-button>
          <el-button size="mini" type="danger" @click="handleDelete(row)">
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="listQuery.page"
      :limit.sync="listQuery.pageSize"
      @pagination="getList"
    />

    <!-- 分配角色对话框 -->
    <el-dialog title="分配角色" :visible.sync="roleDialogVisible" width="40%">
      <el-transfer
        v-model="selectedRoles"
        :data="roleOptions"
        :titles="['可选角色', '已选角色']"
        :props="{
          key: 'id',
          label: 'name'
        }"
      />
      <div slot="footer" class="dialog-footer">
        <el-button @click="roleDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmAssignRole">确定</el-button>
      </div>
    </el-dialog>
    
    <!-- 创建/编辑用户对话框 -->
    <el-dialog :title="dialogTitle" :visible.sync="userDialogVisible" width="40%">
      <el-form :model="userForm" :rules="userRules" ref="userForm" label-width="100px" class="user-form">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="userForm.username" placeholder="请输入用户名"></el-input>
        </el-form-item>
        
        <el-form-item label="密码" prop="password" v-if="isCreate">
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
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="userDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitUserForm" :loading="submitting">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import Pagination from '@/components/Pagination'
import waves from '@/directive/waves'

export default {
  name: 'UserList',
  components: { Pagination },
  directives: { waves },
  filters: {
    statusFilter(status) {
      const statusMap = {
        true: 'success',
        false: 'info'
      }
      return statusMap[status]
    }
  },
  data() {
    return {
      list: [],
      total: 0,
      listLoading: true,
      listQuery: {
        page: 1,
        pageSize: 10,
        keyword: '',
        status: ''
      },
      statusOptions: [
        { label: '启用', value: true },
        { label: '禁用', value: false }
      ],
      roleDialogVisible: false,
      currentUserId: null,
      selectedRoles: [],
      roleOptions: [],
      // 用户表单相关
      userDialogVisible: false,
      isCreate: true,
      dialogTitle: '创建用户',
      submitting: false,
      userForm: {
        id: undefined,
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
      }
    }
  },
  computed: {
    ...mapGetters([
      'roles'
    ])
  },
  created() {
    this.getList()
    this.$store.dispatch('role/getRoles')
  },
  methods: {
    getList() {
      this.listLoading = true
      const params = {
        username: this.listQuery.keyword,
        name: this.listQuery.keyword,
        status: this.listQuery.status,
        current: this.listQuery.page,
        size: this.listQuery.pageSize
      }
      this.$store.dispatch('user/getUsers', params).then(data => {
        console.log('用户列表数据:', data)
        // 确保从后端返回的数据正确绑定到视图
        if (data && data.list) {
          // 处理status值，确保是布尔类型
          this.list = data.list.map(item => {
            return {
              ...item,
              status: item.status === 1 || item.status === true
            }
          })
          this.total = data.total || 0
        } else {
          this.list = []
          this.total = 0
        }
        this.listLoading = false
      }).catch(error => {
        console.error('加载用户列表失败:', error)
        this.listLoading = false
        this.$message.error('加载用户列表失败')
      })
    },
    handleFilter() {
      this.listQuery.page = 1
      this.getList()
    },
    handleCreate() {
      this.isCreate = true
      this.dialogTitle = '创建用户'
      this.userForm = {
        id: undefined,
        username: '',
        password: '',
        name: '',
        email: '',
        phone: '',
        status: 1
      }
      this.userDialogVisible = true
      // 在弹窗打开后重置表单验证
      this.$nextTick(() => {
        if (this.$refs.userForm) {
          this.$refs.userForm.clearValidate()
        }
      })
    },
    handleUpdate(row) {
      this.isCreate = false
      this.dialogTitle = '编辑用户'
      // 获取用户详情
      this.$store.dispatch('user/getUserById', row.id).then(user => {
        this.userForm = { ...user, password: '' }  // 不回显密码
        this.userDialogVisible = true
        // 在弹窗打开后重置表单验证
        this.$nextTick(() => {
          if (this.$refs.userForm) {
            this.$refs.userForm.clearValidate()
          }
        })
      }).catch(error => {
        console.error('获取用户详情失败:', error)
        this.$message.error('获取用户详情失败')
      })
    },
    submitUserForm() {
      this.$refs.userForm.validate(valid => {
        if (valid) {
          this.submitting = true
          const action = this.isCreate ? 'user/createUser' : 'user/updateUser'
          
          this.$store.dispatch(action, this.userForm)
            .then(() => {
              this.$message.success(this.isCreate ? '用户创建成功' : '用户编辑成功')
              this.userDialogVisible = false
              this.getList() // 刷新列表
            })
            .catch(error => {
              console.error(this.isCreate ? '创建用户失败:' : '编辑用户失败:', error)
              this.$message.error(this.isCreate ? '创建用户失败' : '编辑用户失败')
            })
            .finally(() => {
              this.submitting = false
            })
        }
      })
    },
    handleDelete(row) {
      this.$confirm('确认删除该用户?', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$store.dispatch('user/deleteUser', row.id).then(() => {
          this.getList()
          this.$message({
            type: 'success',
            message: '删除成功!'
          })
        })
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消删除'
        })
      })
    },
    handleModifyStatus(row, status) {
      this.$store.dispatch('user/updateUser', {
        id: row.id,
        status
      }).then(() => {
        row.status = status
        this.$message({
          message: '状态更新成功',
          type: 'success'
        })
      })
    },
    handleAssignRole(row) {
      this.currentUserId = row.id
      this.roleOptions = this.roles.map(role => ({
        id: role.id,
        name: role.name
      }))
      // 获取用户当前角色
      this.$store.dispatch('user/getUserById', row.id).then(user => {
        this.selectedRoles = user.roleIds || []
        this.roleDialogVisible = true
      })
    },
    confirmAssignRole() {
      this.$store.dispatch('user/assignRoles', {
        userId: this.currentUserId,
        roleIds: this.selectedRoles
      }).then(() => {
        this.roleDialogVisible = false
        this.$message({
          message: '角色分配成功',
          type: 'success'
        })
      })
    }
  }
}
</script>
