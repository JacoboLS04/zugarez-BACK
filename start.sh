#!/bin/bash

# Create log directory
mkdir -p /var/log/app

# Start Promtail in background if Loki URL is configured
if [ -n "$LOKI_URL" ]; then
    echo "🚀 Starting Promtail for log collection..."
    /usr/local/bin/promtail -config.file=/etc/monitoring/promtail-config.yml > /var/log/promtail.log 2>&1 &
    echo "✅ Promtail started"
fi

# Start the Spring Boot application
echo "🎯 Starting Zugarez Spring Boot Backend..."
exec java -jar target/zugarez-BACK-0.0.1-SNAPSHOT.jar
