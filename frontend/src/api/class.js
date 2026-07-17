import client from './client'

// 학생 이름으로 수강신청 폼 데이터 조회
// 응답: { studentName, hasClass, periods, current, mondayTaken, ... }
export const findClass = (studentName) =>
  client.post('/class/find', { studentName }).then((r) => r.data)

// 수강신청 제출 (payload: { studentName, monday, tuesday, wednesday, thursday, friday })
export const submitClass = (payload) =>
  client.post('/class/submit', payload).then((r) => r.data)
