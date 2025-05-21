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
      roleOptions: []
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
      this.$store.dispatch('user/getUsers', this.listQuery).then(data => {
        this.list = data.list
        this.total = data.total
        this.listLoading = false
      })
    },
    handleFilter() {
      this.listQuery.page = 1
      this.getList()
    },
    handleCreate() {
      this.$router.push('/user/edit')
    },
    handleUpdate(row) {
      this.$router.push(`/user/edit/${row.id}`)
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
