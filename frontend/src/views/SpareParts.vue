<template>
  <div>
    <h2 style="margin-bottom: 20px">备件管理</h2>

    <el-tabs v-model="activeTab">
      <el-tab-pane label="备件库存" name="parts">
        <el-table :data="spareParts" border style="width: 100%">
          <el-table-column prop="name" label="备件名称" width="150" />
          <el-table-column prop="model" label="型号" width="150" />
          <el-table-column prop="stock" label="库存数量" width="120">
            <template #default="scope">
              <el-tag :type="scope.row.stock < 5 ? 'danger' : 'success'">
                {{ scope.row.stock }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="unitPrice" label="单价" width="120">
            <template #default="scope">¥{{ scope.row.unitPrice }}</template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <el-tab-pane label="采购申请" name="purchase">
        <el-table :data="purchaseRequests" border style="width: 100%">
          <el-table-column prop="partName" label="备件名称" width="150" />
          <el-table-column prop="requiredQuantity" label="需求数量" width="120" />
          <el-table-column prop="status" label="状态" width="120">
            <template #default="scope">
              <el-tag :type="scope.row.status === 'COMPLETED' ? 'success' : 'warning'">
                {{ getPurchaseStatusText(scope.row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createdAt" label="创建时间" width="180" />
          <el-table-column label="操作" width="120">
            <template #default="scope">
              <el-button v-if="scope.row.status !== 'COMPLETED'" size="small" type="success" @click="completePurchase(scope.row.id)">完成采购</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <el-tab-pane label="领用记录" name="records">
        <el-table :data="usageRecords" border style="width: 100%">
          <el-table-column prop="partName" label="备件名称" width="150" />
          <el-table-column prop="quantity" label="数量" width="100" />
          <el-table-column prop="createdAt" label="领用时间" width="180" />
        </el-table>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '../utils/request'

const activeTab = ref('parts')
const spareParts = ref([])
const purchaseRequests = ref([])
const usageRecords = ref([])

const loadSpareParts = async () => {
  try {
    const res = await request.get('/spare-parts')
    spareParts.value = res.data
  } catch (error) {
    ElMessage.error('加载备件数据失败')
  }
}

const loadPurchaseRequests = async () => {
  try {
    const res = await request.get('/spare-parts/purchase-requests')
    purchaseRequests.value = res.data
  } catch (error) {
    console.error(error)
  }
}

const loadUsageRecords = async () => {
  try {
    const res = await request.get('/spare-parts/usage-records')
    usageRecords.value = res.data
  } catch (error) {
    console.error(error)
  }
}

const getPurchaseStatusText = (status) => {
  const map = { 'PENDING': '待采购', 'PURCHASING': '采购中', 'COMPLETED': '已完成' }
  return map[status] || status
}

const completePurchase = async (id) => {
  try {
    await request.post(`/spare-parts/purchase-requests/${id}/complete`)
    ElMessage.success('采购完成，库存已更新')
    loadPurchaseRequests()
    loadSpareParts()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

onMounted(() => {
  loadSpareParts()
  loadPurchaseRequests()
  loadUsageRecords()
})
</script>
