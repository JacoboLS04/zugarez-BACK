#!/bin/bash

# Create log directory
mkdir -p /var/log/app

# Start Promtail in background if running in production
if [ "$NODE_ENV" = "production" ] && [ -n "$LOKI_URL" ]; then
    echo "🚀 Starting Promtail for log collection..."
    /usr/local/bin/promtail -config.file=/etc/monitoring/promtail-config.yml > /var/log/promtail.log 2>&1 &
    echo "✅ Promtail started"
fi

# Start the main application with logging
echo "🎯 Starting Zugarez Backend..."
exec "$@" 2>&1 | tee /var/log/app/application.log
