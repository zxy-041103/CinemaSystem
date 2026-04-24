<template>
  <div>
    <el-card>
      <div slot="header" style="display:flex;justify-content:space-between;align-items:center;">
        <div>
          <el-input v-model="search" placeholder="搜索影片名称" style="width:250px;" clearable @clear="loadData" @keyup.enter.native="loadData">
            <el-button slot="append" icon="el-icon-search" @click="loadData"></el-button>
          </el-input>
        </div>
        <el-button type="primary" icon="el-icon-plus" @click="handleAdd">新增影片</el-button>
      </div>
      <el-table :data="tableData" stripe v-loading="loading">
        <el-table-column label="海报" width="100">
          <template slot-scope="scope">
            <el-image :src="scope.row.poster" style="width:60px;height:80px;border-radius:6px;" fit="cover" :preview-src-list="[scope.row.poster]">
              <div slot="error" style="width:60px;height:80px;background:#f5f5f5;display:flex;align-items:center;justify-content:center;border-radius:6px;">
                <i class="el-icon-picture" style="font-size:20px;color:#ccc;"></i>
              </div>
            </el-image>
          </template>
        </el-table-column>
        <el-table-column prop="name" label="影片名称"></el-table-column>
        <el-table-column prop="director" label="导演"></el-table-column>
        <el-table-column prop="actors" label="主演" show-overflow-tooltip></el-table-column>
        <el-table-column prop="genre" label="类型"></el-table-column>
        <el-table-column prop="duration" label="时长">
          <template slot-scope="scope">{{ scope.row.duration }}分钟</template>
        </el-table-column>
        <el-table-column prop="rating" label="评分">
          <template slot-scope="scope">
            <el-rate :value="scope.row.rating / 2" disabled show-score :score-template="scope.row.rating + ''" text-color="#ff9900" style="display:inline-flex;"></el-rate>
          </template>
        </el-table-column>
        <el-table-column prop="releaseDate" label="上映日期"></el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template slot-scope="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : scope.row.status === 2 ? 'warning' : 'info'" size="small">
              {{ scope.row.status === 1 ? '上映中' : scope.row.status === 2 ? '即将上映' : '已下架' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="160">
          <template slot-scope="scope">
            <el-button type="text" icon="el-icon-edit" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button type="text" icon="el-icon-delete" style="color:#f56c6c" @click="handleDelete(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination style="margin-top:16px;text-align:right;" @current-change="handlePageChange" :current-page="pageNum" :page-size="pageSize" :total="total" layout="total, prev, pager, next, jumper"></el-pagination>
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="700px" top="5vh">
      <el-form :model="form" :rules="rules" ref="form" label-width="90px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="影片名称" prop="name">
              <el-input v-model="form.name" placeholder="请输入影片名称"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="导演" prop="director">
              <el-input v-model="form.director" placeholder="请输入导演"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="主演" prop="actors">
          <el-input v-model="form.actors" placeholder="请输入主演，用逗号分隔"></el-input>
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="类型" prop="genre">
              <el-input v-model="form.genre" placeholder="如：科幻/冒险/灾难"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="时长(分钟)" prop="duration">
              <el-input-number v-model="form.duration" :min="1" :max="600" style="width:100%;"></el-input-number>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="上映日期" prop="releaseDate">
              <el-date-picker v-model="form.releaseDate" type="date" value-format="yyyy-MM-dd" placeholder="选择日期" style="width:100%;"></el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="下映日期">
              <el-date-picker v-model="form.endDate" type="date" value-format="yyyy-MM-dd" placeholder="选择日期" style="width:100%;"></el-date-picker>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="评分">
              <el-input-number v-model="form.rating" :min="0" :max="10" :step="0.1" :precision="1" style="width:100%;"></el-input-number>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="国家/地区">
              <el-input v-model="form.country" placeholder="如：中国"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="状态">
              <el-select v-model="form.status" style="width:100%;">
                <el-option :value="1" label="上映中"></el-option>
                <el-option :value="2" label="即将上映"></el-option>
                <el-option :value="0" label="已下架"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="海报">
          <el-upload
            class="poster-upload"
            action="/api/file/upload"
            :show-file-list="false"
            :on-success="handleUploadSuccess"
            accept="image/*">
            <div v-if="form.poster" class="poster-preview">
              <el-image :src="form.poster" style="width:120px;height:160px;border-radius:8px;" fit="cover"></el-image>
              <div class="poster-mask"><i class="el-icon-camera"></i> 更换</div>
            </div>
            <div v-else class="poster-placeholder">
              <i class="el-icon-plus"></i>
              <span>上传海报</span>
            </div>
          </el-upload>
        </el-form-item>
        <el-form-item label="简介">
          <el-input v-model="form.description" type="textarea" :rows="4" placeholder="请输入影片简介"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="submitForm">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: 'MovieManage',
  data() {
    return {
      tableData: [], loading: false, search: '',
      pageNum: 1, pageSize: 10, total: 0,
      dialogVisible: false, dialogTitle: '新增影片',
      form: { name: '', director: '', actors: '', genre: '', duration: 120, releaseDate: '', endDate: '', description: '', rating: 0, country: '中国', status: 1, poster: '' },
      rules: {
        name: [{ required: true, message: '请输入影片名称', trigger: 'blur' }],
        genre: [{ required: true, message: '请输入类型', trigger: 'blur' }],
        duration: [{ required: true, message: '请输入时长', trigger: 'blur' }]
      }
    }
  },
  mounted() { this.loadData() },
  methods: {
    loadData() {
      this.loading = true
      this.$http.get('/api/movie/page', { params: { pageNum: this.pageNum, pageSize: this.pageSize, name: this.search } }).then(res => {
        this.loading = false
        if (res.data.code === 200) {
          this.tableData = res.data.data.records
          this.total = res.data.data.total
        }
      })
    },
    handlePageChange(val) { this.pageNum = val; this.loadData() },
    handleAdd() {
      this.dialogTitle = '新增影片'
      this.form = { name: '', director: '', actors: '', genre: '', duration: 120, releaseDate: '', endDate: '', description: '', rating: 0, country: '中国', status: 1, poster: '' }
      this.dialogVisible = true
    },
    handleEdit(row) {
      this.dialogTitle = '编辑影片'
      this.form = { ...row }
      this.dialogVisible = true
    },
    handleDelete(id) {
      this.$confirm('确认删除该影片吗？', '提示', { type: 'warning' }).then(() => {
        this.$http.delete('/api/movie/' + id).then(res => {
          if (res.data.code === 200) { this.$message.success('删除成功'); this.loadData() }
        })
      }).catch(() => {})
    },
    handleUploadSuccess(res) {
      if (res.code === 200) {
        this.form.poster = res.data
        this.$message.success('上传成功')
      }
    },
    submitForm() {
      this.$refs.form.validate(valid => {
        if (!valid) return
        const method = this.form.id ? 'put' : 'post'
        this.$http[method]('/api/movie', this.form).then(res => {
          if (res.data.code === 200) {
            this.$message.success(this.form.id ? '更新成功' : '新增成功')
            this.dialogVisible = false
            this.loadData()
          }
        })
      })
    }
  }
}
</script>

<style scoped>
.poster-upload { display: inline-block; }
.poster-preview { position: relative; display: inline-block; cursor: pointer; }
.poster-mask {
  position: absolute; inset: 0; background: rgba(0,0,0,0.5); display: flex; align-items: center; justify-content: center;
  color: #fff; font-size: 14px; border-radius: 8px; opacity: 0; transition: opacity 0.3s;
}
.poster-preview:hover .poster-mask { opacity: 1; }
.poster-placeholder {
  width: 120px; height: 160px; border: 2px dashed #dcdfe6; border-radius: 8px; display: flex; flex-direction: column;
  align-items: center; justify-content: center; color: #909399; cursor: pointer; transition: all 0.3s;
}
.poster-placeholder:hover { border-color: #667eea; color: #667eea; }
.poster-placeholder i { font-size: 28px; margin-bottom: 6px; }
</style>
