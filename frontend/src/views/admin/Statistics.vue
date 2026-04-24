<template>
  <div class="statistics-page">
    <!-- 概览卡片 -->
    <el-row :gutter="20" class="stat-row">
      <el-col :span="6">
        <div class="stat-card blue">
          <div class="stat-icon"><i class="el-icon-user"></i></div>
          <div class="stat-info">
            <div class="stat-value">{{ overview.userCount || 0 }}</div>
            <div class="stat-label">注册会员</div>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card green">
          <div class="stat-icon"><i class="el-icon-film"></i></div>
          <div class="stat-info">
            <div class="stat-value">{{ overview.showingCount || 0 }}</div>
            <div class="stat-label">正在上映</div>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card orange">
          <div class="stat-icon"><i class="el-icon-tickets"></i></div>
          <div class="stat-info">
            <div class="stat-value">{{ overview.paidCount || 0 }}</div>
            <div class="stat-label">成交订单</div>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card purple">
          <div class="stat-icon"><i class="el-icon-money"></i></div>
          <div class="stat-info">
            <div class="stat-value">¥{{ (overview.totalRevenue || 0).toFixed(0) }}</div>
            <div class="stat-label">总营收</div>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- 图表 -->
    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="14">
        <el-card>
          <div slot="header" class="card-header">
            <span><i class="el-icon-data-line"></i> 近7天票房趋势</span>
          </div>
          <div ref="revenueChart" style="height: 340px;"></div>
        </el-card>
      </el-col>
      <el-col :span="10">
        <el-card>
          <div slot="header" class="card-header">
            <span><i class="el-icon-pie-chart"></i> 影片类型分布</span>
          </div>
          <div ref="genreChart" style="height: 340px;"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="14">
        <el-card>
          <div slot="header" class="card-header">
            <span><i class="el-icon-trophy"></i> 影片票房排行TOP10</span>
          </div>
          <div ref="movieChart" style="height: 340px;"></div>
        </el-card>
      </el-col>
      <el-col :span="10">
        <el-card>
          <div slot="header" class="card-header">
            <span><i class="el-icon-circle-check"></i> 订单状态分布</span>
          </div>
          <div ref="orderChart" style="height: 340px;"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import * as echarts from 'echarts'

export default {
  name: 'Statistics',
  data() {
    return { overview: {} }
  },
  mounted() {
    this.loadData()
  },
  methods: {
    loadData() {
      this.$http.get('/api/stat/overview').then(res => {
        if (res.data.code === 200) this.overview = res.data.data
      })
      this.loadRevenueChart()
      this.loadGenreChart()
      this.loadMovieChart()
      this.loadOrderChart()
    },
    loadRevenueChart() {
      this.$http.get('/api/stat/revenue/daily').then(res => {
        if (res.data.code === 200) {
          const data = res.data.data
          const chart = echarts.init(this.$refs.revenueChart)
          chart.setOption({
            tooltip: { trigger: 'axis' },
            grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
            xAxis: { type: 'category', data: data.map(d => d.date), axisLabel: { color: '#666' } },
            yAxis: { type: 'value', axisLabel: { color: '#666', formatter: '¥{value}' } },
            series: [{
              name: '票房收入',
              type: 'line',
              smooth: true,
              data: data.map(d => d.revenue),
              areaStyle: {
                color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                  { offset: 0, color: 'rgba(102, 126, 234, 0.4)' },
                  { offset: 1, color: 'rgba(102, 126, 234, 0.05)' }
                ])
              },
              lineStyle: { color: '#667eea', width: 3 },
              itemStyle: { color: '#667eea' }
            }]
          })
          window.addEventListener('resize', () => chart.resize())
        }
      })
    },
    loadGenreChart() {
      this.$http.get('/api/stat/genre').then(res => {
        if (res.data.code === 200) {
          const chart = echarts.init(this.$refs.genreChart)
          chart.setOption({
            tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
            legend: { bottom: 10, textStyle: { color: '#666' } },
            color: ['#667eea', '#764ba2', '#f093fb', '#f5576c', '#4facfe', '#00f2fe', '#11998e', '#38ef7d', '#fc5c7d', '#6a82fb'],
            series: [{
              type: 'pie',
              radius: ['40%', '65%'],
              label: { show: true, color: '#333' },
              data: res.data.data,
              emphasis: {
                itemStyle: { shadowBlur: 10, shadowOffsetX: 0, shadowColor: 'rgba(0, 0, 0, 0.2)' }
              }
            }]
          })
          window.addEventListener('resize', () => chart.resize())
        }
      })
    },
    loadMovieChart() {
      this.$http.get('/api/stat/revenue/movie').then(res => {
        if (res.data.code === 200) {
          const data = res.data.data
          const chart = echarts.init(this.$refs.movieChart)
          chart.setOption({
            tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
            grid: { left: '3%', right: '8%', bottom: '3%', containLabel: true },
            xAxis: { type: 'value', axisLabel: { color: '#666', formatter: '¥{value}' } },
            yAxis: { type: 'category', data: data.map(d => d.movie_name).reverse(), axisLabel: { color: '#666' } },
            series: [{
              type: 'bar',
              data: data.map(d => d.revenue).reverse(),
              barWidth: 20,
              itemStyle: {
                borderRadius: [0, 8, 8, 0],
                color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
                  { offset: 0, color: '#667eea' },
                  { offset: 1, color: '#764ba2' }
                ])
              }
            }]
          })
          window.addEventListener('resize', () => chart.resize())
        }
      })
    },
    loadOrderChart() {
      this.$http.get('/api/stat/order/status').then(res => {
        if (res.data.code === 200) {
          const chart = echarts.init(this.$refs.orderChart)
          chart.setOption({
            tooltip: { trigger: 'item' },
            legend: { bottom: 10, textStyle: { color: '#666' } },
            color: ['#e6a23c', '#67c23a', '#909399', '#409eff', '#f56c6c'],
            series: [{
              type: 'pie',
              radius: '60%',
              label: { show: true, formatter: '{b}\n{d}%', color: '#333' },
              data: res.data.data,
              emphasis: {
                itemStyle: { shadowBlur: 10, shadowColor: 'rgba(0,0,0,0.2)' }
              }
            }]
          })
          window.addEventListener('resize', () => chart.resize())
        }
      })
    }
  }
}
</script>

<style scoped>
.stat-row { animation: fadeInUp 0.5s ease; }
@keyframes fadeInUp {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}
.stat-card {
  border-radius: 16px;
  padding: 24px;
  color: #fff;
  display: flex;
  align-items: center;
  gap: 16px;
  position: relative;
  overflow: hidden;
}
.stat-card::after {
  content: '';
  position: absolute;
  top: -30%;
  right: -20%;
  width: 100px;
  height: 100px;
  border-radius: 50%;
  background: rgba(255,255,255,0.1);
}
.stat-icon { font-size: 36px; opacity: 0.9; }
.stat-value { font-size: 28px; font-weight: 700; }
.stat-label { font-size: 13px; opacity: 0.85; margin-top: 4px; }
.card-header { font-size: 15px; font-weight: 600; color: #1a1a2e; }
.card-header i { margin-right: 6px; color: #667eea; }
</style>
