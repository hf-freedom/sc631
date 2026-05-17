<template>
  <div>
    <div style="display: flex; justify-content: space-between; margin-bottom: 20px">
      <h2>故障管理</h2>
      <div>
        <el-button type="warning" @click="triggerScan">扫描超时故障</el-button>
        <el-button type="primary" @click="showAddDialog = true" style="margin-left: 10px">提交故障</el-button>
      </div>
    </div>

    <el-alert
      v-if="highUrgencyCount > 0"
      :title="`有 ${highUrgencyCount} 个高紧急故障待处理，请及时安排维修！`"
      type="error"
      :closable="false"
      style="margin-bottom: 20px"
    />

    <el-table :data="faults" border style="width: 100%" :row-class-name="tableRowClassName">
      <el-table-column prop="plateNumber" label="车牌号" width="120" />
      <el-table-column prop="driverName" label="司机" width="100" />
      <el-table-column prop="description" label="故障描述" min-width="200" show-overflow-tooltip />
      <el-table-column prop="type" label="故障类型" width="150">
        <template #default="scope">{{ getFaultTypeText(scope.row.type) }}</template>
      </el-table-column>
      <el-table-column prop="urgencyLevel" label="紧急等级" width="120">
        <template #default="scope">
          <el-tag :type="getUrgencyType(scope.row.urgencyLevel)" :effect="isEscalated(scope.row) ? 'dark' : 'light'">
            {{ getUrgencyText(scope.row.urgencyLevel) }}
            <span v-if="isEscalated(scope.row)" style="margin-left: 5px">⏫</span>
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template #default="scope">
          <el-tag :type="getStatusType(scope.row.status)">
            {{ getStatusText(scope.row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createdAt" label="提交时间" width="180">
        <template #default="scope">{{ formatTime(scope.row.createdAt) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="280" fixed="right">
        <template #default="scope">
          <el-button v-if="scope.row.status === 'PENDING'" size="small" type="primary" @click="assignRepair(scope.row)">派单</el-button>
          <el-button size="small" @click="viewDetail(scope.row)">详情</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="showAddDialog" title="提交故障" width="500px">
      <el-form :model="faultForm" label-width="100px">
        <el-form-item label="车辆">
          <el-select v-model="faultForm.vehicleId" placeholder="请选择车辆" style="width: 100%">
            <el-option v-for="v in availableVehicles" :key="v.id" :label="v.plateNumber" :value="v.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="司机姓名">
          <el-input v-model="faultForm.driverName" />
        </el-form-item>
        <el-form-item label="故障类型">
          <el-select v-model="faultForm.type" style="width: 100%">
            <el-option label="发动机故障" value="ENGINE" />
            <el-option label="制动系统故障" value="BRAKE" />
            <el-option label="电气系统故障" value="ELECTRICAL" />
            <el-option label="轮胎故障" value="TIRE" />
            <el-option label="冷却系统故障" value="COOLING" />
            <el-option label="悬挂系统故障" value="SUSPENSION" />
            <el-option label="车身故障" value="BODY" />
            <el-option label="其他故障" value="OTHER" />
          </el-select>
        </el-form-item>
        <el-form-item label="故障描述">
          <el-input type="textarea" v-model="faultForm.description" :rows="4" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAddDialog = false">取消</el-button>
        <el-button type="primary" @click="submitFault">提交</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="showAssignDialog" title="维修派单" width="500px">
      <el-form :model="assignForm" label-width="100px">
        <el-form-item label="维修人员">
          <el-select v-model="assignForm.repairPersonId" style="width: 100%">
            <el-option v-for="p in repairPersons" :key="p.id" :label="p.name" :value="p.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="维修工位">
          <el-select v-model="assignForm.stationId" style="width: 100%">
            <el-option v-for="s in repairStations" :key="s.id" :label="s.name" :value="s.id" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAssignDialog = false">取消</el-button>
        <el-button type="primary" @click="confirmAssign">确定派单</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive, computed } from 'vue'
import { ElMessage } from 'element-plus'
import request from '../utils/request'

const faults = ref([])
const availableVehicles = ref([])
const repairPersons = ref([])
const repairStations = ref([])
const showAddDialog = ref(false)
const showAssignDialog = ref(false)
const currentFaultId = ref(null)

const highUrgencyCount = computed(() => {
  return faults.value.filter(f => f.status === 'PENDING' && f.urgencyLevel === 'HIGH').length
})

const faultForm = reactive({
  vehicleId: '',
  driverName: '',
  type: '',
  description: ''
})

const assignForm = reactive({
  repairPersonId: '',
  stationId: ''
})

const loadFaults = async () => {
  try {
    const res = await request.get('/faults')
    faults.value = res.data.sort((a, b) => {
      const levelOrder = { 'HIGH': 0, 'MEDIUM': 1, 'LOW': 2 }
      return levelOrder[a.urgencyLevel] - levelOrder[b.urgencyLevel]
    })
  } catch (error) {
    ElMessage.error('加载故障数据失败')
  }
}

const triggerScan = async () => {
  try {
    const res = await request.post('/faults/scan')
    if (res.data.updatedCount > 0) {
      ElMessage.warning(`检测到 ${res.data.updatedCount} 个超时故障，已自动升级紧急等级！`)
    } else {
      ElMessage.success('未检测到超时故障')
    }
    loadFaults()
  } catch (error) {
    ElMessage.error('扫描失败')
  }
}

const isEscalated = (row) => {
  const typeDefaultMap = {
    'ENGINE': 'HIGH',
    'BRAKE': 'HIGH',
    'ELECTRICAL': 'MEDIUM',
    'TIRE': 'MEDIUM',
    'COOLING': 'MEDIUM',
    'SUSPENSION': 'MEDIUM',
    'BODY': 'LOW',
    'OTHER': 'LOW'
  }
  return typeDefaultMap[row.type] !== row.urgencyLevel
}

const tableRowClassName = ({ row }) => {
  if (row.status === 'PENDING' && row.urgencyLevel === 'HIGH') {
    return 'high-urgency-row'
  }
  return ''
}

const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  return date.toLocaleString('zh-CN')
}

const loadVehicles = async () => {
  try {
    const res = await request.get('/vehicles')
    availableVehicles.value = res.data.filter(v => v.status === 'AVAILABLE')
  } catch (error) {
    console.error(error)
  }
}

const loadRepairPersons = async () => {
  try {
    const res = await request.get('/faults/repair-persons')
    repairPersons.value = res.data
  } catch (error) {
    console.error(error)
  }
}

const loadRepairStations = async () => {
  try {
    const res = await request.get('/faults/repair-stations')
    repairStations.value = res.data
  } catch (error) {
    console.error(error)
  }
}

const getFaultTypeText = (type) => {
  const map = {
    'ENGINE': '发动机故障',
    'BRAKE': '制动系统故障',
    'ELECTRICAL': '电气系统故障',
    'TIRE': '轮胎故障',
    'COOLING': '冷却系统故障',
    'SUSPENSION': '悬挂系统故障',
    'BODY': '车身故障',
    'OTHER': '其他故障'
  }
  return map[type] || type
}

const getUrgencyType = (level) => {
  const map = { 'HIGH': 'danger', 'MEDIUM': 'warning', 'LOW': 'info' }
  return map[level] || 'info'
}

const getUrgencyText = (level) => {
  const map = { 'HIGH': '高', 'MEDIUM': '中', 'LOW': '低' }
  return map[level] || level
}

const getStatusType = (status) => {
  const map = {
    'PENDING': 'warning',
    'ASSIGNED': 'primary',
    'IN_PROGRESS': 'danger',
    'PAUSED': 'info',
    'COMPLETED': 'success'
  }
  return map[status] || 'info'
}

const getStatusText = (status) => {
  const map = {
    'PENDING': '待处理',
    'ASSIGNED': '已派单',
    'IN_PROGRESS': '处理中',
    'PAUSED': '已暂停',
    'COMPLETED': '已完成'
  }
  return map[status] || status
}

const submitFault = async () => {
  try {
    await request.post('/faults', faultForm)
    ElMessage.success('故障提交成功')
    showAddDialog.value = false
    Object.assign(faultForm, { vehicleId: '', driverName: '', type: '', description: '' })
    loadFaults()
  } catch (error) {
    ElMessage.error('提交失败')
  }
}

const assignRepair = (row) => {
  currentFaultId.value = row.id
  showAssignDialog.value = true
}

const confirmAssign = async () => {
  try {
    await request.post(`/faults/${currentFaultId.value}/assign`, assignForm)
    ElMessage.success('派单成功')
    showAssignDialog.value = false
    Object.assign(assignForm, { repairPersonId: '', stationId: '' })
    loadFaults()
  } catch (error) {
    ElMessage.error('派单失败')
  }
}

const viewDetail = (row) => {
  ElMessage.info('查看详情功能开发中')
}

onMounted(() => {
  loadFaults()
  loadVehicles()
  loadRepairPersons()
  loadRepairStations()
})
</script>

<style scoped>
:deep(.high-urgency-row) {
  background-color: #fef0f0 !important;
}
:deep(.high-urgency-row:hover) {
  background-color: #fde2e2 !important;
}
</style>
