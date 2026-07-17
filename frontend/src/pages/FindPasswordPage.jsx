import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { findPassword, verifyCode, resetPassword } from '../api/account'

// 3단계: request(아이디+이메일→코드 발송) → verify(코드) → reset(새 비번)
export default function FindPasswordPage() {
  const navigate = useNavigate()
  const [step, setStep] = useState('request')
  const [username, setUsername] = useState('')
  const [email, setEmail] = useState('')
  const [code, setCode] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')
  const [info, setInfo] = useState('')

  const run = async (fn) => {
    setError('')
    setInfo('')
    try {
      return await fn()
    } catch (err) {
      setError(err.response?.data?.message ?? '요청에 실패했습니다.')
      return null
    }
  }

  const onRequest = async (e) => {
    e.preventDefault()
    const r = await run(() => findPassword(username, email))
    if (r) {
      setInfo(r.data.message)
      setStep('verify')
    }
  }
  const onVerify = async (e) => {
    e.preventDefault()
    const r = await run(() => verifyCode(username, code))
    if (r) setStep('reset')
  }
  const onReset = async (e) => {
    e.preventDefault()
    const r = await run(() => resetPassword(username, password))
    if (r) {
      alert('비밀번호가 변경되었습니다. 새 비밀번호로 로그인하세요.')
      navigate('/login')
    }
  }

  return (
    <div style={styles.wrap}>
      <div style={styles.card}>
        <h1 style={styles.title}>비밀번호 찾기</h1>

        {step === 'request' && (
          <form onSubmit={onRequest} style={styles.form}>
            <p style={styles.hint}>아이디와 이메일을 확인하면 인증번호를 보내드립니다.</p>
            <input style={styles.input} placeholder="아이디" value={username} onChange={(e) => setUsername(e.target.value)} />
            <input style={styles.input} type="email" placeholder="이메일" value={email} onChange={(e) => setEmail(e.target.value)} />
            <button type="submit" style={styles.primary}>인증번호 받기</button>
          </form>
        )}

        {step === 'verify' && (
          <form onSubmit={onVerify} style={styles.form}>
            <p style={styles.hint}>이메일로 받은 6자리 인증번호를 입력하세요.</p>
            <input style={styles.input} placeholder="인증번호 6자리" value={code} onChange={(e) => setCode(e.target.value)} />
            <button type="submit" style={styles.primary}>인증 확인</button>
          </form>
        )}

        {step === 'reset' && (
          <form onSubmit={onReset} style={styles.form}>
            <p style={styles.hint}>새 비밀번호를 입력하세요.</p>
            <input style={styles.input} type="password" placeholder="새 비밀번호" value={password} onChange={(e) => setPassword(e.target.value)} />
            <button type="submit" style={styles.primary}>비밀번호 변경</button>
          </form>
        )}

        {info && <p style={styles.ok}>{info}</p>}
        {error && <p style={styles.error}>{error}</p>}
        <div style={styles.center}>
          <Link to="/login" style={styles.link}>로그인으로</Link>
        </div>
      </div>
    </div>
  )
}

const styles = {
  wrap: { display: 'flex', justifyContent: 'center', padding: '40px 16px' },
  card: { display: 'flex', flexDirection: 'column', gap: 12, width: 340, padding: 28, border: '1px solid #ddd', borderRadius: 12, background: '#fff' },
  title: { margin: 0, fontSize: 22, textAlign: 'center' },
  form: { display: 'flex', flexDirection: 'column', gap: 10 },
  hint: { margin: 0, fontSize: 13, color: '#666', textAlign: 'center' },
  input: { padding: '10px 12px', border: '1px solid #ccc', borderRadius: 8 },
  primary: { padding: 10, border: 'none', borderRadius: 8, background: '#2563eb', color: '#fff', cursor: 'pointer' },
  center: { textAlign: 'center' },
  link: { color: '#2563eb', fontSize: 14 },
  error: { color: '#dc2626', fontSize: 13, margin: 0, textAlign: 'center' },
  ok: { color: '#059669', fontSize: 13, margin: 0, textAlign: 'center' },
}
