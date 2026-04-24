<template>
  <el-container style="height: 100vh;">
    <el-aside width="220px" class="admin-sidebar">
      <div class="sidebar-logo">
        <i class="el-icon-film" style="font-size: 28px; color: #667eea;"></i>
        <span class="logo-text">电影院订票管理系统</span>
      </div>
      <el-menu :default-active="$route.path" router unique-opened text-color="#fff" active-text-color="#667eea">
        <el-menu-item index="/admin/statistics">
          <i class="el-icon-data-analysis"></i>
          <span>数据统计</span>
        </el-menu-item>
        <el-menu-item index="/admin/movie">
          <i class="el-icon-film"></i>
          <span>影片管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/hall">
          <i class="el-icon-office-building"></i>
          <span>放映厅管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/screening">
          <i class="el-icon-date"></i>
          <span>场次排片管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/order">
          <i class="el-icon-tickets"></i>
          <span>订单管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/user">
          <i class="el-icon-user"></i>
          <span>会员管理</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="admin-header">
        <div class="header-left">
          <span class="page-title">{{ pageTitle }}</span>
        </div>
        <div class="header-right">
          <el-dropdown @command="handleCommand">
            <span class="user-info">
              <el-avatar :size="32" :src="user.avatar" icon="el-icon-user-solid"></el-avatar>
              <span class="user-name">{{ user.nickname || user.username }}</span>
              <i class="el-icon-arrow-down"></i>
            </span>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item command="logout" icon="el-icon-switch-button">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </div>
      </el-header>
      <el-main class="admin-main">
        <transition name="slide-fade">
          <router-view :key="$route.fullPath" />
        </transition>
      </el-main>
    </el-container>
  </el-container>
</template>

<script>
export default {
  name: 'AdminLayout',
  computed: {
    user() { return this.$store.state.user || {} },
    pageTitle() {
      const map = {
        '/admin/statistics': '数据统计',
        '/admin/movie': '影片管理',
        '/admin/hall': '放映厅管理',
        '/admin/screening': '场次排片管理',
        '/admin/order': '订单管理',
        '/admin/user': '会员管理'
      }
      return map[this.$route.path] || '管理后台'
    }
  },
  methods: {
    handleCommand(cmd) {
      if (cmd === 'logout') {
        this.$store.dispatch('logout')
        this.$router.push('/login')
        this.$message.success('已退出登录')
      }
    }
  }
}
</script>

<style scoped>
.sidebar-logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  border-bottom: 1px solid rgba(255,255,255,0.05);
}
.logo-text {
  color: #fff;
  font-size: 18px;
  font-weight: 700;
  letter-spacing: 2px;
}
.admin-header {
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: space-between;
  box-shadow: 0 1px 4px rgba(0,0,0,0.06);
  padding: 0 24px;
  height: 60px !important;
}
.page-title {
  font-size: 18px;
  font-weight: 600;
  color: #1a1a2e;
}
.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  color: #606266;
}
.user-name { font-size: 14px; }
.admin-main {
  background: #f0f2f5;
  padding: 20px;
}
</style>
