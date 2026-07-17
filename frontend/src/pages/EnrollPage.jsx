import { useState } from 'react'
import { Link } from 'react-router-dom'
import { findClass, submitClass } from '../api/class'

const DAYS = [
  ['monday', '월요일', 'mondayTaken'],
  ['tuesday', '화요일', 'tuesdayTaken'],
  ['wednesday', '수요일', 'wednesdayTaken'],
  ['thursday', '목요일', 'thursdayTaken'],
  ['friday', '금요일', 'fridayTaken'],
]

export default function EnrollPage() {
  const [step, setStep] = useState('search') // search | form | done
  const [name, setName] = useState('')
  const [data, setData] = useState(null) // find 응답
  const [form, setForm] = useState({}) // { monday: 교시, ... }
  const [error, setError] = useState('')

  // 1) 이름으로 조회
  const onSearch = async (e) => {
    e.preventDefault()
    setError('')
    try {
      const res = await findClass(name.trim())
      setData(res)
      setForm({
        monday: res.current.monday,
        tuesday: res.current.tuesday,
        wednesday: res.current.wednesday,
        thursday: res.current.thursday,
        friday: res.current.friday,
      })
      setStep('form')
    } catch (err) {
      setError(err.response?.data?.message ?? '조회에 실패했습니다.')
    }
  }

  // 2) 제출
  const onSubmit = async (e) => {
    e.preventDefault()
    setError('')
    try {
      await submitClass({ studentName: data.studentName, ...form })
      setStep('done')
    } catch (err) {
      setError(err.response?.data?.message ?? '신청에 실패했습니다.')
    }
  }

  const pick = (dayKey, period) => setForm((prev) => ({ ...prev, [dayKey]: period }))

  if (step === 'done') {
    return (
      <div style={styles.wrap}>
        <div style={styles.card}>
          <h1 style={styles.title}>신청 완료 🎉</h1>
          <p style={styles.center}>{data.studentName} 학생의 수강신청이 저장되었습니다.</p>
          <button style={styles.primary} onClick={() => setStep('search')}>
            처음으로
          </button>
        </div>
      </div>
    )
  }

  if (step === 'form') {
    return (
      <div style={styles.wrap}>
        <form onSubmit={onSubmit} style={styles.card}>
          <h1 style={styles.title}>{data.studentName} 시간표</h1>
          <p style={styles.hint}>요일별로 시간을 선택하세요. 회색은 이미 마감된 시간입니다.</p>

          {DAYS.map(([dayKey, label, takenKey]) => {
            const taken = data[takenKey] ?? []
            return (
              <div key={dayKey} style={styles.dayBlock}>
                <div style={styles.dayLabel}>{label}</div>
                <div style={styles.periods}>
                  {data.periods.map((p) => {
                    const disabled = taken.includes(p)
                    const selected = form[dayKey] === p
                    return (
                      <button
                        type="button"
                        key={p}
                        disabled={disabled}
                        onClick={() => pick(dayKey, p)}
                        style={{
                          ...styles.periodBtn,
                          ...(selected ? styles.periodSelected : {}),
                          ...(disabled ? styles.periodDisabled : {}),
                        }}
                      >
                        오후 {p}시
                      </button>
                    )
                  })}
                  <button
                    type="button"
                    onClick={() => pick(dayKey, 0)}
                    style={{
                      ...styles.periodBtn,
                      ...(form[dayKey] === 0 ? styles.periodSelected : {}),
                    }}
                  >
                    등원 안 함
                  </button>
                </div>
              </div>
            )
          })}

          {error && <p style={styles.error}>{error}</p>}

          <div style={styles.row}>
            <button type="button" style={styles.ghost} onClick={() => setStep('search')}>
              ← 뒤로
            </button>
            <button type="submit" style={styles.primary}>
              제출하기
            </button>
          </div>
        </form>
      </div>
    )
  }

  // step === 'search'
  return (
    <div style={styles.wrap}>
      <form onSubmit={onSearch} style={styles.card}>
        <h1 style={styles.title}>시간표 조회</h1>
        <p style={styles.hint}>학생 이름을 정확히 입력해 주세요.</p>
        <input
          style={styles.input}
          value={name}
          onChange={(e) => setName(e.target.value)}
          placeholder="학생 이름"
          autoFocus
        />
        {error && <p style={styles.error}>{error}</p>}
        <button type="submit" style={styles.primary}>
          수업 조회
        </button>
        <div style={styles.center}>
          <Link to="/login" style={styles.link}>
            선생님/관리자 로그인
          </Link>
        </div>
      </form>
    </div>
  )
}

const styles = {
  wrap: { display: 'flex', justifyContent: 'center', padding: '40px 16px' },
  card: { display: 'flex', flexDirection: 'column', gap: 12, width: 380, padding: 28, border: '1px solid #ddd', borderRadius: 12, background: '#fff' },
  title: { margin: 0, fontSize: 22, textAlign: 'center' },
  hint: { margin: 0, fontSize: 13, color: '#666', textAlign: 'center' },
  center: { textAlign: 'center', fontSize: 14 },
  input: { padding: '10px 12px', fontSize: 15, border: '1px solid #ccc', borderRadius: 8 },
  dayBlock: { border: '1px solid #eee', borderRadius: 10, padding: 12 },
  dayLabel: { fontWeight: 600, marginBottom: 8 },
  periods: { display: 'flex', flexWrap: 'wrap', gap: 6 },
  periodBtn: { padding: '6px 10px', fontSize: 13, border: '1px solid #ccc', borderRadius: 8, background: '#fff', cursor: 'pointer' },
  periodSelected: { background: '#2563eb', color: '#fff', borderColor: '#2563eb' },
  periodDisabled: { background: '#f0f0f0', color: '#bbb', cursor: 'not-allowed' },
  row: { display: 'flex', gap: 8, justifyContent: 'space-between' },
  primary: { padding: '10px', fontSize: 15, border: 'none', borderRadius: 8, background: '#2563eb', color: '#fff', cursor: 'pointer', flex: 1 },
  ghost: { padding: '10px 14px', border: '1px solid #ccc', borderRadius: 8, background: '#fff', cursor: 'pointer' },
  link: { color: '#2563eb' },
  error: { color: '#dc2626', fontSize: 13, margin: 0, textAlign: 'center' },
}
