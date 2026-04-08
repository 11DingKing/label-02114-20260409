import { createRequest } from '../utils/request-factory'
import { useUserStore } from '../stores/user'
import router from '../router'

const request = createRequest({
  getToken: () => useUserStore().token,
  onUnauthorized: () => {
    useUserStore().logout()
    router.push('/login')
  },
  onError: (msg) => console.error(msg)
})

export default request
