<template>
  <div class="role-list">
    <div class="page-header">
      <div class="page-title">角色管理</div>
      <el-button type="primary" @click="handleCreate" v-if="hasPermission('role:create')">
        <i class="el-icon-plus"></i> 创建角色
      </el-button>
    </div>

    <el-card class="filter-container">
      <el-form :inline="true" :model="queryParams" ref="queryForm" size="small">
        <el-form-item label="角色名称" prop="name">
          <el-input v-model="queryParams.name" placeholder="请输入角色名称" clearable></el-input>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="queryParams.status" placeholder="角色状态" clearable>
            <el-option label="启用" value="1"></el-option>
            <el-option label="禁用" value="0"></el-option>
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
        :data="roleList"
        border
        style="width: 100%">
        <el-table-column prop="name" label="角色名称" width="180"></el-table-column>
        <el-table-column prop="code" label="角色编码" width="180"></el-table-column>
        <el-table-column prop="description" label="描述"></el-table-column>
        <el-table-column label="状态" width="100">
          <template slot-scope="scope">
            <el-tag :type="scope.row.status === '1' ? 'success' : 'danger'">
              {{ scope.row.status === '1' ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" width="180">
          <template slot-scope="scope">
            <span>{{ scope.row.createTime }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="250" align="center">
          <template slot-scope="scope">
            <el-button
              size="mini"
              type="text"
              @click="handleEdit(scope.row)"
              v-if="hasPermission('role:update')">
              编辑
            </el-button>
            <el-button
              size="mini"
              type="text"
              @click="handleAssignPermission(scope.row)"
              v-if="hasPermission('role:assign')">
              分配权限
            </el-button>
            <el-button
              size="mini"
              type="text"
              @click="handleDelete(scope.row)"
              class="delete-btn"
              v-if="hasPermission('role:delete')">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination
          background
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="queryParams.pageNum"
          :page-sizes="[10, 20, 30, 50]"
          :page-size="queryParams.pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total">
        </el-pagination>
      </div>
    </el-card>

    <!-- 权限分配对话框 -->
    <el-dialog title="分配权限" :visible.sync="permDialogVisible" width="600px">
      <el-tree
        ref="permTree"
        :data="permissionTree"
        show-checkbox
        node-key="id"
        :default-checked-keys="selectedPermissions"
        :props="{ label: 'name', children: 'children' }">
      </el-tree>
      <div slot="footer" class="dialog-footer">
        <el-button @click="permDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="confirmPermAssign">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { mapGetters } from 'vuex';
import { listRoles, deleteRole, assignPermissions } from '@/api/role';
import { listPermissions, getPermissionTree } from '@/api/permission';

export default {
  name: 'RoleList',
  data() {
    return {
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        name: '',
        status: ''
      },
      // 角色列表
      roleList: [],
      // 总记录数
      total: 0,
      // 加载状态
      loading: false,
      // 权限分配对话框
      permDialogVisible: false,
      // 权限树数据
      permissionTree: [],
      // 已选择的权限
      selectedPermissions: [],
      // 当前选中的角色ID
      currentRoleId: null
    };
  },
  computed: {
    ...mapGetters(['hasPermission', 'currentTenantId'])
  },
  created() {
    this.getList();
  },
  methods: {
    // 获取角色列表
    async getList() {
      this.loading = true;
      try {
        const response = await listRoles(this.queryParams);
        this.roleList = response.data.list;
        this.total = response.data.total;
      } catch (error) {
        console.error('获取角色列表失败:', error);
        this.$message.error('获取角色列表失败');
      } finally {
        this.loading = false;
      }
    },
    
    // 搜索按钮点击事件
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    
    // 重置搜索表单
    resetQuery() {
      this.$refs.queryForm.resetFields();
      this.handleQuery();
    },
    
    // 新建角色
    handleCreate() {
      this.$router.push('/roles/create');
    },
    
    // 编辑角色
    handleEdit(row) {
      this.$router.push(`/roles/edit/${row.id}`);
    },
    
    // 删除角色
    handleDelete(row) {
      this.$confirm(`确认删除角色"${row.name}"吗?`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          await deleteRole(row.id);
          this.$message.success('删除成功');
          this.getList();
        } catch (error) {
          console.error('删除角色失败:', error);
          this.$message.error('删除角色失败');
        }
      }).catch(() => {
        this.$message.info('已取消删除');
      });
    },
    
    // 分配权限按钮点击事件
    async handleAssignPermission(row) {
      this.currentRoleId = row.id;
      try {
        // 获取权限树
        const treeResponse = await getPermissionTree();
        this.permissionTree = treeResponse.data;
        
        // 获取角色已分配的权限
        this.selectedPermissions = row.permissionIds || [];
        this.permDialogVisible = true;
      } catch (error) {
        console.error('获取权限数据失败:', error);
        this.$message.error('获取权限数据失败');
      }
    },
    
    // 确认权限分配
    async confirmPermAssign() {
      const checkedKeys = this.$refs.permTree.getCheckedKeys();
      const halfCheckedKeys = this.$refs.permTree.getHalfCheckedKeys();
      const permissionIds = [...checkedKeys, ...halfCheckedKeys];
      
      try {
        await assignPermissions(this.currentRoleId, permissionIds);
        this.$message.success('权限分配成功');
        this.permDialogVisible = false;
        this.getList();
      } catch (error) {
        console.error('分配权限失败:', error);
        this.$message.error('分配权限失败');
      }
    },
    
    // 每页条数改变
    handleSizeChange(size) {
      this.queryParams.pageSize = size;
      this.getList();
    },
    
    // 当前页改变
    handleCurrentChange(page) {
      this.queryParams.pageNum = page;
      this.getList();
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

.pagination-container {
  margin-top: 20px;
  text-align: right;
}

.delete-btn {
  color: #F56C6C;
}
</style>
