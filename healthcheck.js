// Monitoring health check
app.get('/monitoring/health', (req, res) => {
  const monitoringConfig = {
    lokiUrl: process.env.LOKI_URL ? '✅ Configured' : '❌ Missing',
    lokiUsername: process.env.LOKI_USERNAME ? '✅ Configured' : '❌ Missing',
    grafanaToken: process.env.GRAFANA_ACCESS_TOKEN ? '✅ Configured' : '❌ Missing',
    environment: process.env.NODE_ENV || 'development'
  };
  
  res.json({
    status: 'ok',
    timestamp: new Date().toISOString(),
    monitoring: monitoringConfig
  });
});