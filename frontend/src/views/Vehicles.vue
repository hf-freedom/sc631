<template>
  <div>
    <div style="display: flex; justify-content: space-between; margin-bottom: 20px">
      <h2>车辆管理</h2>
      <el-button type="primary" @click="showAddDialog = true">添加车辆</el-button>
    </div>

    <el-table :data="vehicles" border style="width: 100%">
      <el-table-column prop="plateNumber" label="车牌号" width="120" />
      <el-table-column prop="brand" label="品牌" width="100" />
      <el-table-column prop="model" label="型号" width="120" />
      <el-table-column prop="mileage" label="当前里程" width="120" />
      <el-table-column prop="lastMaintenanceMileage" label="上次保养里程" width="150" />
      <el-table-column prop="maintenanceCycle" label="保养周期" width="100" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="scope">
          <el-tag :type="getStatusType(scope.row.status)">
            {{ getStatusText(scope.row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="scope">
          <el-button size="small" @click="editVehicle(scope.row)">编辑</el-button>
          <el-button size="small" type="danger" @click="deleteVehicle(scope.row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="showAddDialog" title="添加车辆" width="500px">
      <el-form :model="vehicleForm" label-width="120px">
        <el-form-item label="车牌号">
          <el-input v-model="vehicleForm.plateNumber" />
        </el-form-item>
        <el-form-item label="品牌">
          <el-input v-model="vehicleForm.brand" />
        </el-form-item>
        <el-form-item label="型号">
          <el-input v-model="vehicleForm.model" />
        </el-form-item>
        <el-form-item label="当前里程">
          <el-input-number v-model="vehicleForm.mileage" :min="0" :step="100" />
        </el-form-item>
        <el-form-item label="上次保养里程">
          <el-input-number v-model="vehicleForm.lastMaintenanceMileage" :min="0" :step="100" />
        </el-form-item>
        <el-form-item label="保养周期">
          <el-input-number v-model="vehicleForm.maintenanceCycle" :min="1000" :step="1000" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAddDialog = false">取消</el-button>
        <el-button type="primary" @click="saveVehicle">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '../utils/request'

const vehicles = ref([])
const showAddDialog = ref(false)
const editingId = ref(null)
const vehicleForm = reactive({
  plateNumber: '',
  brand: '',
  model: '',
  mileage: 0,
  lastMaintenanceMileage: 0,
  maintenanceCycle: 5000
})

const loadVehicles = async () => {
  try {
    const res = await request.get('/vehicles')
    vehicles.value = res.data
  } catch (error) {
    ElMessage.error('加载车辆数据失败')
  }
}

const getStatusType = (status) => {
  const map = {
    'AVAILABLE': 'success',
    'IN_MAINTENANCE': 'warning',
    'IN_REPAIR': 'danger',
    'OUT_OF_SERVICE': 'info'
  }
  return map[status] || 'info'
}

const getStatusText = (status) => {
  const map = {
    'AVAILABLE': '可用',
    'IN_MAINTENANCE': '保养中',
    'IN_REPAIR': '维修中',
    'OUT_OF_SERVICE': '停用'
  }
  return map[status] || status
}

const editVehicle = (row) => {
  editingId.value = row.id
  Object.assign(vehicleForm, {
    plateNumber: row.plateNumber,
    brand: row.brand,
    model: row.model,
    mileage: row.mileage,
    lastMaintenanceMileage: row.lastMaintenanceMileage,
    maintenanceCycle: row.maintenanceCycle
  })
  showAddDialog.value = true
}

const saveVehicle = async () => {
  try {
    if (editingId.value) {
      await request.put(`/vehicles/${editingId.value}`, vehicleForm)
      ElMessage.success('更新成功')
    } else {
      await request.post('/vehicles', vehicleForm)
      ElMessage.success('添加成功')
    }
    showAddDialog.value = false
    resetForm()
    loadVehicles()
  } catch (error) {
    ElMessage.error('保存失败')
  }
}

const deleteVehicle = async (id) => {
  try {
    await ElMessageBox.confirm('确定删除该车辆吗?', '提示')
    await request.delete(`/vehicles/${id}`)
    ElMessage.success('删除成功')
    loadVehicles()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const resetForm = () => {
  editingId.value = null
  Object.assign(vehicleForm, {
    plateNumber: '',
    brand: '',
    model: '',
    mileage: 0,
    lastMaintenanceMileage: 0,
    maintenanceCycle: 5000
  })
}

onMounted(() => {
  loadVehicles()
})
</script>
