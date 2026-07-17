import client from './client'

// --- 공개: 회원가입 & 계정 복구 ---
export const signup = (payload) => client.post('/auth/join', payload)
export const findId = (email) => client.post('/auth/find-id', { email })
export const findPassword = (username, email) =>
  client.post('/auth/find-password', { username, email })
export const verifyCode = (username, authNum) =>
  client.post('/auth/verify-code', { username, authNum })
export const resetPassword = (username, password) =>
  client.post('/auth/reset-password', { username, password })

// --- 로그인 상태: 본인 계정 관리 ---
export const getAccount = () => client.get('/account').then((r) => r.data)
export const changePassword = (password) => client.post('/account/change-password', { password })
export const changeEmail = (email) => client.post('/account/change-email', { email })
