import axios from 'axios'

const TOKEN_KEY = 'accessToken'
const REFRESH_KEY = 'refreshToken'

export const tokenStore = {
  getAccess: () => localStorage.getItem(TOKEN_KEY),
  getRefresh: () => localStorage.getItem(REFRESH_KEY),
  set: (access, refresh) => {
    localStorage.setItem(TOKEN_KEY, access)
    if (refresh) localStorage.setItem(REFRESH_KEY, refresh)
  },
  clear: () => {
    localStorage.removeItem(TOKEN_KEY)
    localStorage.removeItem(REFRESH_KEY)
  },
}

// dev 에서는 vite 프록시(/api → :3205) 사용 → baseURL 은 상대경로
const client = axios.create({
  baseURL: '/api',
})

// 요청마다 access 토큰 자동 첨부
client.interceptors.request.use((config) => {
  const token = tokenStore.getAccess()
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

// 401 → refresh 토큰으로 1회 재발급 시도
let refreshing = null

client.interceptors.response.use(
  (res) => res,
  async (error) => {
    const original = error.config
    const status = error.response?.status

    if (status === 401 && !original._retry && tokenStore.getRefresh()) {
      original._retry = true
      try {
        refreshing =
          refreshing ||
          axios.post('/api/auth/refresh', { refreshToken: tokenStore.getRefresh() })
        const { data } = await refreshing
        refreshing = null
        tokenStore.set(data.accessToken, data.refreshToken)
        original.headers.Authorization = `Bearer ${data.accessToken}`
        return client(original)
      } catch (e) {
        refreshing = null
        tokenStore.clear()
        window.location.href = '/login'
        return Promise.reject(e)
      }
    }
    return Promise.reject(error)
  },
)

export default client
