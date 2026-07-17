import client from './client'

// 교사 목록
export const fetchTeachers = () => client.get('/admin/teachers').then((r) => r.data)

// 교사 승인 / 승인취소
export const grantTeacher = (username) => client.post(`/admin/teachers/${username}/grant`)
export const revokeTeacher = (username) => client.post(`/admin/teachers/${username}/revoke`)

// 교사 계정 삭제
export const deleteTeacher = (username) => client.delete(`/admin/teachers/${username}`)

// 담당교사 변경 (studentId 학생을 teacherId 교사에게)
export const changeTeacher = (teacherId, studentId) =>
  client.post('/admin/change-teacher', { teacherId, studentId })
