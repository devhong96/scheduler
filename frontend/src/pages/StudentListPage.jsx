import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { fetchStudents, deleteStudent } from '../api/teacher'

export default function StudentListPage() {
  const [data, setData] = useState(null)
  const [page, setPage] = useState(0)
  const [search, setSearch] = useState('')
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  const load = () => {
    setLoading(true)
    fetchStudents({ page, size: 10, studentName: search })
      .then(setData)
      .catch(() => setError('학생 목록을 불러오지 못했습니다.'))
      .finally(() => setLoading(false))
  }

  useEffect(load, [page])

  const onSearch = (e) => {
    e.preventDefault()
    if (page === 0) load()
    else setPage(0)
  }

  const onDelete = async (id, name) => {
    if (!confirm(`${name} 학생 정보를 삭제할까요?`)) return
    try {
      await deleteStudent(id)
      load()
    } catch {
      alert('삭제에 실패했습니다.')
    }
  }

  return (
    <div style={styles.wrap}>
      <header style={styles.header}>
        <h1 style={styles.title}>학생 관리</h1>
        <Link to="/manage" style={styles.link}>
          ← 수업 관리
        </Link>
      </header>

      <form onSubmit={onSearch} style={styles.searchRow}>
        <input
          style={styles.input}
          placeholder="학생 이름 검색"
          value={search}
          onChange={(e) => setSearch(e.target.value)}
        />
        <button type="submit" style={styles.btn}>
          검색
        </button>
      </form>

      {loading ? (
        <p>불러오는 중…</p>
      ) : error ? (
        <p style={styles.error}>{error}</p>
      ) : (
        <>
          <table style={styles.table}>
            <thead>
              <tr>
                <th style={styles.th}>이름</th>
                <th style={styles.th}>연락처</th>
                <th style={styles.th}>주소</th>
                <th style={styles.th}>담당교사</th>
                <th style={styles.th}>관리</th>
              </tr>
            </thead>
            <tbody>
              {data.students.length === 0 ? (
                <tr>
                  <td style={styles.td} colSpan={5}>
                    등록된 학생이 없습니다.
                  </td>
                </tr>
              ) : (
                data.students.map((s) => (
                  <tr key={s.id}>
                    <td style={styles.td}>{s.studentName}</td>
                    <td style={styles.td}>{s.studentPhoneNumber}</td>
                    <td style={styles.td}>
                      {s.studentAddress} {s.studentDetailedAddress}
                    </td>
                    <td style={styles.td}>{s.teacherName}</td>
                    <td style={styles.td}>
                      <button
                        onClick={() => onDelete(s.id, s.studentName)}
                        style={styles.dangerBtn}
                      >
                        삭제
                      </button>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>

          <div style={styles.pager}>
            <button disabled={page <= 0} onClick={() => setPage((p) => p - 1)} style={styles.btn}>
              이전
            </button>
            <span style={styles.pageInfo}>
              {data.totalPages === 0 ? 0 : page + 1} / {data.totalPages}
            </span>
            <button
              disabled={page + 1 >= data.totalPages}
              onClick={() => setPage((p) => p + 1)}
              style={styles.btn}
            >
              다음
            </button>
          </div>
        </>
      )}
    </div>
  )
}

const styles = {
  wrap: { padding: 32, maxWidth: 960, margin: '0 auto' },
  header: { display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 20 },
  title: { margin: 0, fontSize: 24 },
  link: { fontSize: 14, color: '#2563eb' },
  searchRow: { display: 'flex', gap: 8, marginBottom: 16 },
  input: { flex: '0 0 240px', padding: '8px 10px', border: '1px solid #ccc', borderRadius: 8 },
  btn: { padding: '8px 14px', border: '1px solid #ccc', borderRadius: 8, background: '#fff', cursor: 'pointer' },
  table: { width: '100%', borderCollapse: 'collapse' },
  th: { borderBottom: '2px solid #ddd', padding: '10px 8px', textAlign: 'left', fontSize: 14 },
  td: { borderBottom: '1px solid #eee', padding: '10px 8px', fontSize: 14 },
  dangerBtn: { padding: '4px 10px', border: 'none', borderRadius: 6, background: '#dc2626', color: '#fff', cursor: 'pointer' },
  pager: { display: 'flex', gap: 12, alignItems: 'center', justifyContent: 'center', marginTop: 20 },
  pageInfo: { fontSize: 14 },
  error: { color: '#dc2626' },
}
