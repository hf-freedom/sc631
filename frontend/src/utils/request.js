import axios from 'axios'

const request = axios.create({
  baseURL: 'http://localhost:8004/api',
  timeout: 10000
})

export default request
