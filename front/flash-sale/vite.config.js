import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// https://vitejs.dev/config/
export default defineConfig({
  server: {
    port: 80
  },
  plugins: [
    vue()
  ],
  base: '/', // 修改为根路径
  build: {
    outDir: 'dist', // 打包输出目录
    assetsDir: 'assets', // 静态资源目录
    chunkSizeWarningLimit: 1500, // 文件大小警告的限制（单位kb）
    rollupOptions: {
      output: {
        manualChunks(id) {
          // element-plus 相关依赖
          if (id.includes('node_modules/element-plus')) {
            return 'element-plus';
          }
          // vue 相关依赖
          if (id.includes('node_modules/vue') || 
              id.includes('node_modules/vue-router') || 
              id.includes('node_modules/pinia')) {
            return 'vue-vendor';
          }
          // axios 相关依赖
          if (id.includes('node_modules/axios')) {
            return 'axios';
          }
          // 其他第三方库
          if (id.includes('node_modules')) {
            return 'vendor';
          }
        },
        chunkFileNames: 'assets/js/[name]-[hash].js',
        entryFileNames: 'assets/js/[name]-[hash].js',
        assetFileNames: 'assets/[ext]/[name]-[hash].[ext]'
      }
    },
    terserOptions: {
      compress: {
        drop_console: true,  // 生产环境移除 console
        drop_debugger: true  // 生产环境移除 debugger
      }
    }
  },
  optimizeDeps: {
    include: ['vue', 'vue-router', 'pinia', 'element-plus'],
  },
  css: {
    preprocessorOptions: {
      css: {
        charset: false
      }
    }
  }
})
