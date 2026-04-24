<template>
  <div class="seat-page" v-if="screening">
    <div class="seat-header">
      <div class="sh-info">
        <h2>{{ screening.movieName }}</h2>
        <p>{{ screening.hallName }} ({{ screening.hallType }}) | {{ formatDateTime(screening.startTime) }} - {{ formatTime(screening.endTime) }}</p>
      </div>
    </div>

    <!-- 座位区域 -->
    <div class="seat-area">
      <div class="screen">银 幕</div>
      <div class="seat-grid">
        <div v-for="row in seatRows" :key="row" class="seat-row">
          <span class="row-label">{{ row }}排</span>
          <div v-for="col in seatCols" :key="col"
            class="seat-item"
            :class="seatClass(row, col)"
            @click="toggleSeat(row, col)">
            {{ col }}
          </div>
          <span class="row-label">{{ row }}排</span>
        </div>
      </div>

      <div class="seat-legend">
        <span class="legend-item"><span class="seat-dot available"></span>可选</span>
        <span class="legend-item"><span class="seat-dot selected"></span>已选</span>
        <span class="legend-item"><span class="seat-dot sold"></span>已售</span>
        <span class="legend-item"><span class="seat-dot disabled"></span>不可选</span>
      </div>
    </div>

    <!-- 订单信息 -->
    <div class="order-panel" v-if="selectedSeats.length > 0">
      <div class="op-info">
        <h3>订单信息</h3>
        <div class="op-movie">{{ screening.movieName }}</div>
        <div class="op-time">{{ formatDateTime(screening.startTime) }} | {{ screening.hallName }}</div>
        <div class="op-seats">
          座位：
          <el-tag v-for="s in selectedSeats" :key="s.row + '-' + s.col" size="small" style="margin:2px 4px;">
            {{ s.row }}排{{ s.col }}座
          </el-tag>
        </div>
        <div class="op-price">
          <span>共 {{ selectedSeats.length }} 张</span>
          <span class="price-total">¥{{ totalPrice }}</span>
        </div>
      </div>
      <el-button type="primary" size="large" round class="pay-btn" @click="submitOrder">
        确认购票
      </el-button>
    </div>
  </div>
</template>

<script>
export default {
  name: 'SeatSelect',
  data() {
    return {
      screening: null,
      seats: [],
      soldSeats: [],
      selectedSeats: [],
      seatRows: 0,
      seatCols: 0
    }
  },
  computed: {
    totalPrice() {
      if (!this.screening) return 0
      const user = this.$store.state.user
      const price = user && user.points >= 100 && this.screening.discountPrice ? this.screening.discountPrice : this.screening.price
      return (price * this.selectedSeats.length).toFixed(2)
    }
  },
  mounted() {
    const screeningId = this.$route.params.screeningId
    this.$http.get('/api/screening/' + screeningId).then(res => {
      if (res.data.code === 200) {
        this.screening = res.data.data
        this.loadSeats(this.screening.hallId)
        this.loadSoldSeats(screeningId)
      }
    })
  },
  methods: {
    formatDateTime(t) { return t ? t.replace('T', ' ').substring(0, 16) : '' },
    formatTime(t) { return t ? t.replace('T', ' ').substring(11, 16) : '' },
    loadSeats(hallId) {
      this.$http.get('/api/seat/hall/' + hallId).then(res => {
        if (res.data.code === 200) {
          this.seats = res.data.data
          this.seatRows = Math.max(...this.seats.map(s => s.rowNum))
          this.seatCols = Math.max(...this.seats.map(s => s.colNum))
        }
      })
    },
    loadSoldSeats(screeningId) {
      this.$http.get('/api/seat/sold/' + screeningId).then(res => {
        if (res.data.code === 200) this.soldSeats = res.data.data
      })
    },
    getSeatStatus(row, col) {
      const seat = this.seats.find(s => s.rowNum === row && s.colNum === col)
      if (!seat || seat.status === 0) return 'disabled'
      if (this.soldSeats.some(s => s.row === row && s.col === col)) return 'sold'
      if (this.selectedSeats.some(s => s.row === row && s.col === col)) return 'selected'
      return 'available'
    },
    seatClass(row, col) {
      return this.getSeatStatus(row, col)
    },
    toggleSeat(row, col) {
      const status = this.getSeatStatus(row, col)
      if (status === 'disabled' || status === 'sold') return
      const idx = this.selectedSeats.findIndex(s => s.row === row && s.col === col)
      if (idx >= 0) {
        this.selectedSeats.splice(idx, 1)
      } else {
        if (this.selectedSeats.length >= 6) {
          this.$message.warning('最多选择6个座位')
          return
        }
        this.selectedSeats.push({ row, col })
      }
    },
    submitOrder() {
      if (this.selectedSeats.length === 0) {
        this.$message.warning('请先选择座位')
        return
      }
      this.$confirm(`确认购买 ${this.selectedSeats.length} 张票，总计 ¥${this.totalPrice}？`, '确认购票', { type: 'info' }).then(() => {
        const order = {
          userId: this.$store.getters.userId,
          screeningId: this.screening.id,
          seats: JSON.stringify(this.selectedSeats),
          seatCount: this.selectedSeats.length,
          totalPrice: parseFloat(this.totalPrice)
        }
        this.$http.post('/api/order', order).then(res => {
          if (res.data.code === 200) {
            const orderId = res.data.data.id
            this.$confirm('订单创建成功，是否立即支付？', '支付确认', { confirmButtonText: '立即支付', cancelButtonText: '稍后支付', type: 'success' }).then(() => {
              this.$http.put('/api/order/pay/' + orderId).then(payRes => {
                if (payRes.data.code === 200) {
                  this.$message.success('支付成功！')
                  // 刷新用户信息
                  this.$http.get('/api/user/' + this.$store.getters.userId).then(uRes => {
                    if (uRes.data.code === 200) this.$store.dispatch('login', uRes.data.data)
                  })
                  this.$router.push('/my-orders')
                }
              })
            }).catch(() => {
              this.$message.info('订单已创建，请在我的订单中完成支付')
              this.$router.push('/my-orders')
            })
          }
        })
      }).catch(() => {})
    }
  }
}
</script>

<style scoped>
.seat-page { max-width: 900px; margin: 0 auto; }
.seat-header { margin-bottom: 20px; padding: 20px; background: rgba(255,255,255,0.04); border-radius: 12px; }
.seat-header h2 { color: #fff; font-size: 22px; margin-bottom: 6px; }
.seat-header p { color: rgba(255,255,255,0.5); font-size: 14px; }

.seat-area {
  background: rgba(255,255,255,0.02);
  border-radius: 16px;
  padding: 30px;
  margin-bottom: 20px;
}

.seat-grid { display: flex; flex-direction: column; align-items: center; gap: 6px; padding: 20px 0; }
.seat-row { display: flex; gap: 6px; align-items: center; }
.row-label { width: 36px; text-align: center; font-size: 12px; color: rgba(255,255,255,0.3); }

.seat-item {
  width: 32px; height: 32px;
  border-radius: 6px 6px 10px 10px;
  display: flex; align-items: center; justify-content: center;
  font-size: 10px; cursor: pointer;
  transition: all 0.2s; border: 2px solid transparent;
}
.seat-item.available { background: #2a2a4a; color: #aaa; border-color: #3a3a5a; }
.seat-item.available:hover { background: #667eea; color: #fff; transform: scale(1.1); }
.seat-item.selected { background: #667eea; color: #fff; border-color: #8898ee; box-shadow: 0 0 10px rgba(102,126,234,0.5); }
.seat-item.sold { background: #555; color: #888; cursor: not-allowed; }
.seat-item.disabled { background: transparent; border: 1px dashed #333; cursor: not-allowed; color: transparent; }

.seat-legend { display: flex; justify-content: center; gap: 24px; margin-top: 16px; }
.legend-item { display: flex; align-items: center; gap: 6px; color: rgba(255,255,255,0.5); font-size: 13px; }
.seat-dot { width: 16px; height: 16px; border-radius: 4px; }
.seat-dot.available { background: #2a2a4a; border: 1px solid #3a3a5a; }
.seat-dot.selected { background: #667eea; }
.seat-dot.sold { background: #555; }
.seat-dot.disabled { border: 1px dashed #333; }

.order-panel {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: rgba(102, 126, 234, 0.08);
  border: 1px solid rgba(102, 126, 234, 0.2);
  border-radius: 16px;
  padding: 24px 30px;
}
.op-info h3 { color: #fff; margin-bottom: 8px; }
.op-movie { color: #fff; font-size: 15px; margin-bottom: 4px; }
.op-time { color: rgba(255,255,255,0.5); font-size: 13px; margin-bottom: 8px; }
.op-seats { color: rgba(255,255,255,0.6); font-size: 13px; margin-bottom: 8px; }
.op-price { display: flex; align-items: center; gap: 12px; margin-top: 8px; color: rgba(255,255,255,0.6); }
.price-total { font-size: 28px; font-weight: 700; color: #f56c6c; }
.pay-btn { padding: 14px 40px !important; font-size: 16px !important; }
</style>
