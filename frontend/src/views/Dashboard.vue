<template>
  <div>
    <h2 style="margin-bottom: 20px">系统统计概览</h2>
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-value">{{ statistics.totalVehicles || 0 }}</div>
            <div class="stat-label">车辆总数</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-value" style="color: #67c23a">{{ statistics.availableVehicles || 0 }}</div>
            <div class="stat-label">可用车辆</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-value" style="color: #409eff">{{ statistics.availabilityRate || 0 }}%</div>
            <div class="stat-label">完好率</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-value" style="color: #e6a23c">{{ statistics.totalRepairs || 0 }}</div>
            <div class="stat-label">维修次数</div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="6">
        <el-card class="stat-card warning">
          <div class="stat-content">
            <div class="stat-value" style="color: #f56c6c">{{ statistics.overdueMaintenance || 0 }}</div>
            <div class="stat-label">逾期保养</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-value" style="color: #f56c6c">{{ statistics.highUrgencyFaults || 0 }}</div>
            <div class="stat-label">高紧急故障</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-value" style="color: #e6a23c">{{ statistics.mediumUrgencyFaults || 0 }}</div>
            <div class="stat-label">中紧急故障</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-value" style="color: #909399">{{ statistics.lowUrgencyFaults || 0 }}</div>
            <div class="stat-label">低紧急故障</div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '../utils/request'

const statistics = ref({})

const loadStatistics = async () => {
  try {
    const res = await request.get('/statistics')
    statistics.value = res.data
  } catch (error) {
    console.error('加载统计数据失败', error)
  }
}

onMounted(() => {
  loadStatistics()
})
</script>

<style scoped>
.stat-card {
  text-align: center;
}
.stat-content {
  padding: 20px;
}
.stat-value {
  font-size: 48px;
  font-weight: bold;
  color: #409eff;
}
.stat-label {
  font-size: 16px;
  color: #666;
  margin-top: 10px;
}
.warning {
  background-color: #fef0f0;
}
</style>
