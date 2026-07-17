import { useEffect, useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { fetchNotices } from '../api/board'
import { useAuth } from '../auth/AuthContext'

export default function BoardListPage() {
  const { user } = useAuth()
  const navigate = useNavigate()
  const isAdmin = user?.roles?.includes('ROLE_ADMIN')

  const [data, setData] = useState(null)
  const [page, setPage] = useState(0)
  const [keyword, setKeyword] = useState('')
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  const load = () => {
    setLoading(true)
    fetchNotices({ page, size: 10, titleContent: keyword })
      .then(setData)
      .catch(() => setError('공지 목록을 불러오지 못했습니다.'))
      .finally(() => setLoading(false))
  }

  useEffect(load, [page])

  const onSearch = (e) => {
    e.preventDefault()
    page === 0 ? load() : setPage(0)
  }

  return (
    <div style={styles.wrap}>
      <header style={styles.header}>
        <h1 style={styles.title}>공지사항</h1>
        <div style={styles.right}>
          <Link to="/" style={styles.link}>
            ← 홈
          </Link>
          {isAdmin && (
            <button style={styles.primary} onClick={() => navigate('/board/new')}>
              글쓰기
            </button>
          )}
        </div>
      </header>

      <form onSubmit={onSearch} style={styles.searchRow}>
        <input
          style={styles.input}
          placeholder="제목/내용 검색"
          value={keyword}
          onChange={(e) => setKeyword(e.target.value)}
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
                <th style={{ ...styles.th, width: 60 }}>번호</th>
                <th style={styles.th}>제목</th>
                <th style={{ ...styles.th, width: 90 }}>작성자</th>
                <th style={{ ...styles.th, width: 60 }}>조회</th>
                <th style={{ ...styles.th, width: 110 }}>작성일</th>
              </tr>
            </thead>
            <tbody>
              {data.notices.length === 0 ? (
                <tr>
                  <td style={styles.td} colSpan={5}>
                    등록된 공지가 없습니다.
                  </td>
                </tr>
              ) : (
                data.notices.map((n) => (
                  <tr key={n.id}>
                    <td style={styles.td}>{n.id}</td>
                    <td style={styles.td}>
                      <Link to={`/board/${n.id}`}>{n.title}</Link>
                    </td>
                    <td style={styles.td}>{n.name}</td>
                    <td style={styles.td}>{n.views}</td>
                    <td style={styles.td}>{n.createdDate?.slice(0, 10)}</td>
                  </tr>
                ))
              )}
            </tbody>
          </table>

          <div style={styles.pager}>
            <button disabled={page <= 0} onClick={() => setPage((p) => p - 1)} style={styles.btn}>
              이전
            </button>
            <span>
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
  wrap: { padding: 32, maxWidth: 860, margin: '0 auto' },
  header: { display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 20 },
  right: { display: 'flex', alignItems: 'center', gap: 12 },
  title: { margin: 0, fontSize: 24 },
  link: { fontSize: 14 },
  searchRow: { display: 'flex', gap: 8, marginBottom: 16 },
  input: { flex: '0 0 260px', padding: '8px 10px', border: '1px solid #ccc', borderRadius: 8 },
  table: { width: '100%', borderCollapse: 'collapse' },
  th: { borderBottom: '2px solid #ddd', padding: '10px 8px', textAlign: 'left', fontSize: 14 },
  td: { borderBottom: '1px solid #eee', padding: '10px 8px', fontSize: 14 },
  pager: { display: 'flex', gap: 12, alignItems: 'center', justifyContent: 'center', marginTop: 20 },
  btn: { padding: '8px 14px', border: '1px solid #ccc', borderRadius: 8, background: '#fff', cursor: 'pointer' },
  primary: { padding: '8px 14px', border: 'none', borderRadius: 8, background: '#2563eb', color: '#fff', cursor: 'pointer' },
  error: { color: '#dc2626' },
}
