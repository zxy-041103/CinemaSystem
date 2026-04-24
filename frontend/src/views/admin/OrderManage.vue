<template>
  <div>
    <el-card>
      <div slot="header" style="display:flex;justify-content:space-between;align-items:center;">
        <span style="font-weight:600;font-size:15px;">订单管理</span>
        <el-tag>共 {{ tableData.length }} 条订单</el-tag>
      </div>
      <el-table :data="tableData" stripe v-loading="loading">
        <el-table-column prop="orderNo" label="订单编号"></el-table-column>
        <el-table-column prop="userName" label="用户"></el-table-column>
        <el-table-column label="影片">
          <template slot-scope="scope">
            <div style="display:flex;align-items:center;gap:8px;">
              <el-image :src="scope.row.moviePoster" style="width:32px;height:44px;border-radius:4px;flex-shrink:0;" fit="cover"></el-image>
              <span>{{ scope.row.movieName }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="hallName" label="放映厅"></el-table-column>
        <el-table-column label="放映时间">
          <template slot-scope="scope">
            <span>{{ formatTime(scope.row.startTime) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="座位">
          <template slot-scope="scope">
            <span v-for="(s, i) in parseSeats(scope.row.seats)" :key="i" class="seat-tag">{{ s.row }}排{{ s.col }}座</span>
          </template>
        </el-table-column>
        <el-table-column prop="seatCount" label="票数" width="60"></el-table-column>
        <el-table-column label="总价" width="80">
          <template slot-scope="scope"><span style="color:#f56c6c;font-weight:600;">¥{{ scope.row.totalPrice }}</span></template>
        </el-table-column>
        <el-table-column label="状态" width="90">
          <template slot-scope="scope">
            <el-tag :type="statusType(scope.row.status)" size="small">{{ statusText(scope.row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建时间">
          <template slot-scope="scope">{{ formatTime(scope.row.createTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template slot-scope="scope">
            <el-button v-if="scope.row.status==='PAID'" type="text" icon="el-icon-check" @click="handleUse(scope.row.id)">核销</el-button>
            <el-button v-if="scope.row.status==='PAID'" type="text" icon="el-icon-refresh-left" style="color:#e6a23c" @click="handleRefund(scope.row.id)">退票</el-button>
            <el-button type="text" icon="el-icon-delete" style="color:#f56c6c" @click="handleDelete(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script>
export default {
  name: 'OrderManage',
  data() { return { tableData: [], loading: false } },
  mounted() { this.loadData() },
  methods: {
    formatTime(t) { return t ? t.replace('T', ' ').substring(0, 16) : '' },
    parseSeats(s) { try { return JSON.parse(s) } catch { return [] } },
    statusType(s) { return { UNPAID: 'warning', PAID: 'success', CANCELLED: 'info', USED: '', REFUNDED: 'danger' }[s] || 'info' },
    statusText(s) { return { UNPAID: '未支付', PAID: '已支付', CANCELLED: '已取消', USED: '已使用', REFUNDED: '已退票' }[s] || s },
    loadData() {
      this.loading = true
      this.$http.get('/api/order/list').then(res => {
        this.loading = false
        if (res.data.code === 200) this.tableData = res.data.data
      })
    },
    handleUse(id) {
      this.$confirm('确认核销该订单吗？', '核销确认', { type: 'info' }).then(() => {
        this.$http.put('/api/order/use/' + id).then(res => {
          if (res.data.code === 200) { this.$message.success('核销成功'); this.loadData() } else { this.$message.error(res.data.msg) }
        })
      }).catch(() => {})
    },
    handleRefund(id) {
      this.$confirm('确认退票吗？退票将扣减用户积分', '退票确认', { type: 'warning' }).then(() => {
        this.$http.put('/api/order/refund/' + id).then(res => {
          if (res.data.code === 200) { this.$message.success('退票成功'); this.loadData() } else { this.$message.error(res.data.msg) }
        })
      }).catch(() => {})
    },
    handleDelete(id) {
      this.$confirm('确认删除该订单吗？', '提示', { type: 'warning' }).then(() => {
        this.$http.delete('/api/order/' + id).then(res => {
          if (res.data.code === 200) { this.$message.success('删除成功'); this.loadData() }
        })
      }).catch(() => {})
    }
  }
}
</script>

<style scoped>
.seat-tag {
  display: inline-block;
  background: #ecf5ff;
  color: #409eff;
  padding: 2px 6px;
  border-radius: 4px;
  font-size: 12px;
  margin: 1px 2px;
}
</style>
