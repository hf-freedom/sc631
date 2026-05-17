import { createRouter, createWebHistory } from 'vue-router'
import Dashboard from '../views/Dashboard.vue'
import Vehicles from '../views/Vehicles.vue'
import Faults from '../views/Faults.vue'
import RepairOrders from '../views/RepairOrders.vue'
import SpareParts from '../views/SpareParts.vue'
import MaintenanceReminders from '../views/MaintenanceReminders.vue'

const routes = [
  {
    path: '/',
    name: 'Dashboard',
    component: Dashboard
  },
  {
    path: '/vehicles',
    name: 'Vehicles',
    component: Vehicles
  },
  {
    path: '/faults',
    name: 'Faults',
    component: Faults
  },
  {
    path: '/repair-orders',
    name: 'RepairOrders',
    component: RepairOrders
  },
  {
    path: '/spare-parts',
    name: 'SpareParts',
    component: SpareParts
  },
  {
    path: '/maintenance-reminders',
    name: 'MaintenanceReminders',
    component: MaintenanceReminders
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
