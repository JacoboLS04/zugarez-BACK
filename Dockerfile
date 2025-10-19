FROM openjdk:17-jdk-slim

# Install wget for Promtail
RUN apt-get update && apt-get install -y wget unzip

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

# Copy jar file
COPY target/zugarez-BACK-0.0.1-SNAPSHOT.jar app.jar

# Expose port
EXPOSE 8080

# Use the start script
CMD ["/start.sh"]