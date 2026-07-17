import { Navigate } from 'react-router-dom'
import { useAuth } from '../auth/AuthContext'

// roles 를 주면 해당 권한이 있는 사용자만 통과 (예: roles={['ROLE_ADMIN']})
export default function ProtectedRoute({ children, roles }) {
  const { user, loading } = useAuth()

  if (loading) return <div style={{ padding: 24 }}>불러오는 중…</div>
  if (!user) return <Navigate to="/login" replace />
  if (roles && !roles.some((r) => user.roles?.includes(r))) {
    return <Navigate to="/" replace />
  }
  return children
}
