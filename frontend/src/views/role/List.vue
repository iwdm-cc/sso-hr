<template>
  <div class="app-container">
    <div class="filter-container">
      <el-input
        v-model="listQuery.keyword"
        placeholder="角色名称"
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
      <el-table-column label="角色名称" align="center">
        <template slot-scope="{row}">
          <span>{{ row.name }}</span>
        </template>
      </el-table-column>
      <el-table-column label="描述" align="center">
        <template slot-scope="{row}">
          <span>{{ row.description }}</span>
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
          <el-button size="mini" type="success" @click="handleAssignPermission(row)">
            分配权限
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

    <!-- 分配权限对话框 -->
    <el-dialog title="分配权限" :visible.sync="permissionDialogVisible" width="50%">
      <el-tree
        ref="permissionTree"
        :data="permissionTreeData"
        :props="defaultProps"
        show-checkbox
        node-key="id"
        :default-checked-keys="defaultCheckedKeys"
      />
      <div slot="footer" class="dialog-footer">
        <el-button @click="permissionDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmAssignPermission">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import Pagination from '@/components/Pagination'
import waves from '@/directive/waves'
import { getRolePermissions } from '@/api/role'

export default {
  name: 'RoleList',
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
      permissionDialogVisible: false,
      currentRoleId: null,
      defaultCheckedKeys: [],
      permissionTreeData: [],
      defaultProps: {
        children: 'children',
        label: 'name'
      }
    }
  },
  computed: {
    ...mapGetters([
      'permissionList'
    ])
  },
  created() {
    this.getList()
    this.$store.dispatch('permission/getPermissions')
  },
  methods: {
    getList() {
      this.listLoading = true
      this.$store.dispatch('role/getRoles', this.listQuery).then(data => {
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
      this.$router.push('/role/edit')
    },
    handleUpdate(row) {
      this.$router.push(`/role/edit/${row.id}`)
    },
    handleDelete(row) {
      this.$confirm('确认删除该角色?', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$store.dispatch('role/deleteRole', row.id).then(() => {
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
      this.$store.dispatch('role/updateRole', {
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
    buildPermissionTree(permissions) {
      // 通过权限代码路径构建树形结构
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
    handleAssignPermission(row) {
      this.currentRoleId = row.id
      this.permissionTreeData = this.buildPermissionTree(this.permissionList)
      
      // 获取该角色已有的权限
      getRolePermissions(row.id).then(response => {
        const { data } = response
        this.defaultCheckedKeys = data.map(item => item.id)
        this.permissionDialogVisible = true
      })
    },
    confirmAssignPermission() {
      const checkedKeys = this.$refs.permissionTree.getCheckedKeys()
      const halfCheckedKeys = this.$refs.permissionTree.getHalfCheckedKeys()
      const permissionIds = [...checkedKeys, ...halfCheckedKeys]
      
      this.$store.dispatch('role/assignPermissions', {
        roleId: this.currentRoleId,
        permissionIds
      }).then(() => {
        this.permissionDialogVisible = false
        this.$message({
          message: '权限分配成功',
          type: 'success'
        })
      })
    }
  }
}
</script>
