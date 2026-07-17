import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import { AuthProvider } from './auth/AuthContext'
import ProtectedRoute from './components/ProtectedRoute'
import LoginPage from './pages/LoginPage'
import ManagePage from './pages/ManagePage'
import StudentListPage from './pages/StudentListPage'

export default function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Routes>
          <Route path="/login" element={<LoginPage />} />
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
          <Route path="*" element={<Navigate to="/manage" replace />} />
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  )
}
