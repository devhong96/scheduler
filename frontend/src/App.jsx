import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import { AuthProvider } from './auth/AuthContext'
import ProtectedRoute from './components/ProtectedRoute'
import LoginPage from './pages/LoginPage'
import ManagePage from './pages/ManagePage'
import StudentListPage from './pages/StudentListPage'
import EnrollPage from './pages/EnrollPage'
import BoardListPage from './pages/BoardListPage'
import BoardDetailPage from './pages/BoardDetailPage'
import BoardFormPage from './pages/BoardFormPage'
import AdminTeachersPage from './pages/AdminTeachersPage'
import SignupPage from './pages/SignupPage'
import FindIdPage from './pages/FindIdPage'
import FindPasswordPage from './pages/FindPasswordPage'
import AccountPage from './pages/AccountPage'

export default function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Routes>
          {/* 공개: 학생 수강신청 (랜딩) */}
          <Route path="/" element={<EnrollPage />} />
          <Route path="/login" element={<LoginPage />} />
          <Route path="/signup" element={<SignupPage />} />
          <Route path="/find-id" element={<FindIdPage />} />
          <Route path="/find-password" element={<FindPasswordPage />} />

          {/* 공개: 공지사항 목록/상세 */}
          <Route path="/board" element={<BoardListPage />} />
          <Route path="/board/:id" element={<BoardDetailPage />} />
          {/* 관리자: 공지 작성/수정 */}
          <Route
            path="/board/new"
            element={
              <ProtectedRoute roles={['ROLE_ADMIN']}>
                <BoardFormPage />
              </ProtectedRoute>
            }
          />
          <Route
            path="/board/:id/edit"
            element={
              <ProtectedRoute roles={['ROLE_ADMIN']}>
                <BoardFormPage />
              </ProtectedRoute>
            }
          />
          <Route
            path="/manage"
            element={
              <ProtectedRoute>
                <ManagePage />
              </ProtectedRoute>
            }
          />
          <Route
            path="/manage/students"
            element={
              <ProtectedRoute>
                <StudentListPage />
              </ProtectedRoute>
            }
          />
          <Route
            path="/admin/teachers"
            element={
              <ProtectedRoute roles={['ROLE_ADMIN']}>
                <AdminTeachersPage />
              </ProtectedRoute>
            }
          />
          <Route
            path="/account"
            element={
              <ProtectedRoute>
                <AccountPage />
              </ProtectedRoute>
            }
          />
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  )
}
