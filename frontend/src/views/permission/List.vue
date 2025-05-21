<template>
  <div class="app-container">
    <div class="filter-container">
      <el-input
        v-model="listQuery.keyword"
        placeholder="权限名称/代码"
        style="width: 200px;"
        class="filter-item"
        @keyup.enter.native="handleFilter"
      />
      <el-select v-model="listQuery.type" placeholder="类型" clearable style="width: 120px" class="filter-item">
        <el-option v-for="item in typeOptions" :key="item.value" :label="item.label" :value="item.value" />
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
      row-key="id"
      :tree-props="{children: 'children', hasChildren: 'hasChildren'}"
    >
      <el-table-column label="ID" align="center" width="80">
        <template slot-scope="{row}">
          <span>{{ row.id }}</span>
        </template>
      </el-table-column>
      <el-table-column label="权限名称" align="center">
        <template slot-scope="{row}">
          <span>{{ row.name }}</span>
        </template>
      </el-table-column>
      <el-table-column label="权限代码" align="center">
        <template slot-scope="{row}">
          <span>{{ row.code }}</span>
        </template>
      </el-table-column>
      <el-table-column label="资源路径" align="center">
        <template slot-scope="{row}">
          <span>{{ row.resourcePath }}</span>
        </template>
      </el-table-column>
      <el-table-column label="类型" align="center">
        <template slot-scope="{row}">
          <el-tag>{{ row.type | typeFilter }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="200" class-name="small-padding fixed-width">
        <template slot-scope="{row}">
          <el-button type="primary" size="mini" @click="handleUpdate(row)">
            编辑
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
  </div>
</template>

<script>
import Pagination from '@/components/Pagination'
import waves from '@/directive/waves'

export default {
  name: 'PermissionList',
  components: { Pagination },
  directives: { waves },
  filters: {
    typeFilter(type) {
      const typeMap = {
        'MENU': '菜单',
        'BUTTON': '按钮',
        'API': 'API'
      }
      return typeMap[type] || type
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
        type: ''
      },
      typeOptions: [
        { label: '菜单', value: 'MENU' },
        { label: '按钮', value: 'BUTTON' },
        { label: 'API', value: 'API' }
      ]
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.listLoading = true
      this.$store.dispatch('permission/getPermissions', this.listQuery).then(data => {
        this.list = this.buildPermissionTree(data)
        this.total = data.length
        this.listLoading = false
      })
    },
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
    handleFilter() {
      this.listQuery.page = 1
      this.getList()
    },
    handleCreate() {
      this.$router.push('/permission/edit')
    },
    handleUpdate(row) {
      this.$router.push(`/permission/edit/${row.id}`)
    },
    handleDelete(row) {
      this.$confirm('确认删除该权限?', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$store.dispatch('permission/deletePermission', row.id).then(() => {
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
    }
  }
}
</script>
