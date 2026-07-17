import { useEffect, useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../auth/AuthContext'
import { fetchClasses, deleteClass } from '../api/teacher'
import NotificationBell from '../components/NotificationBell'

const DAYS = [
  ['monday', '월'],
  ['tuesday', '화'],
  ['wednesday', '수'],
  ['thursday', '목'],
  ['friday', '금'],
]

// 0(=미신청) 은 빈칸, 그 외 교시 숫자 표시
const cell = (v) => (v ? `${v}교시` : '-')

export default function ManagePage() {
  const { user, logout } = useAuth()
  const navigate = useNavigate()
  const [rows, setRows] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  const load = () => {
    setLoading(true)
    fetchClasses()
      .then(setRows)
      .catch(() => setError('수업 목록을 불러오지 못했습니다.'))
      .finally(() => setLoading(false))
  }

  useEffect(load, [])

  const onDelete = async (studentName) => {
    if (!confirm(`${studentName} 학생의 수업을 삭제할까요?`)) return
    try {
      await deleteClass(studentName)
      setRows((prev) => prev.filter((r) => r.studentName !== studentName))
    } catch {
      alert('삭제에 실패했습니다.')
    }
  }

  const onLogout = () => {
    logout()
    navigate('/login')
  }

  return (
    <div style={styles.wrap}>
      <header style={styles.header}>
        <h1 style={styles.title}>수업 관리</h1>
        <div style={styles.headerRight}>
          <NotificationBell />
          <span style={styles.who}>
            {user?.username} ({user?.roles?.join(', ')})
          </span>
          <Link to="/manage/students" style={styles.link}>
            학생 관리
          </Link>
          {user?.roles?.includes('ROLE_ADMIN') && (
            <Link to="/admin/teachers" style={styles.link}>
              교사 관리
            </Link>
          )}
          <button onClick={onLogout} style={styles.ghostBtn}>
            로그아웃
          </button>
        </div>
      </header>

      {loading ? (
        <p>불러오는 중…</p>
      ) : error ? (
        <p style={styles.error}>{error}</p>
      ) : rows.length === 0 ? (
        <p>등록된 수업이 없습니다.</p>
      ) : (
        <table style={styles.table}>
          <thead>
            <tr>
              <th style={styles.th}>학생</th>
              {DAYS.map(([, label]) => (
                <th key={label} style={styles.th}>
                  {label}
                </th>
              ))}
              <th style={styles.th}>담당교사</th>
              <th style={styles.th}>관리</th>
            </tr>
          </thead>
          <tbody>
            {rows.map((r) => (
              <tr key={r.studentName}>
                <td style={styles.td}>{r.studentName}</td>
                {DAYS.map(([key]) => (
                  <td key={key} style={styles.td}>
                    {cell(r[key])}
                  </td>
                ))}
                <td style={styles.td}>{r.teacherName}</td>
                <td style={styles.td}>
                  <button onClick={() => onDelete(r.studentName)} style={styles.dangerBtn}>
                    삭제
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  )
}

const styles = {
  wrap: { padding: 32, maxWidth: 960, margin: '0 auto' },
  header: { display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 24 },
  headerRight: { display: 'flex', alignItems: 'center', gap: 12 },
  title: { margin: 0, fontSize: 24 },
  who: { fontSize: 13, color: '#666' },
  link: { fontSize: 14, color: '#2563eb' },
  ghostBtn: { padding: '6px 12px', border: '1px solid #ccc', borderRadius: 8, background: '#fff', cursor: 'pointer' },
  table: { width: '100%', borderCollapse: 'collapse' },
  th: { borderBottom: '2px solid #ddd', padding: '10px 8px', textAlign: 'left', fontSize: 14 },
  td: { borderBottom: '1px solid #eee', padding: '10px 8px', fontSize: 14 },
  dangerBtn: { padding: '4px 10px', border: 'none', borderRadius: 6, background: '#dc2626', color: '#fff', cursor: 'pointer' },
  error: { color: '#dc2626' },
}
