import client from './client'

// 수업 시간표 목록 (교사는 본인 담당만)
export const fetchClasses = () => client.get('/manage/classes').then((r) => r.data)

// 수업 삭제
export const deleteClass = (studentName) =>
  client.delete(`/manage/classes/${encodeURIComponent(studentName)}`)

// 학생정보 목록 (검색 + 페이징) — { students, page, size, totalElements, totalPages, teachers }
export const fetchStudents = ({ page = 0, size = 10, studentName = '', teacherName = '' } = {}) =>
  client
    .get('/manage/students', { params: { page, size, studentName, teacherName } })
    .then((r) => r.data)

// 학생정보 등록
export const registerStudent = (payload) => client.post('/manage/students', payload)

// 학생정보 삭제
export const deleteStudent = (id) => client.delete(`/manage/students/${id}`)
