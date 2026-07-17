import { createContext, useContext, useEffect, useState } from 'react'
import client, { tokenStore } from '../api/client'

const AuthContext = createContext(null)

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null) // { username, roles }
  const [loading, setLoading] = useState(true)

  // 앱 시작 시 토큰이 있으면 사용자 정보 복원
  useEffect(() => {
    const token = tokenStore.getAccess()
    if (!token) {
      setLoading(false)
      return
    }
    client
      .get('/auth/me')
      .then((res) => setUser(res.data))
      .catch(() => tokenStore.clear())
      .finally(() => setLoading(false))
  }, [])

  const login = async (username, password) => {
    const { data } = await client.post('/auth/login', { username, password })
    tokenStore.set(data.accessToken, data.refreshToken)
    setUser({ username: data.username, roles: data.roles })
    return data
  }

  const logout = () => {
    tokenStore.clear()
    setUser(null)
  }

  return (
    <AuthContext.Provider value={{ user, loading, login, logout }}>
      {children}
    </AuthContext.Provider>
  )
}

export function useAuth() {
  return useContext(AuthContext)
}
