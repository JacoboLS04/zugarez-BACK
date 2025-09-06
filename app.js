// Test logging for monitoring
console.log('🚀 Zugarez Backend started - Monitoring test log');
console.log('📊 Environment:', process.env.NODE_ENV);
console.log('🔍 Loki URL configured:', !!process.env.LOKI_URL);

// Test logs for monitoring verification
console.log('🚀 [MONITORING TEST] Zugarez Backend started successfully');
console.log('📊 [MONITORING TEST] Environment:', process.env.NODE_ENV);
console.log('🔍 [MONITORING TEST] Platform:', process.env.PLATFORM);
console.log('📡 [MONITORING TEST] Loki URL configured:', !!process.env.LOKI_URL);

// Log every 30 seconds for testing
setInterval(() => {
  console.log(`⏰ [MONITORING TEST] Heartbeat - ${new Date().toISOString()}`);
}, 30000);

const { register, httpRequestDuration } = require('./monitoring/metrics');

// Metrics endpoint
app.get('/metrics', async (req, res) => {
  try {
    res.set('Content-Type', register.contentType);
    res.end(await register.metrics());
  } catch (ex) {
    res.status(500).end(ex);
  }
});

// Middleware para métricas HTTP
app.use((req, res, next) => {
  const end = httpRequestDuration.startTimer({
    method: req.method,
    route: req.route?.path || req.path
  });
  
  res.on('finish', () => {
    end({ status_code: res.statusCode });
  });
  
  next();
});

// Test logs para monitoreo
console.log('🚀 [MONITORING] Zugarez Backend started');
console.log('📊 [MONITORING] Environment:', process.env.NODE_ENV);
console.log('🌐 [MONITORING] Platform:', process.env.PLATFORM);

// Heartbeat logs
setInterval(() => {
  console.log(`💓 [HEARTBEAT] ${new Date().toISOString()} - Service running`);
}, 60000);