const winston = require('winston');
const LokiTransport = require('winston-loki');

const logger = winston.createLogger({
  level: 'info',
  format: winston.format.combine(
    winston.format.timestamp(),
    winston.format.errors({ stack: true }),
    winston.format.json()
  ),
  transports: []
});

// Console transport (always active)
logger.add(new winston.transports.Console({
  format: winston.format.combine(
    winston.format.colorize(),
    winston.format.simple()
  )
}));

// Loki transport (only if configured)
if (process.env.LOKI_URL) {
  logger.add(new LokiTransport({
    host: process.env.LOKI_URL.replace('/loki/api/v1/push', ''),
    labels: {
      job: 'zugarez-backend',
      environment: process.env.NODE_ENV || 'development',
      platform: process.env.PLATFORM || 'koyeb'
    },
    basicAuth: process.env.LOKI_USERNAME && process.env.GRAFANA_ACCESS_TOKEN ? {
      username: process.env.LOKI_USERNAME,
      password: process.env.GRAFANA_ACCESS_TOKEN
    } : undefined
  }));
}

module.exports = logger;
