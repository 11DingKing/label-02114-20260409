import { createRequest } from '../utils/request-factory'
import { ElMessage } from 'element-plus'
import { useUserStore } from '../stores/user'
import router from '../router'

const request = createRequest({
  getToken: () => useUserStore().token,
  onUnauthorized: () => {
    useUserStore().logout()
    router.push('/login')
  },
  onError: (msg) => ElMessage.error(msg)
})

export default request
