# Install Promtail
RUN wget -O /tmp/promtail.zip https://github.com/grafana/loki/releases/download/v2.9.0/promtail-linux-amd64.zip && \
    unzip /tmp/promtail.zip -d /tmp && \
    mv /tmp/promtail-linux-amd64 /usr/local/bin/promtail && \
    chmod +x /usr/local/bin/promtail && \
    rm /tmp/promtail.zip

# Copy monitoring config
COPY monitoring/ /etc/monitoring/
COPY start.sh /start.sh
RUN chmod +x /start.sh

# Create log directory
RUN mkdir -p /var/log/app

# Use the start script instead of direct node command
CMD ["/start.sh", "node", "app.js"]