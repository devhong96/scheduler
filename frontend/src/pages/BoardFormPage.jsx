import { useEffect, useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { fetchNotice, createNotice, updateNotice } from '../api/board'

// 공지 작성/수정 (관리자). id 가 있으면 수정 모드.
export default function BoardFormPage() {
  const { id } = useParams()
  const navigate = useNavigate()
  const isEdit = Boolean(id)

  const [form, setForm] = useState({ title: '', content: '' })
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(isEdit)

  useEffect(() => {
    if (!isEdit) return
    fetchNotice(id)
      .then((d) => setForm({ title: d.notice.title, content: d.notice.content }))
      .catch(() => setError('공지를 불러오지 못했습니다.'))
      .finally(() => setLoading(false))
  }, [id, isEdit])

  const onSubmit = async (e) => {
    e.preventDefault()
    setError('')
    try {
      if (isEdit) {
        await updateNotice(id, form)
        navigate(`/board/${id}`)
      } else {
        await createNotice(form)
        navigate('/board')
      }
    } catch (err) {
      setError(err.response?.data?.message ?? '저장에 실패했습니다.')
    }
  }

  if (loading) return <div style={styles.wrap}><p>불러오는 중…</p></div>

  return (
    <div style={styles.wrap}>
      <h1 style={styles.title}>{isEdit ? '공지 수정' : '공지 작성'}</h1>
      <form onSubmit={onSubmit} style={styles.form}>
        <input
          style={styles.input}
          placeholder="제목"
          value={form.title}
          onChange={(e) => setForm({ ...form, title: e.target.value })}
          required
        />
        <textarea
          style={styles.textarea}
          placeholder="내용"
          value={form.content}
          onChange={(e) => setForm({ ...form, content: e.target.value })}
          required
        />
        {error && <p style={styles.error}>{error}</p>}
        <div style={styles.actions}>
          <button type="button" style={styles.btn} onClick={() => navigate(-1)}>
            취소
          </button>
          <button type="submit" style={styles.primary}>
            {isEdit ? '수정' : '등록'}
          </button>
        </div>
      </form>
    </div>
  )
}

const styles = {
  wrap: { padding: 32, maxWidth: 760, margin: '0 auto' },
  title: { fontSize: 22, marginBottom: 16 },
  form: { display: 'flex', flexDirection: 'column', gap: 12 },
  input: { padding: '10px 12px', border: '1px solid #ccc', borderRadius: 8, fontSize: 16 },
  textarea: { padding: '10px 12px', border: '1px solid #ccc', borderRadius: 8, minHeight: 240, resize: 'vertical', fontSize: 15, lineHeight: 1.6 },
  actions: { display: 'flex', justifyContent: 'flex-end', gap: 8 },
  btn: { padding: '10px 16px', border: '1px solid #ccc', borderRadius: 8, background: '#fff', cursor: 'pointer' },
  primary: { padding: '10px 20px', border: 'none', borderRadius: 8, background: '#2563eb', color: '#fff', cursor: 'pointer' },
  error: { color: '#dc2626', fontSize: 13, margin: 0 },
}
