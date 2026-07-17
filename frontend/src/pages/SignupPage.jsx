import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { signup } from '../api/account'

export default function SignupPage() {
  const navigate = useNavigate()
  const [form, setForm] = useState({ username: '', password: '', teacherName: '', email: '' })
  const [error, setError] = useState('')
  const [done, setDone] = useState('')

  const set = (k) => (e) => setForm({ ...form, [k]: e.target.value })

  const onSubmit = async (e) => {
    e.preventDefault()
    setError('')
    try {
      const { data } = await signup(form)
      setDone(data.message)
      setTimeout(() => navigate('/login'), 1500)
    } catch (err) {
      setError(err.response?.data?.message ?? '가입에 실패했습니다.')
    }
  }

  return (
    <div style={styles.wrap}>
      <form onSubmit={onSubmit} style={styles.card}>
        <h1 style={styles.title}>교사 회원가입</h1>
        <input style={styles.input} placeholder="아이디" value={form.username} onChange={set('username')} />
        <input style={styles.input} type="password" placeholder="비밀번호" value={form.password} onChange={set('password')} />
        <input style={styles.input} placeholder="이름" value={form.teacherName} onChange={set('teacherName')} />
        <input style={styles.input} type="email" placeholder="이메일" value={form.email} onChange={set('email')} />
        {error && <p style={styles.error}>{error}</p>}
        {done && <p style={styles.ok}>{done}</p>}
        <button type="submit" style={styles.primary}>가입하기</button>
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
  input: { padding: '10px 12px', border: '1px solid #ccc', borderRadius: 8 },
  primary: { padding: 10, border: 'none', borderRadius: 8, background: '#2563eb', color: '#fff', cursor: 'pointer' },
  center: { textAlign: 'center' },
  link: { color: '#2563eb', fontSize: 14 },
  error: { color: '#dc2626', fontSize: 13, margin: 0, textAlign: 'center' },
  ok: { color: '#059669', fontSize: 13, margin: 0, textAlign: 'center' },
}
