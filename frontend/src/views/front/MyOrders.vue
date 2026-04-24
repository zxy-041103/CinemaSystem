<template>
  <div class="orders-page">
    <h2 class="page-title"><i class="el-icon-tickets" style="color:#667eea;"></i> 我的订单</h2>

    <div v-if="orders.length === 0" class="empty-state">
      <i class="el-icon-document" style="font-size:60px;color:#555;"></i>
      <p>暂无订单记录</p>
      <el-button type="primary" round @click="$router.push('/home')">去选电影</el-button>
    </div>

    <div class="order-list">
      <div v-for="order in orders" :key="order.id" class="order-card">
        <div class="oc-left">
          <el-image :src="order.moviePoster" style="width:80px;height:112px;border-radius:8px;flex-shrink:0;" fit="cover"></el-image>
          <div class="oc-info">
            <div class="oc-movie">{{ order.movieName }}</div>
            <div class="oc-detail">
              <i class="el-icon-location-outline"></i> {{ order.hallName }}
            </div>
            <div class="oc-detail">
              <i class="el-icon-time"></i> {{ formatTime(order.startTime) }}
            </div>
            <div class="oc-detail">
              <i class="el-icon-position"></i>
              <span v-for="(s, i) in parseSeats(order.seats)" :key="i">{{ s.row }}排{{ s.col }}座{{ i < parseSeats(order.seats).length - 1 ? '、' : '' }}</span>
            </div>
            <div class="oc-detail">
              <i class="el-icon-document"></i> {{ order.orderNo }}
            </div>
          </div>
        </div>
        <div class="oc-right">
          <div class="oc-price">¥{{ order.totalPrice }}</div>
          <el-tag :type="statusType(order.status)" size="medium">{{ statusText(order.status) }}</el-tag>
          <div class="oc-actions">
            <el-button v-if="order.status === 'UNPAID'" type="primary" size="small" round @click="payOrder(order.id)">立即支付</el-button>
            <el-button v-if="order.status === 'UNPAID'" size="small" round @click="cancelOrder(order.id)">取消订单</el-button>
            <el-button v-if="order.status === 'PAID'" type="warning" size="small" round @click="refundOrder(order.id)">申请退票</el-button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'MyOrders',
  data() { return { orders: [] } },
  mounted() { this.loadData() },
  methods: {
    formatTime(t) { return t ? t.replace('T', ' ').substring(0, 16) : '' },
    parseSeats(s) { try { return JSON.parse(s) } catch { return [] } },
    statusType(s) { return { UNPAID: 'warning', PAID: 'success', CANCELLED: 'info', USED: '', REFUNDED: 'danger' }[s] || 'info' },
    statusText(s) { return { UNPAID: '待支付', PAID: '已支付', CANCELLED: '已取消', USED: '已使用', REFUNDED: '已退票' }[s] || s },
    loadData() {
      const userId = this.$store.getters.userId
      this.$http.get('/api/order/user/' + userId).then(res => {
        if (res.data.code === 200) this.orders = res.data.data
      })
    },
    payOrder(id) {
      this.$confirm('确认支付该订单吗？', '支付确认', { type: 'info' }).then(() => {
        this.$http.put('/api/order/pay/' + id).then(res => {
          if (res.data.code === 200) {
            this.$message.success('支付成功！')
            this.loadData()
            this.$http.get('/api/user/' + this.$store.getters.userId).then(uRes => {
              if (uRes.data.code === 200) this.$store.dispatch('login', uRes.data.data)
            })
          } else { this.$message.error(res.data.msg) }
        })
      }).catch(() => {})
    },
    cancelOrder(id) {
      this.$confirm('确认取消该订单吗？', '取消确认', { type: 'warning' }).then(() => {
        this.$http.put('/api/order/cancel/' + id).then(res => {
          if (res.data.code === 200) { this.$message.success('订单已取消'); this.loadData() }
        })
      }).catch(() => {})
    },
    refundOrder(id) {
      this.$confirm('确认申请退票吗？退票将扣减相应积分', '退票确认', { type: 'warning' }).then(() => {
        this.$http.put('/api/order/refund/' + id).then(res => {
          if (res.data.code === 200) {
            this.$message.success('退票成功')
            this.loadData()
            this.$http.get('/api/user/' + this.$store.getters.userId).then(uRes => {
              if (uRes.data.code === 200) this.$store.dispatch('login', uRes.data.data)
            })
          } else { this.$message.error(res.data.msg) }
        })
      }).catch(() => {})
    }
  }
}
</script>

<style scoped>
.page-title { color: #fff; font-size: 22px; margin-bottom: 24px; display: flex; align-items: center; gap: 10px; }
.empty-state { text-align: center; padding: 80px 20px; color: #888; }
.empty-state p { margin: 16px 0; }
.order-list { display: flex; flex-direction: column; gap: 16px; }
.order-card {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: rgba(255,255,255,0.04);
  border: 1px solid rgba(255,255,255,0.06);
  border-radius: 14px;
  padding: 20px 24px;
  transition: all 0.3s;
}
.order-card:hover {
  background: rgba(255,255,255,0.06);
  border-color: rgba(102, 126, 234, 0.2);
}
.oc-left { display: flex; gap: 16px; align-items: flex-start; }
.oc-movie { color: #fff; font-size: 17px; font-weight: 600; margin-bottom: 8px; }
.oc-detail { color: rgba(255,255,255,0.5); font-size: 13px; margin-bottom: 4px; }
.oc-detail i { margin-right: 4px; color: #667eea; }
.oc-right { text-align: right; display: flex; flex-direction: column; align-items: flex-end; gap: 8px; }
.oc-price { font-size: 24px; font-weight: 700; color: #f56c6c; }
.oc-actions { display: flex; gap: 8px; margin-top: 4px; }
</style>
