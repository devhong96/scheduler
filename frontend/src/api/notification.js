import client from './client'

// 현재 교사의 알림 목록 (최신순)
export const fetchNotifications = () => client.get('/notifications').then((r) => r.data)

// 알림 하나 읽음 처리
export const readNotification = (id) => client.post(`/notifications/${id}/read`)

// 전체 읽음 처리
export const readAllNotifications = () => client.post('/notifications/read-all')
