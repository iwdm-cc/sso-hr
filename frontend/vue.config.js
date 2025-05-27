module.exports = {
  devServer: {
    port: 5000,
    host: '0.0.0.0',
    proxy: {
      '/api': {
        target: 'http://localhost:8000',
        changeOrigin: true,
        // 不重写路径，因为后端已有/api前缀
        pathRewrite: null
      }
    },
    allowedHosts: ['all'] // 修改为数组格式
  },
  lintOnSave: false
}
