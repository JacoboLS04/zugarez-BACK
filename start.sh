#!/bin/bash

# Create log directory
mkdir -p /var/log/app

# Start the Spring Boot application
echo "🎯 Starting Zugarez Spring Boot Backend..."
exec java -jar target/zugarez-BACK-0.0.1-SNAPSHOT.jar
    /usr/local/bin/promtail -config.file=/etc/monitoring/promtail-config.yml > /var/log/promtail.log 2>&1 &
    echo "✅ Promtail started"
fi

# Start the main application with logging
echo "🎯 Starting Zugarez Backend..."
exec "$@" 2>&1 | tee /var/log/app/application.log
