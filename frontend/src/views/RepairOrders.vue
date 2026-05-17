<template>
  <div>
    <h2 style="margin-bottom: 20px">维修工单管理</h2>

    <el-table :data="repairOrders" border style="width: 100%">
      <el-table-column prop="plateNumber" label="车牌号" width="120" />
      <el-table-column prop="repairPersonName" label="维修人员" width="120" />
      <el-table-column prop="stationName" label="工位" width="100" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="scope">
          <el-tag :type="getStatusType(scope.row.status)">
            {{ getStatusText(scope.row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createdAt" label="创建时间" width="180" />
      <el-table-column label="操作" width="350" fixed="right">
        <template #default="scope">
          <el-button v-if="scope.row.status === 'PENDING'" size="small" type="primary" @click="startRepair(scope.row.id)">开始维修</el-button>
          <el-button v-if="scope.row.status === 'IN_PROGRESS'" size="small" type="warning" @click="usePart(scope.row)">领用备件</el-button>
          <el-button v-if="scope.row.status === 'IN_PROGRESS'" size="small" type="success" @click="completeRepair(scope.row.id)">完成维修</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="showPartDialog" title="领用备件" width="500px">
      <el-form :model="partForm" label-width="100px">
        <el-form-item label="备件">
          <el-select v-model="partForm.partId" style="width: 100%">
            <el-option v-for="p in spareParts" :key="p.id" :label="`${p.name} (库存: ${p.stock})`" :value="p.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="数量">
          <el-input-number v-model="partForm.quantity" :min="1" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showPartDialog = false">取消</el-button>
        <el-button type="primary" @click="confirmUsePart">确认领用</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="showCompleteDialog" title="完成维修" width="400px">
      <el-form :model="completeForm" label-width="100px">
        <el-form-item label="最新里程">
          <el-input-number v-model="completeForm.newMileage" :min="0" :step="100" style="width: 100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showCompleteDialog = false">取消</el-button>
        <el-button type="primary" @click="confirmComplete">确认完成</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import request from '../utils/request'

const repairOrders = ref([])
const spareParts = ref([])
const showPartDialog = ref(false)
const showCompleteDialog = ref(false)
const currentOrderId = ref(null)

const partForm = reactive({ partId: '', quantity: 1 })
const completeForm = reactive({ newMileage: 0 })

const loadRepairOrders = async () => {
  try {
    const res = await request.get('/faults/repair-orders')
    repairOrders.value = res.data
  } catch (error) {
    ElMessage.error('加载工单数据失败')
  }
}

const loadSpareParts = async () => {
  try {
    const res = await request.get('/spare-parts')
    spareParts.value = res.data
  } catch (error) {
    console.error(error)
  }
}

const getStatusType = (status) => {
  const map = {
    'PENDING': 'warning',
    'IN_PROGRESS': 'danger',
    'PAUSED': 'info',
    'COMPLETED': 'success'
  }
  return map[status] || 'info'
}

const getStatusText = (status) => {
  const map = {
    'PENDING': '待开始',
    'IN_PROGRESS': '进行中',
    'PAUSED': '已暂停',
    'COMPLETED': '已完成'
  }
  return map[status] || status
}

const startRepair = async (orderId) => {
  try {
    await request.post(`/faults/repair-orders/${orderId}/start`)
    ElMessage.success('维修已开始')
    loadRepairOrders()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const usePart = (row) => {
  currentOrderId.value = row.id
  showPartDialog.value = true
}

const confirmUsePart = async () => {
  try {
    await request.post('/spare-parts/use', {
      repairOrderId: currentOrderId.value,
      partId: partForm.partId,
      quantity: partForm.quantity
    })
    ElMessage.success('备件领用成功')
    showPartDialog.value = false
    partForm.partId = ''
    partForm.quantity = 1
    loadRepairOrders()
    loadSpareParts()
  } catch (error) {
    ElMessage.error('备件库存不足，已生成采购申请，维修暂停')
    showPartDialog.value = false
    loadRepairOrders()
  }
}

const completeRepair = (orderId) => {
  currentOrderId.value = orderId
  showCompleteDialog.value = true
}

const confirmComplete = async () => {
  try {
    await request.post(`/faults/repair-orders/${currentOrderId.value}/complete`, completeForm)
    ElMessage.success('维修完成')
    showCompleteDialog.value = false
    completeForm.newMileage = 0
    loadRepairOrders()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

onMounted(() => {
  loadRepairOrders()
  loadSpareParts()
})
</script>
