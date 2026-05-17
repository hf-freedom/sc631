<template>
  <el-container style="height: 100vh">
    <el-aside width="200px" style="background-color: #2c3e50">
      <div style="color: white; padding: 20px; text-align: center; font-size: 18px; font-weight: bold">
        车辆维修保养管理系统
      </div>
      <el-menu
        :default-active="activeMenu"
        router
        background-color="#2c3e50"
        text-color="#fff"
        active-text-color="#ffd04b"
      >
        <el-menu-item index="/">
          <span>首页统计</span>
        </el-menu-item>
        <el-menu-item index="/vehicles">
          <span>车辆管理</span>
        </el-menu-item>
        <el-menu-item index="/faults">
          <span>故障管理</span>
        </el-menu-item>
        <el-menu-item index="/repair-orders">
          <span>维修工单</span>
        </el-menu-item>
        <el-menu-item index="/spare-parts">
          <span>备件管理</span>
        </el-menu-item>
        <el-menu-item index="/maintenance-reminders">
          <span>保养提醒</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-main style="background-color: #f5f5f5; overflow-y: auto">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const activeMenu = computed(() => route.path)
</script>

<style>
* {
  margin: 0;
  padding: 0;
}
</style>
