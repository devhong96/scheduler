import { useEffect, useState } from 'react'
import { Link, useNavigate, useParams } from 'react-router-dom'
import { fetchNotice, deleteNotice, addComment, removeComment } from '../api/board'
import { useAuth } from '../auth/AuthContext'

export default function BoardDetailPage() {
  const { id } = useParams()
  const navigate = useNavigate()
  const { user } = useAuth()
  const isAdmin = user?.roles?.includes('ROLE_ADMIN')

  const [data, setData] = useState(null)
  const [error, setError] = useState('')
  // 댓글 작성 폼 (학생 인증)
  const [form, setForm] = useState({ commentAuthor: '', password: '', comment: '' })
  const [commentError, setCommentError] = useState('')

  const load = () => {
    fetchNotice(id)
      .then(setData)
      .catch(() => setError('공지를 불러오지 못했습니다.'))
  }

  useEffect(load, [id])

  const onDeleteNotice = async () => {
    if (!confirm('이 공지를 삭제할까요?')) return
    try {
      await deleteNotice(id)
      navigate('/board')
    } catch {
      alert('삭제에 실패했습니다.')
    }
  }

  const onAddComment = async (e) => {
    e.preventDefault()
    setCommentError('')
    try {
      await addComment({ noticeId: Number(id), ...form })
      setForm({ commentAuthor: '', password: '', comment: '' })
      load()
    } catch (err) {
      setCommentError(err.response?.data?.message ?? '댓글 등록에 실패했습니다.')
    }
  }

  const onDeleteComment = async (commentId) => {
    const author = prompt('작성자 이름을 입력하세요')
    if (!author) return
    const password = prompt('학부모 전화번호(숫자만)를 입력하세요')
    if (!password) return
    try {
      await removeComment({ commentId, commentAuthor: author, password })
      load()
    } catch (err) {
      alert(err.response?.data?.message ?? '삭제에 실패했습니다.')
    }
  }

  if (error) return <div style={styles.wrap}><p style={styles.error}>{error}</p></div>
  if (!data) return <div style={styles.wrap}><p>불러오는 중…</p></div>

  const { notice, comments } = data

  return (
    <div style={styles.wrap}>
      <div style={styles.topbar}>
        <Link to="/board" style={styles.link}>
          ← 목록
        </Link>
        {isAdmin && (
          <div style={styles.actions}>
            <button style={styles.btn} onClick={() => navigate(`/board/${id}/edit`)}>
              수정
            </button>
            <button style={styles.danger} onClick={onDeleteNotice}>
              삭제
            </button>
          </div>
        )}
      </div>

      <article style={styles.card}>
        <h1 style={styles.title}>{notice.title}</h1>
        <div style={styles.meta}>
          작성자 {notice.name} · 조회 {notice.views} · {notice.createdDate?.slice(0, 10)}
        </div>
        <p style={styles.content}>{notice.content}</p>
      </article>

      <section style={styles.commentSection}>
        <h2 style={styles.h2}>댓글 {comments.length}</h2>
        {comments.length === 0 ? (
          <p style={styles.muted}>아직 댓글이 없습니다.</p>
        ) : (
          comments.map((c) => (
            <div key={c.id} style={styles.comment}>
              <div>
                <b>{c.commentAuthor}</b>{' '}
                <span style={styles.muted}>{c.createdDate?.slice(0, 16).replace('T', ' ')}</span>
                <div>{c.comment}</div>
              </div>
              <button style={styles.smallDanger} onClick={() => onDeleteComment(c.id)}>
                삭제
              </button>
            </div>
          ))
        )}

        <form onSubmit={onAddComment} style={styles.commentForm}>
          <div style={styles.commentRow}>
            <input
              style={styles.input}
              placeholder="이름"
              value={form.commentAuthor}
              onChange={(e) => setForm({ ...form, commentAuthor: e.target.value })}
              required
            />
            <input
              style={styles.input}
              placeholder="학부모 전화번호(숫자만)"
              value={form.password}
              onChange={(e) => setForm({ ...form, password: e.target.value })}
              required
            />
          </div>
          <textarea
            style={styles.textarea}
            placeholder="댓글을 입력하세요"
            value={form.comment}
            onChange={(e) => setForm({ ...form, comment: e.target.value })}
            required
          />
          {commentError && <p style={styles.error}>{commentError}</p>}
          <div style={{ textAlign: 'right' }}>
            <button type="submit" style={styles.primary}>
              댓글 등록
            </button>
          </div>
        </form>
      </section>
    </div>
  )
}

const styles = {
  wrap: { padding: 32, maxWidth: 760, margin: '0 auto' },
  topbar: { display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 16 },
  link: { fontSize: 14 },
  actions: { display: 'flex', gap: 8 },
  card: { border: '1px solid #e5e7eb', borderRadius: 12, padding: 24, background: '#fff' },
  title: { margin: '0 0 8px', fontSize: 22 },
  meta: { fontSize: 13, color: '#6b7280', marginBottom: 16 },
  content: { fontSize: 15, lineHeight: 1.7, whiteSpace: 'pre-wrap' },
  commentSection: { marginTop: 28 },
  h2: { fontSize: 18, margin: '0 0 12px' },
  muted: { color: '#9ca3af', fontSize: 13 },
  comment: { display: 'flex', justifyContent: 'space-between', gap: 12, padding: '12px 0', borderBottom: '1px solid #eee', fontSize: 14 },
  commentForm: { marginTop: 16, display: 'flex', flexDirection: 'column', gap: 8 },
  commentRow: { display: 'flex', gap: 8 },
  input: { flex: 1, padding: '8px 10px', border: '1px solid #ccc', borderRadius: 8 },
  textarea: { padding: '8px 10px', border: '1px solid #ccc', borderRadius: 8, minHeight: 70, resize: 'vertical' },
  primary: { padding: '8px 16px', border: 'none', borderRadius: 8, background: '#2563eb', color: '#fff', cursor: 'pointer' },
  btn: { padding: '6px 12px', border: '1px solid #ccc', borderRadius: 8, background: '#fff', cursor: 'pointer' },
  danger: { padding: '6px 12px', border: 'none', borderRadius: 8, background: '#dc2626', color: '#fff', cursor: 'pointer' },
  smallDanger: { padding: '4px 8px', border: 'none', borderRadius: 6, background: '#f3f4f6', color: '#dc2626', cursor: 'pointer', fontSize: 12, alignSelf: 'flex-start' },
  error: { color: '#dc2626', fontSize: 13, margin: 0 },
}
