import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { fetchTeachers, grantTeacher, revokeTeacher, deleteTeacher } from '../api/admin'

// 관리자 전용: 교사 승인/승인취소/삭제
export default function AdminTeachersPage() {
  const [teachers, setTeachers] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  const load = () => {
    setLoading(true)
    fetchTeachers()
      .then(setTeachers)
      .catch(() => setError('교사 목록을 불러오지 못했습니다.'))
      .finally(() => setLoading(false))
  }

  useEffect(load, [])

  const onGrant = async (username) => {
    await grantTeacher(username)
    load()
  }
  const onRevoke = async (username) => {
    await revokeTeacher(username)
    load()
  }
  const onDelete = async (username) => {
    if (!confirm(`${username} 교사 계정을 삭제할까요?`)) return
    try {
      await deleteTeacher(username)
      load()
    } catch (err) {
      alert(err.response?.data?.message ?? '삭제에 실패했습니다.')
    }
  }

  return (
    <div style={styles.wrap}>
      <header style={styles.header}>
        <h1 style={styles.title}>교사 관리</h1>
        <Link to="/manage" style={styles.link}>
          ← 수업 관리
        </Link>
      </header>

      {loading ? (
        <p>불러오는 중…</p>
      ) : error ? (
        <p style={styles.error}>{error}</p>
      ) : (
        <table style={styles.table}>
          <thead>
            <tr>
              <th style={styles.th}>아이디</th>
              <th style={styles.th}>이름</th>
              <th style={styles.th}>승인 상태</th>
              <th style={styles.th}>관리</th>
            </tr>
          </thead>
          <tbody>
            {teachers.length === 0 ? (
              <tr>
                <td style={styles.td} colSpan={4}>
                  등록된 교사가 없습니다.
                </td>
              </tr>
            ) : (
              teachers.map((t) => (
                <tr key={t.id}>
                  <td style={styles.td}>{t.username}</td>
                  <td style={styles.td}>{t.teacherName}</td>
                  <td style={styles.td}>
                    <span style={t.approved ? styles.approved : styles.pending}>
                      {t.approved ? '승인됨' : '대기중'}
                    </span>
                  </td>
                  <td style={{ ...styles.td, display: 'flex', gap: 6 }}>
                    {t.approved ? (
                      <button style={styles.btn} onClick={() => onRevoke(t.username)}>
                        승인취소
                      </button>
                    ) : (
                      <button style={styles.primary} onClick={() => onGrant(t.username)}>
                        승인
                      </button>
                    )}
                    <button style={styles.danger} onClick={() => onDelete(t.username)}>
                      삭제
                    </button>
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      )}
    </div>
  )
}

const styles = {
  wrap: { padding: 32, maxWidth: 760, margin: '0 auto' },
  header: { display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 20 },
  title: { margin: 0, fontSize: 24 },
  link: { fontSize: 14 },
  table: { width: '100%', borderCollapse: 'collapse' },
  th: { borderBottom: '2px solid #ddd', padding: '10px 8px', textAlign: 'left', fontSize: 14 },
  td: { borderBottom: '1px solid #eee', padding: '10px 8px', fontSize: 14 },
  approved: { color: '#059669', fontWeight: 600 },
  pending: { color: '#d97706', fontWeight: 600 },
  btn: { padding: '4px 10px', border: '1px solid #ccc', borderRadius: 6, background: '#fff', cursor: 'pointer' },
  primary: { padding: '4px 10px', border: 'none', borderRadius: 6, background: '#2563eb', color: '#fff', cursor: 'pointer' },
  danger: { padding: '4px 10px', border: 'none', borderRadius: 6, background: '#dc2626', color: '#fff', cursor: 'pointer' },
  error: { color: '#dc2626' },
}
