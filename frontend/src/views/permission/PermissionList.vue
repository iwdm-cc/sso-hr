<template>
  <div class="permission-list">
    <div class="page-header">
      <div class="page-title">权限管理</div>
      <el-button type="primary" @click="handleCreate" v-if="hasPermission('permission:create')">
        <i class="el-icon-plus"></i> 创建权限
      </el-button>
    </div>

    <el-card class="filter-container">
      <el-form :inline="true" :model="queryParams" ref="queryForm" size="small">
        <el-form-item label="权限名称" prop="name">
          <el-input v-model="queryParams.name" placeholder="请输入权限名称" clearable></el-input>
        </el-form-item>
        <el-form-item label="权限类型" prop="type">
          <el-select v-model="queryParams.type" placeholder="权限类型" clearable>
            <el-option label="菜单" value="menu"></el-option>
            <el-option label="按钮" value="button"></el-option>
            <el-option label="接口" value="api"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">
            <i class="el-icon-search"></i> 搜索
          </el-button>
          <el-button @click="resetQuery">
            <i class="el-icon-refresh"></i> 重置
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="table-container">
      <el-table
        v-loading="loading"
        :data="permissionList"
        row-key="id"
        border
        default-expand-all
        :tree-props="{children: 'children', hasChildren: 'hasChildren'}">
        <el-table-column prop="name" label="权限名称" width="200"></el-table-column>
        <el-table-column prop="code" label="权限标识" width="180"></el-table-column>
        <el-table-column prop="type" label="权限类型" width="100">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.type === 'menu'" type="success">菜单</el-tag>
            <el-tag v-else-if="scope.row.type === 'button'" type="warning">按钮</el-tag>
            <el-tag v-else-if="scope.row.type === 'api'" type="info">接口</el-tag>
            <span v-else>{{ scope.row.type }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="path" label="资源路径"></el-table-column>
        <el-table-column prop="icon" label="图标" width="80">
          <template slot-scope="scope">
            <i v-if="scope.row.icon" :class="scope.row.icon"></i>
          </template>
        </el-table-column>
        <el-table-column prop="sort" label="排序" width="80"></el-table-column>
        <el-table-column label="操作" width="180" align="center">
          <template slot-scope="scope">
            <el-button
              size="mini"
              type="text"
              @click="handleCreate(scope.row)"
              v-if="hasPermission('permission:create')">
              新增
            </el-button>
            <el-button
              size="mini"
              type="text"
              @click="handleEdit(scope.row)"
              v-if="hasPermission('permission:update')">
              编辑
            </el-button>
            <el-button
              size="mini"
              type="text"
              @click="handleDelete(scope.row)"
              class="delete-btn"
              v-if="hasPermission('permission:delete')">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script>
import { mapGetters } from 'vuex';
import { listPermissions, deletePermission } from '@/api/permission';

export default {
  name: 'PermissionList',
  data() {
    return {
      // 查询参数
      queryParams: {
        name: '',
        type: ''
      },
      // 权限列表
      permissionList: [],
      // 加载状态
      loading: false
    };
  },
  computed: {
    ...mapGetters(['hasPermission', 'currentTenantId'])
  },
  created() {
    this.getList();
  },
  methods: {
    // 获取权限列表
    async getList() {
      this.loading = true;
      try {
        const response = await listPermissions(this.queryParams);
        this.permissionList = response.data;
      } catch (error) {
        console.error('获取权限列表失败:', error);
        this.$message.error('获取权限列表失败');
      } finally {
        this.loading = false;
      }
    },
    
    // 搜索按钮点击事件
    handleQuery() {
      this.getList();
    },
    
    // 重置搜索表单
    resetQuery() {
      this.$refs.queryForm.resetFields();
      this.handleQuery();
    },
    
    // 新建权限
    handleCreate(row) {
      if (row && row.id) {
        // 如果有行数据，表示添加子权限
        this.$router.push({
          path: '/permissions/create',
          query: { parentId: row.id }
        });
      } else {
        // 否则添加顶级权限
        this.$router.push('/permissions/create');
      }
    },
    
    // 编辑权限
    handleEdit(row) {
      this.$router.push(`/permissions/edit/${row.id}`);
    },
    
    // 删除权限
    handleDelete(row) {
      this.$confirm(`确认删除权限"${row.name}"吗?`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          await deletePermission(row.id);
          this.$message.success('删除成功');
          this.getList();
        } catch (error) {
          console.error('删除权限失败:', error);
          this.$message.error('删除权限失败');
        }
      }).catch(() => {
        this.$message.info('已取消删除');
      });
    }
  }
};
</script>

<style scoped>
.filter-container {
  margin-bottom: 20px;
}

.table-container {
  margin-bottom: 20px;
}

.delete-btn {
  color: #F56C6C;
}
</style>
