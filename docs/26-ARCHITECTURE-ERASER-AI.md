# Payments Engine V2 - Eraser AI Architecture Diagram

## 🎯 **Eraser AI Code for V2 Architecture**

This document contains the Eraser AI equivalent code for generating the Payments Engine v2 architecture diagram.

```eraser
title Payments Engine V2 - Complete Architecture (South Africa)

// === EXTERNAL CHANNELS ===
External Channels [icon: users] {
  Banking Channels [icon: bank, label: "Banking Channels"]
  Partner APIs [icon: api, label: "Partner APIs"]
  Mobile Apps [icon: smartphone, label: "Mobile Apps"]
  Web Portals [icon: monitor, label: "Web Portals"]
}

// === BFF LAYER ===
BFF Layer [icon: layers] {
  Payment BFF [icon: credit-card, label: "Payment BFF"]
  Operations BFF [icon: settings, label: "Operations BFF"]
  Analytics BFF [icon: bar-chart, label: "Analytics BFF"]
}

// === CORE SERVICES LAYER ===
Core Services Layer [icon: server] {
  Payment Initiation Service [icon: send, label: "Payment Initiation Service"]
  Validation Service [icon: shield, label: "Validation Service"]
  Account Adapter Service [icon: bank, label: "Account Adapter Service"]
  Routing Service [icon: map, label: "Routing Service"]
  Saga Orchestrator Service [icon: repeat, label: "Saga Orchestrator Service"]
  Payment Status Service [icon: check-circle, label: "Payment Status Service"]
}

// === CLEARING ADAPTERS LAYER ===
Clearing Adapters Layer [icon: shuffle] {
  SAMOS Adapter [icon: globe, label: "SAMOS Adapter"]
  BankservAfrica Adapter [icon: server, label: "BankservAfrica Adapter"]
  RTC Adapter [icon: clock, label: "RTC Adapter"]
  PayShap Adapter [icon: zap, label: "PayShap Adapter"]
  SWIFT Adapter [icon: globe, label: "SWIFT Adapter"]
}

// === PLATFORM SERVICES LAYER ===
Platform Services Layer [icon: settings] {
  IAM Service [icon: user-check, label: "IAM Service"]
  Notification Service [icon: bell, label: "Notification Service"]
  Audit Service [icon: file-lock, label: "Audit Service"]
  Reporting Service [icon: bar-chart, label: "Reporting Service"]
  Tenant Management Service [icon: users, label: "Tenant Management Service"]
}

// === OPERATIONS LAYER ===
Operations Layer [icon: activity] {
  Operations Management [icon: settings, label: "Operations Management"]
  Metrics Aggregation [icon: bar-chart, label: "Metrics Aggregation"]
  Channel Onboarding [icon: user-plus, label: "Channel Onboarding"]
  Clearing System Onboarding [icon: server, label: "Clearing System Onboarding"]
  Business Intelligence [icon: brain, label: "Business Intelligence"]
}

// === DATABASE LAYER ===
Database Layer [icon: database] {
  PostgreSQL [icon: postgres, label: "PostgreSQL (Core Data)"]
  Cassandra [icon: database, label: "Cassandra (ISO 20022 Messages)"]
  Redis [icon: redis, label: "Redis (Cache)"]
  EventStore [icon: file-lock, label: "EventStore (Audit Trail)"]
  TimescaleDB [icon: clock, label: "TimescaleDB (Metrics)"]
}

// === INFRASTRUCTURE LAYER ===
Infrastructure Layer [icon: server] {
  Kubernetes Cluster [icon: kubernetes, label: "Kubernetes Cluster"]
  Istio Service Mesh [icon: network, label: "Istio Service Mesh"]
  Prometheus Monitoring [icon: prometheus, label: "Prometheus Monitoring"]
  Grafana Dashboards [icon: grafana, label: "Grafana Dashboards"]
  Kafka Event Streaming [icon: kafka, label: "Kafka Event Streaming"]
}

// === EXTERNAL SYSTEMS ===
External Systems [icon: external-link] {
  Fraud Detection API [icon: activity, label: "Fraud Detection API"]
  Core Banking Systems [icon: bank, label: "Core Banking Systems"]
  Clearing Systems [icon: shuffle, label: "Clearing Systems"]
  Sanctions Screening [icon: alert-triangle, label: "Sanctions Screening"]
}

// === CONNECTIONS: EXTERNAL TO BFF ===
Banking Channels > Payment BFF
Partner APIs > Payment BFF
Mobile Apps > Payment BFF
Web Portals > Operations BFF

// === CONNECTIONS: BFF TO CORE SERVICES ===
Payment BFF > Payment Initiation Service
Payment BFF > Payment Status Service
Operations BFF > Operations Management
Analytics BFF > Metrics Aggregation

// === CONNECTIONS: CORE SERVICES FLOW ===
Payment Initiation Service > Validation Service
Payment Initiation Service > Account Adapter Service
Payment Initiation Service > Routing Service
Payment Initiation Service > Saga Orchestrator Service
Validation Service > Payment Status Service

// === CONNECTIONS: CORE SERVICES TO CLEARING ADAPTERS ===
Routing Service > SAMOS Adapter
Routing Service > BankservAfrica Adapter
Routing Service > RTC Adapter
Routing Service > PayShap Adapter
Routing Service > SWIFT Adapter

// === CONNECTIONS: PLATFORM SERVICES ===
Payment Initiation Service > IAM Service
Payment Initiation Service > Notification Service
Payment Initiation Service > Audit Service
Saga Orchestrator Service > Tenant Management Service

// === CONNECTIONS: OPERATIONS ===
Operations Management > Metrics Aggregation
Metrics Aggregation > Business Intelligence
Channel Onboarding > Tenant Management Service
Clearing System Onboarding > Tenant Management Service

// === CONNECTIONS: DATABASE LAYER ===
Payment Initiation Service > PostgreSQL
Payment Initiation Service > Cassandra
Payment Initiation Service > Redis
Payment Initiation Service > EventStore
Payment Initiation Service > TimescaleDB

Validation Service > PostgreSQL
Account Adapter Service > PostgreSQL
Routing Service > PostgreSQL

SAMOS Adapter > Cassandra
BankservAfrica Adapter > Cassandra
RTC Adapter > Cassandra
PayShap Adapter > Cassandra
SWIFT Adapter > Cassandra

Audit Service > EventStore
Metrics Aggregation > TimescaleDB

// === CONNECTIONS: EXTERNAL SYSTEMS ===
Validation Service > Fraud Detection API
Account Adapter Service > Core Banking Systems
SAMOS Adapter > Clearing Systems
BankservAfrica Adapter > Clearing Systems
RTC Adapter > Clearing Systems
PayShap Adapter > Clearing Systems
SWIFT Adapter > Sanctions Screening
SWIFT Adapter > Clearing Systems

// === CONNECTIONS: INFRASTRUCTURE ===
Kubernetes Cluster > Istio Service Mesh
Istio Service Mesh > Prometheus Monitoring
Prometheus Monitoring > Grafana Dashboards

// === CONNECTIONS: KAFKA EVENT STREAMING ===
Payment Initiation Service > Kafka Event Streaming
Validation Service > Kafka Event Streaming
Saga Orchestrator Service > Kafka Event Streaming
Payment Status Service > Kafka Event Streaming
Account Adapter Service > Kafka Event Streaming
Routing Service > Kafka Event Streaming
SAMOS Adapter > Kafka Event Streaming
BankservAfrica Adapter > Kafka Event Streaming
RTC Adapter > Kafka Event Streaming
PayShap Adapter > Kafka Event Streaming
SWIFT Adapter > Kafka Event Streaming
IAM Service > Kafka Event Streaming
Notification Service > Kafka Event Streaming
Audit Service > Kafka Event Streaming
Reporting Service > Kafka Event Streaming
Tenant Management Service > Kafka Event Streaming
Operations Management > Kafka Event Streaming
Metrics Aggregation > Kafka Event Streaming

// === LEGEND ===
// Blue: Core Services
// Green: Clearing Adapters  
// Yellow: Platform Services
// Emerald: Operations
// Purple: Infrastructure
// Red: External Systems
// Multi-color: Database Layer
```

## 🎯 **Eraser AI Features Used**

### **Icons & Labels:**
- **Service Icons**: Each service has appropriate icons (bank, shield, map, etc.)
- **Descriptive Labels**: Clear service descriptions
- **Layer Grouping**: Services grouped by architectural layers

### **Connection Types:**
- **Direct Connections**: `>` for direct service-to-service communication
- **Event Streaming**: All services connected to Kafka for event-driven architecture
- **Database Connections**: Each service connected to appropriate databases
- **External Integrations**: Clear connections to external systems

### **Architecture Layers:**
1. **External Channels** (Gray) - Entry points
2. **BFF Layer** (Blue) - Backend for Frontend services
3. **Core Services** (Blue) - Core payment processing
4. **Clearing Adapters** (Green) - South African clearing systems
5. **Platform Services** (Yellow) - Supporting services
6. **Operations** (Emerald) - Operational management
7. **Database Layer** (Multi-color) - Polyglot persistence
8. **Infrastructure** (Purple) - Kubernetes, monitoring, Kafka
9. **External Systems** (Red) - Third-party integrations

### **Key V2 Features:**
- **Event-Driven Architecture**: All services connected to Kafka
- **Polyglot Persistence**: 5 specialized databases
- **ISO 20022 Compliance**: Native message processing
- **UETR Correlation**: End-to-end transaction tracking
- **Multi-Tenant Architecture**: Tenant management integration
- **Service Mesh**: Istio for traffic management
- **High Performance**: 2000 TPS, sub-second response times
