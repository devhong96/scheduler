import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { getAccount, changeEmail, changePassword } from '../api/account'

// 로그인한 본인 계정 설정: 이메일/비밀번호 변경
export default function AccountPage() {
  const [account, setAccount] = useState(null)
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [msg, setMsg] = useState('')
  const [error, setError] = useState('')

  useEffect(() => {
    getAccount()
      .then((a) => {
        setAccount(a)
        setEmail(a.email ?? '')
      })
      .catch(() => setError('계정 정보를 불러오지 못했습니다.'))
  }, [])

  const notify = (fn) => async (e) => {
    e.preventDefault()
    setMsg('')
    setError('')
    try {
      const { data } = await fn()
      setMsg(data.message)
    } catch (err) {
      setError(err.response?.data?.message ?? '변경에 실패했습니다.')
    }
  }

  const onEmail = notify(() => changeEmail(email))
  const onPassword = notify(async () => {
    const r = await changePassword(password)
    setPassword('')
    return r
  })

  if (!account) return <div style={styles.wrap}><p>{error || '불러오는 중…'}</p></div>

  return (
    <div style={styles.wrap}>
      <header style={styles.header}>
        <h1 style={styles.title}>계정 설정</h1>
        <Link to="/manage" style={styles.link}>← 수업 관리</Link>
      </header>

      <div style={styles.info}>
        <b>{account.username}</b> ({account.role})
      </div>

      {msg && <p style={styles.ok}>{msg}</p>}
      {error && <p style={styles.error}>{error}</p>}

      <form onSubmit={onEmail} style={styles.card}>
        <h2 style={styles.h2}>이메일 변경</h2>
        <input style={styles.input} type="email" value={email} onChange={(e) => setEmail(e.target.value)} />
        <button type="submit" style={styles.primary}>이메일 변경</button>
      </form>

      <form onSubmit={onPassword} style={styles.card}>
        <h2 style={styles.h2}>비밀번호 변경</h2>
        <input style={styles.input} type="password" placeholder="새 비밀번호" value={password} onChange={(e) => setPassword(e.target.value)} />
        <button type="submit" style={styles.primary}>비밀번호 변경</button>
      </form>
    </div>
  )
}

const styles = {
  wrap: { padding: 32, maxWidth: 480, margin: '0 auto' },
  header: { display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 12 },
  title: { margin: 0, fontSize: 24 },
  link: { fontSize: 14 },
  info: { marginBottom: 16, color: '#555' },
  card: { display: 'flex', flexDirection: 'column', gap: 10, padding: 20, border: '1px solid #e5e7eb', borderRadius: 12, background: '#fff', marginBottom: 16 },
  h2: { margin: 0, fontSize: 16 },
  input: { padding: '10px 12px', border: '1px solid #ccc', borderRadius: 8 },
  primary: { padding: 10, border: 'none', borderRadius: 8, background: '#2563eb', color: '#fff', cursor: 'pointer', alignSelf: 'flex-start' },
  ok: { color: '#059669', fontSize: 14 },
  error: { color: '#dc2626', fontSize: 14 },
}
