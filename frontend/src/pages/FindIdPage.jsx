import { useState } from 'react'
import { Link } from 'react-router-dom'
import { findId } from '../api/account'

export default function FindIdPage() {
  const [email, setEmail] = useState('')
  const [message, setMessage] = useState('')
  const [error, setError] = useState('')

  const onSubmit = async (e) => {
    e.preventDefault()
    setError('')
    setMessage('')
    try {
      const { data } = await findId(email)
      setMessage(data.message)
    } catch (err) {
      setError(err.response?.data?.message ?? '조회에 실패했습니다.')
    }
  }

  return (
    <div style={styles.wrap}>
      <form onSubmit={onSubmit} style={styles.card}>
        <h1 style={styles.title}>아이디 찾기</h1>
        <p style={styles.hint}>가입한 이메일로 아이디를 보내드립니다.</p>
        <input style={styles.input} type="email" placeholder="이메일" value={email} onChange={(e) => setEmail(e.target.value)} />
        {error && <p style={styles.error}>{error}</p>}
        {message && <p style={styles.ok}>{message}</p>}
        <button type="submit" style={styles.primary}>아이디 전송</button>
        <div style={styles.center}>
          <Link to="/login" style={styles.link}>로그인으로</Link>
        </div>
      </form>
    </div>
  )
}

const styles = {
  wrap: { display: 'flex', justifyContent: 'center', padding: '40px 16px' },
  card: { display: 'flex', flexDirection: 'column', gap: 12, width: 340, padding: 28, border: '1px solid #ddd', borderRadius: 12, background: '#fff' },
  title: { margin: 0, fontSize: 22, textAlign: 'center' },
  hint: { margin: 0, fontSize: 13, color: '#666', textAlign: 'center' },
  input: { padding: '10px 12px', border: '1px solid #ccc', borderRadius: 8 },
  primary: { padding: 10, border: 'none', borderRadius: 8, background: '#2563eb', color: '#fff', cursor: 'pointer' },
  center: { textAlign: 'center' },
  link: { color: '#2563eb', fontSize: 14 },
  error: { color: '#dc2626', fontSize: 13, margin: 0, textAlign: 'center' },
  ok: { color: '#059669', fontSize: 13, margin: 0, textAlign: 'center' },
}
