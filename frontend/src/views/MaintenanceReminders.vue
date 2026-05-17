<template>
  <div>
    <h2 style="margin-bottom: 20px">保养提醒</h2>

    <el-table :data="reminders" border style="width: 100%">
      <el-table-column prop="plateNumber" label="车牌号" width="120" />
      <el-table-column prop="currentMileage" label="当前里程" width="120" />
      <el-table-column prop="dueMileage" label="应保养里程" width="120" />
      <el-table-column prop="handled" label="状态" width="100">
        <template #default="scope">
          <el-tag :type="scope.row.handled ? 'success' : 'warning'">
            {{ scope.row.handled ? '已处理' : '待处理' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createdAt" label="提醒时间" width="180" />
      <el-table-column label="操作" width="120">
        <template #default="scope">
          <el-button v-if="!scope.row.handled" size="small" type="success" @click="handleReminder(scope.row.id)">标记已保养</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '../utils/request'

const reminders = ref([])

const loadReminders = async () => {
  try {
    const res = await request.get('/vehicles/maintenance-reminders')
    reminders.value = res.data
  } catch (error) {
    ElMessage.error('加载保养提醒数据失败')
  }
}

const handleReminder = async (id) => {
  try {
    await request.post(`/vehicles/maintenance-reminders/${id}/handle`)
    ElMessage.success('已标记为保养完成')
    loadReminders()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

onMounted(() => {
  loadReminders()
})
</script>
