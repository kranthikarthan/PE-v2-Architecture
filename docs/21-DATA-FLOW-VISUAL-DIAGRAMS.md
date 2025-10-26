# Data Flow Visual Diagrams v2 - Mermaid Diagrams

## 🎯 **Visual Data Flow Diagrams**

This document contains Mermaid diagrams showing the data flow across all databases in the Payments Engine v2 architecture.

## 📊 **1. Payment Initiation Data Flow**

```mermaid
graph TB
    subgraph CL["Client Layer"]
        A[Channel/Partner] --> B[BFF Services]
    end
    
    subgraph AL["Application Layer"]
        B --> C[Payment Initiation Service]
        C --> D[Validation Service]
        C --> E[UETR Service]
    end
    
    subgraph DL["Database Layer"]
        F[(PostgreSQL<br/>Core Data)]
        G[(Cassandra<br/>ISO 20022 Messages)]
        H[(Redis<br/>Cache)]
        I[(EventStore<br/>Audit Trail)]
        J[(TimescaleDB<br/>Metrics)]
    end
    
    subgraph BG["Bank's Gateway"]
        K[Clearing Systems]
        L[Fraud API]
        M[Core Banking]
    end
    
    C --> F
    C --> G
    C --> H
    C --> I
    C --> J
    D --> L
    C --> K
    C --> M
    
    style CL fill:#f8fafc,stroke:#e2e8f0,stroke-width:2px
    style AL fill:#f0f9ff,stroke:#0ea5e9,stroke-width:2px
    style DL fill:#f0fdf4,stroke:#22c55e,stroke-width:2px
    style BG fill:#fef2f2,stroke:#ef4444,stroke-width:2px
    
    style F fill:#dbeafe,stroke:#3b82f6,stroke-width:2px
    style G fill:#e0e7ff,stroke:#6366f1,stroke-width:2px
    style H fill:#fef3c7,stroke:#f59e0b,stroke-width:2px
    style I fill:#dcfce7,stroke:#16a34a,stroke-width:2px
    style J fill:#fce7f3,stroke:#ec4899,stroke-width:2px
```
*Note: The "Bank's Gateway" subgraph represents the banking institution's own infrastructure for connecting to external systems. The Payments Engine does not connect to these systems directly.*

## 📊 **2. ISO 20022 Message Processing Flow**

```mermaid
graph TB
    subgraph MP["Message Processing"]
        A[pain.001 Input] --> B[Message Parser]
        B --> C[Schema Validator]
        C --> D[Business Validator]
        D --> E[UETR Generator]
        E --> F[Message Transformer]
    end
    
    subgraph DS["Database Storage"]
        G[(PostgreSQL<br/>Payment Records)]
        H[(Cassandra<br/>Message Storage)]
        I[(EventStore<br/>Event History)]
        J[(Redis<br/>Processing Cache)]
    end
    
    subgraph MT["Message Types"]
        K[pain.001<br/>Payment Initiation]
        L[pacs.008<br/>Credit Transfer]
        M[pacs.002<br/>Status Report]
        N[pacs.004<br/>Payment Return]
        O[camt.054<br/>Notification]
    end
    
    F --> G
    F --> H
    F --> I
    F --> J
    
    H --> K
    H --> L
    H --> M
    H --> N
    H --> O
    
    style MP fill:#f0f9ff,stroke:#0ea5e9,stroke-width:3px
    style DS fill:#f0fdf4,stroke:#22c55e,stroke-width:3px
    style MT fill:#fefce8,stroke:#eab308,stroke-width:3px
    
    style G fill:#dbeafe,stroke:#3b82f6,stroke-width:2px
    style H fill:#e0e7ff,stroke:#6366f1,stroke-width:2px
    style I fill:#dcfce7,stroke:#16a34a,stroke-width:2px
    style J fill:#fef3c7,stroke:#f59e0b,stroke-width:2px
    style K fill:#fce7f3,stroke:#ec4899,stroke-width:2px
    style L fill:#fce7f3,stroke:#ec4899,stroke-width:2px
    style M fill:#fce7f3,stroke:#ec4899,stroke-width:2px
    style N fill:#fce7f3,stroke:#ec4899,stroke-width:2px
    style O fill:#fce7f3,stroke:#ec4899,stroke-width:2px
```

## 📊 **3. UETR Correlation Flow**

```mermaid
graph TB
    subgraph "UETR Correlation"
        A[UETR Generation] --> B[Message Correlation]
        B --> C[Cross-System Tracking]
        C --> D[Audit Trail Creation]
    end
    
    subgraph "Database Storage"
        E[(PostgreSQL<br/>UETR Registry)]
        F[(Cassandra<br/>Message Chain)]
        G[(EventStore<br/>Correlation Events)]
        H[(Redis<br/>Active Correlations)]
    end
    
    subgraph "Message Flow"
        I[pain.001] --> J[pacs.008]
        J --> K[pacs.002]
        K --> L[pacs.004]
        L --> M[camt.054]
    end
    
    A --> E
    B --> F
    C --> G
    D --> H
    
    I --> F
    J --> F
    K --> F
    L --> F
    M --> F
    
    style E fill:#e1f5fe
    style F fill:#f3e5f5
    style G fill:#e8f5e8
    style H fill:#ffebee
```

## 📊 **4. Multi-Tenant Data Flow**

```mermaid
graph TB
    subgraph "Tenant Hierarchy"
        A[Tenant] --> B[Business Unit]
        B --> C[Customer]
    end
    
    subgraph "Data Isolation"
        D[Row-Level Security]
        E[Tenant Context]
        F[Data Partitioning]
    end
    
    subgraph "Database Storage"
        G[(PostgreSQL<br/>Tenant Data)]
        H[(Cassandra<br/>Tenant Messages)]
        I[(Redis<br/>Tenant Cache)]
        J[(EventStore<br/>Tenant Events)]
    end
    
    A --> D
    B --> E
    C --> F
    
    D --> G
    E --> H
    F --> I
    G --> J
    
    style G fill:#e1f5fe
    style H fill:#f3e5f5
    style I fill:#ffebee
    style J fill:#e8f5e8
```

## 📊 **5. Database-Specific Data Flows**

### **PostgreSQL Data Flow**

```mermaid
graph TB
    subgraph "PostgreSQL Operations"
        A[Payment Initiation] --> B[Payment Table]
        C[Account Validation] --> D[Account Table]
        E[Tenant Management] --> F[Tenant Table]
        G[User Management] --> H[User Table]
    end
    
    subgraph "ACID Transactions"
        I[Payment Transaction]
        J[Account Transaction]
        K[Tenant Transaction]
        L[User Transaction]
    end
    
    B --> I
    D --> J
    F --> K
    H --> L
    
    style B fill:#e1f5fe
    style D fill:#e1f5fe
    style F fill:#e1f5fe
    style H fill:#e1f5fe
```

### **Cassandra Data Flow**

```mermaid
graph TB
    subgraph "Cassandra Operations"
        A[ISO 20022 Messages] --> B[Message Tables]
        C[UETR Correlation] --> D[Correlation Tables]
        E[Time-Series Data] --> F[Time-Series Tables]
    end
    
    subgraph "Partitioning Strategy"
        G[UETR Partitioning]
        H[Tenant Partitioning]
        I[Time Partitioning]
    end
    
    B --> G
    D --> H
    F --> I
    
    style B fill:#f3e5f5
    style D fill:#f3e5f5
    style F fill:#f3e5f5
```

### **Redis Data Flow**

```mermaid
graph TB
    subgraph "Redis Operations"
        A[Session Management] --> B[Session Cache]
        C[Rate Limiting] --> D[Rate Limit Cache]
        E[Payment Cache] --> F[Payment Cache]
        G[UETR Cache] --> H[UETR Cache]
    end
    
    subgraph "Cache Strategies"
        I[TTL Expiration]
        J[LRU Eviction]
        K[Write-Through]
        L[Write-Behind]
    end
    
    B --> I
    D --> J
    F --> K
    H --> L
    
    style B fill:#ffebee
    style D fill:#ffebee
    style F fill:#ffebee
    style H fill:#ffebee
```

### **EventStore Data Flow**

```mermaid
graph TB
    subgraph "EventStore Operations"
        A[Domain Events] --> B[Event Streams]
        C[Audit Events] --> D[Audit Streams]
        E[Correlation Events] --> F[Correlation Streams]
    end
    
    subgraph "Event Processing"
        G[Event Appending]
        H[Event Replay]
        I[Event Projection]
        J[Event Correlation]
    end
    
    B --> G
    D --> H
    F --> I
    B --> J
    
    style B fill:#e8f5e8
    style D fill:#e8f5e8
    style F fill:#e8f5e8
```

### **TimescaleDB Data Flow**

```mermaid
graph TB
    subgraph "TimescaleDB Operations"
        A[Performance Metrics] --> B[Metrics Tables]
        C[Business Metrics] --> D[Business Tables]
        E[Operational Data] --> F[Operational Tables]
    end
    
    subgraph "Time-Series Processing"
        G[Time Bucketing]
        H[Aggregation]
        I[Retention Policies]
        J[Compression]
    end
    
    B --> G
    D --> H
    F --> I
    B --> J
    
    style B fill:#fff3e0
    style D fill:#fff3e0
    style F fill:#fff3e0
```

## 📊 **6. Cross-Database Synchronization**

```mermaid
graph TB
    subgraph "Payment Status Synchronization"
        A[Payment Status Change] --> B[PostgreSQL]
        A --> C[EventStore]
        A --> D[Cassandra]
        A --> E[Redis]
        A --> F[TimescaleDB]
    end
    
    subgraph "UETR Correlation Synchronization"
        G[UETR Generation] --> H[PostgreSQL]
        G --> I[EventStore]
        G --> J[Cassandra]
        G --> K[Redis]
        G --> L[TimescaleDB]
    end
    
    subgraph "Consistency Strategies"
        M[Strong Consistency]
        N[Eventual Consistency]
        O[Session Consistency]
        P[Bounded Staleness]
    end
    
    B --> M
    C --> N
    D --> O
    E --> P
    
    style B fill:#e1f5fe
    style C fill:#e8f5e8
    style D fill:#f3e5f5
    style E fill:#ffebee
    style F fill:#fff3e0
```

## 📊 **7. Data Volume and Performance Flow**

```mermaid
graph TB
    subgraph "Performance Targets"
        A[PostgreSQL<br/>1000 TPS<br/><10ms Latency]
        B[Cassandra<br/>10,000 TPS<br/><5ms Latency]
        C[Redis<br/>50,000 TPS<br/><1ms Latency]
        D[EventStore<br/>5,000 TPS<br/><5ms Latency]
        E[TimescaleDB<br/>1,000 TPS<br/><10ms Latency]
    end
    
    subgraph "Data Volume (Daily)"
        F[PostgreSQL<br/>50GB]
        G[Cassandra<br/>200GB]
        H[Redis<br/>10GB]
        I[EventStore<br/>100GB]
        J[TimescaleDB<br/>50GB]
    end
    
    subgraph "Retention Policies"
        K[PostgreSQL<br/>7 years]
        L[Cassandra<br/>3 years]
        M[Redis<br/>24 hours]
        N[EventStore<br/>7 years]
        O[TimescaleDB<br/>2 years]
    end
    
    A --> F
    B --> G
    C --> H
    D --> I
    E --> J
    
    F --> K
    G --> L
    H --> M
    I --> N
    J --> O
    
    style A fill:#e1f5fe
    style B fill:#f3e5f5
    style C fill:#ffebee
    style D fill:#e8f5e8
    style E fill:#fff3e0
```

## 🎯 **Key Data Flow Insights**

### **1. Write Patterns**
- **PostgreSQL**: ACID transactions for core data
- **Cassandra**: High-volume message storage
- **Redis**: Real-time caching and sessions
- **EventStore**: Immutable audit trails
- **TimescaleDB**: Time-series analytics

### **2. Read Patterns**
- **PostgreSQL**: Complex queries and joins
- **Cassandra**: Fast lookups by UETR/tenant
- **Redis**: Sub-millisecond cache access
- **EventStore**: Event replay and correlation
- **TimescaleDB**: Time-based analytics

### **3. Consistency Patterns**
- **Strong Consistency**: Payment status, UETR registry
- **Eventual Consistency**: Message chains, metrics
- **Session Consistency**: User sessions, cache
- **Bounded Staleness**: Analytics, reporting

### **4. Performance Characteristics**
- **PostgreSQL**: 1000 TPS, <10ms latency
- **Cassandra**: 10,000 TPS, <5ms latency
- **Redis**: 50,000 TPS, <1ms latency
- **EventStore**: 5,000 TPS, <5ms latency
- **TimescaleDB**: 1,000 TPS, <10ms latency

---

**Version**: 2.0  
**Last Updated**: 2025-01-27  
**Status**: 🚀 Ready for Implementation  
**Next Review**: Weekly during implementation
