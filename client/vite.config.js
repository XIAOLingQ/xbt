import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'

// https://vite.dev/config/
export default defineConfig({
  define: {
    'global': {},
  },
  plugins: [
    vue(),
    vueDevTools(),
  ],
  server: {
    proxy: {
      // API 请求的代理规则
      '/api': {
        target: 'http://localhost:8081',
        changeOrigin: true,
        configure: (proxy) => {
          proxy.on('proxyReq', (proxyReq, req) => {
            console.log(`[Vite Proxy /api] Forwarding request: ${req.method} ${req.url}`);
          });
        }
      },
      // WebSocket 连接的代理规则
      '/ws': {
        target: 'http://localhost:8081',
        ws: true,
        changeOrigin: true,
        configure: (proxy) => {
          proxy.on('proxyReq', (proxyReq, req) => {
            console.log(`[Vite Proxy /ws] Forwarding request: ${req.method} ${req.url}`);
          });
          proxy.on('error', (err) => {
            console.error('[Vite Proxy /ws] Error:', err.message);
          });
        }
      }
    }
  },
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    },
  },
})
