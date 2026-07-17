import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../auth/AuthContext'

export default function LoginPage() {
  const { login } = useAuth()
  const navigate = useNavigate()
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')
  const [submitting, setSubmitting] = useState(false)

  const onSubmit = async (e) => {
    e.preventDefault()
    setError('')
    setSubmitting(true)
    try {
      await login(username, password)
      navigate('/manage')
    } catch (err) {
      setError(err.response?.data?.message ?? '로그인에 실패했습니다.')
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <div style={styles.wrap}>
      <form onSubmit={onSubmit} style={styles.card}>
        <h1 style={styles.title}>로그인</h1>

        <label style={styles.label}>
          아이디
          <input
            style={styles.input}
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            autoFocus
          />
        </label>

        <label style={styles.label}>
          비밀번호
          <input
            style={styles.input}
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
          />
        </label>

        {error && <p style={styles.error}>{error}</p>}

        <button type="submit" style={styles.button} disabled={submitting}>
          {submitting ? '처리 중…' : '로그인'}
        </button>

        <div style={styles.links}>
          <Link to="/signup" style={styles.link}>회원가입</Link>
          <Link to="/find-id" style={styles.link}>아이디 찾기</Link>
          <Link to="/find-password" style={styles.link}>비밀번호 찾기</Link>
        </div>
      </form>
    </div>
  )
}

const styles = {
  wrap: { display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: '100vh' },
  card: { display: 'flex', flexDirection: 'column', gap: 12, width: 320, padding: 32, border: '1px solid #ddd', borderRadius: 12 },
  title: { margin: '0 0 8px', fontSize: 22 },
  label: { display: 'flex', flexDirection: 'column', gap: 4, fontSize: 14 },
  input: { padding: '8px 10px', fontSize: 15, border: '1px solid #ccc', borderRadius: 8 },
  links: { display: 'flex', justifyContent: 'center', gap: 14, marginTop: 4, fontSize: 13 },
  link: { color: '#2563eb' },
  button: { marginTop: 8, padding: '10px', fontSize: 15, border: 'none', borderRadius: 8, background: '#2563eb', color: '#fff', cursor: 'pointer' },
  error: { color: '#dc2626', fontSize: 13, margin: 0 },
}
