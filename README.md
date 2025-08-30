# 🚀 Zugarez Backend - Guía de Inicio Rápido

## 📋 **Stack Completo**
- **Spring Boot**: Backend API (Puerto 8080)
- **PostgreSQL**: Base de datos (Puerto 5432)
- **Prometheus**: Métricas (Puerto 9090)
- **Grafana**: Dashboards (Puerto 3001)
- **Loki**: Logs (Puerto 3100)

## 🏃‍♂️ **Inicio Rápido**

### **Opción 1: Inicio Manual (Recomendado)**
```bash
# 1. Iniciar stack de monitoreo
docker-compose -f docker-compose.monitoring.yml up -d

# 2. Iniciar aplicación Spring Boot
mvn spring-boot:run
```

### **Opción 2: Inicio Automático**
```bash
# Todo en uno
start-all.cmd
```

## 🔗 **URLs Importantes**

| Servicio | URL | Credenciales |
|----------|-----|--------------|
| **Backend API** | http://localhost:8080 | - |
| **Prometheus** | http://localhost:9090 | - |
| **Grafana** | http://localhost:3001 | admin/admin123 |
| **Loki** | http://localhost:3100 | - |

## 🛑 **Detener Servicios**

```bash
# Detener solo monitoreo
docker-compose -f docker-compose.monitoring.yml down

# O usar script
stop-all.cmd

# Spring Boot: Ctrl+C en la terminal
```

## 📊 **Dashboards en Grafana**
1. Accede a http://localhost:3001
2. Login: admin/admin123
3. Dashboard oficial de Spring Boot ya importado (ID: 10280)

## 🎯 **Desarrollo**
- El backend incluye JWT Authentication, Product CRUD e Inventory system
- Todas las métricas se exponen automáticamente en `/actuator/prometheus`
- Los logs se recolectan automáticamente con Promtail → Loki
