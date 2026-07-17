import client from './client'

// 공지 목록 (검색 + 페이징) → { notices, page, size, totalElements, totalPages }
export const fetchNotices = ({ page = 0, size = 10, titleContent = '', author = '' } = {}) =>
  client.get('/board', { params: { page, size, titleContent, author } }).then((r) => r.data)

// 공지 상세 + 댓글 → { notice, comments }
export const fetchNotice = (id) => client.get(`/board/${id}`).then((r) => r.data)

// 공지 작성 (관리자)
export const createNotice = (payload) => client.post('/board', payload)

// 공지 수정 (관리자)
export const updateNotice = (id, payload) => client.put(`/board/${id}`, payload)

// 공지 삭제 (관리자)
export const deleteNotice = (id) => client.delete(`/board/${id}`)

// 댓글 작성 (학생 인증: commentAuthor + password=학부모 전화번호)
export const addComment = (payload) => client.post('/comment', payload)

// 댓글 삭제 (학생 인증)
export const removeComment = (payload) => client.delete('/comment', { data: payload })
