import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// dev 서버: /api 요청을 Spring Boot(3205)로 프록시 → CORS 없이 개발
export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    proxy: {
      '/api': {
        target: 'http://localhost:3205',
        changeOrigin: true,
      },
    },
  },
})
