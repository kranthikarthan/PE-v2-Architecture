# Architectural Patterns v2 - Enhanced Design Patterns

## 🎯 **Architectural Patterns Overview**

The Payments Engine v2 implements comprehensive architectural patterns to ensure maintainability, testability, and scalability. This document outlines the enhanced design patterns that build upon v1's foundation while adding ISO 20022 compliance and performance improvements.

## 🏗️ **Core Architectural Patterns**

### **1. Hexagonal Architecture (Ports & Adapters)**

#### **Pattern Implementation**
```yaml
Hexagonal Architecture Benefits:
  - Testability: Easy to mock external dependencies
  - Flexibility: Swap implementations without changing core logic
  - Maintainability: Clear separation of concerns
  - ISO 20022 Compliance: Clean message processing boundaries
```

#### **Ports (Interfaces)**
```java
// Payment Processing Ports
public interface PaymentInitiationPort {
    PaymentResponse initiatePayment(PaymentRequest request);
    PaymentStatus getPaymentStatus(String paymentId);
}

public interface Iso20022MessagePort {
    Iso20022Message processPain001(Pain001Message message);
    Iso20022Message processPain002(Pain002Message message);
    Iso20022Message processPacs008(Pacs008Message message);
}

public interface UetrCorrelationPort {
    UetrCorrelation correlateMessages(String uetr);
    void trackMessageChain(String uetr, Iso20022Message message);
}

// External System Ports
public interface ClearingSystemPort {
    ClearingResponse sendPayment(ClearingRequest request);
    ClearingStatus getPaymentStatus(String clearingId);
}

public interface FraudDetectionPort {
    FraudScore assessFraudRisk(PaymentRequest request);
    FraudResult validatePayment(PaymentRequest request);
}
```

#### **Adapters (Implementations)**
```java
// Primary Adapters (Driving)
@RestController
public class PaymentInitiationController implements PaymentInitiationPort {
    private final PaymentInitiationService paymentService;
    
    @PostMapping("/api/v1/payments")
    public PaymentResponse initiatePayment(@RequestBody PaymentRequest request) {
        return paymentService.initiatePayment(request);
    }
}

// Secondary Adapters (Driven)
@Component
public class PostgresPaymentRepository implements PaymentRepositoryPort {
    private final JpaRepository<Payment, String> repository;
    
    @Override
    public Payment save(Payment payment) {
        return repository.save(payment);
    }
}

@Component
public class KafkaEventPublisher implements EventPublisherPort {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    
    @Override
    public void publishEvent(DomainEvent event) {
        kafkaTemplate.send("payment-events", event);
    }
}
```

### **2. Domain-Driven Design (DDD)**

#### **Domain Model Structure**
```yaml
Domain Layers:
  - Domain Entities: Core business objects
  - Value Objects: Immutable business concepts
  - Domain Services: Business logic coordination
  - Domain Events: Business state changes
  - Repositories: Data access abstractions
```

#### **Domain Entities**
```java
// Payment Aggregate Root
@Entity
@Table(name = "payments")
public class Payment {
    @Id
    private PaymentId id;
    private Uetr uetr;
    private Money amount;
    private PaymentStatus status;
    private TenantId tenantId;
    private List<DomainEvent> domainEvents;
    
    // Business Logic
    public void initiate(PaymentRequest request) {
        validatePaymentRequest(request);
        this.status = PaymentStatus.PROCESSING;
        this.uetr = Uetr.generate();
        addDomainEvent(new PaymentInitiatedEvent(this.id, this.uetr));
    }
    
    public void complete() {
        if (this.status != PaymentStatus.PROCESSING) {
            throw new IllegalStateException("Payment must be processing to complete");
        }
        this.status = PaymentStatus.COMPLETED;
        addDomainEvent(new PaymentCompletedEvent(this.id, this.uetr));
    }
}

// Value Objects
@Embeddable
public class Money {
    private BigDecimal amount;
    private Currency currency;
    
    public Money(BigDecimal amount, Currency currency) {
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Amount cannot be negative");
        }
        this.amount = amount;
        this.currency = currency;
    }
}

@Embeddable
public class Uetr {
    private String value;
    
    public static Uetr generate() {
        return new Uetr(UUID.randomUUID().toString());
    }
}
```

#### **Domain Services**
```java
@Service
public class PaymentDomainService {
    
    public PaymentValidationResult validatePayment(PaymentRequest request) {
        // Business rules validation
        if (request.getAmount().isNegative()) {
            return PaymentValidationResult.invalid("Amount cannot be negative");
        }
        
        if (request.getSourceAccount().equals(request.getDestinationAccount())) {
            return PaymentValidationResult.invalid("Source and destination accounts cannot be the same");
        }
        
        return PaymentValidationResult.valid();
    }
    
    public Iso20022Message transformToPain001(Payment payment) {
        // Domain logic for ISO 20022 transformation
        return Pain001Message.builder()
            .uetr(payment.getUetr().getValue())
            .amount(payment.getAmount().getAmount())
            .currency(payment.getAmount().getCurrency().getCode())
            .build();
    }
}
```

### **3. CQRS (Command Query Responsibility Segregation)**

#### **Command Side (Write Model)**
```java
// Commands
public class InitiatePaymentCommand {
    private PaymentId paymentId;
    private Money amount;
    private AccountId sourceAccount;
    private AccountId destinationAccount;
    private TenantId tenantId;
}

public class CompletePaymentCommand {
    private PaymentId paymentId;
    private Uetr uetr;
}

// Command Handlers
@Component
public class InitiatePaymentCommandHandler {
    
    @CommandHandler
    public PaymentResponse handle(InitiatePaymentCommand command) {
        // Command processing logic
        Payment payment = Payment.create(command);
        paymentRepository.save(payment);
        
        // Publish domain events
        eventPublisher.publish(new PaymentInitiatedEvent(payment.getId()));
        
        return PaymentResponse.from(payment);
    }
}
```

#### **Query Side (Read Model)**
```java
// Query Models
public class PaymentQueryModel {
    private PaymentId id;
    private Uetr uetr;
    private Money amount;
    private PaymentStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

// Query Handlers
@Component
public class PaymentQueryHandler {
    
    @QueryHandler
    public PaymentQueryModel handle(GetPaymentQuery query) {
        return paymentQueryRepository.findById(query.getPaymentId());
    }
    
    @QueryHandler
    public List<PaymentQueryModel> handle(GetPaymentsByTenantQuery query) {
        return paymentQueryRepository.findByTenantId(query.getTenantId());
    }
}
```

### **4. Event Sourcing**

#### **Event Store Implementation**
```java
// Domain Events
public class PaymentInitiatedEvent extends DomainEvent {
    private PaymentId paymentId;
    private Uetr uetr;
    private Money amount;
    private TenantId tenantId;
    private LocalDateTime timestamp;
}

public class PaymentCompletedEvent extends DomainEvent {
    private PaymentId paymentId;
    private Uetr uetr;
    private LocalDateTime completedAt;
}

// Event Store
@Component
public class EventStore {
    
    public void saveEvents(String aggregateId, List<DomainEvent> events, int expectedVersion) {
        // Optimistic concurrency control
        for (DomainEvent event : events) {
            event.setVersion(expectedVersion + 1);
            eventRepository.save(event);
        }
    }
    
    public List<DomainEvent> getEvents(String aggregateId) {
        return eventRepository.findByAggregateIdOrderByVersion(aggregateId);
    }
}
```

### **5. Saga Pattern Implementation**

#### **Saga Orchestrator**
```java
@Component
public class PaymentSagaOrchestrator {
    
    @SagaOrchestrationStart
    public void handle(PaymentInitiatedEvent event) {
        // Step 1: Validate Payment
        sagaManager.send(new ValidatePaymentCommand(event.getPaymentId()));
    }
    
    @SagaOrchestrationStep
    public void handle(PaymentValidatedEvent event) {
        // Step 2: Check Limits
        sagaManager.send(new CheckLimitsCommand(event.getPaymentId()));
    }
    
    @SagaOrchestrationStep
    public void handle(LimitsCheckedEvent event) {
        // Step 3: Fraud Check
        sagaManager.send(new FraudCheckCommand(event.getPaymentId()));
    }
    
    @SagaOrchestrationStep
    public void handle(FraudCheckCompletedEvent event) {
        if (event.isApproved()) {
            // Step 4: Process Payment
            sagaManager.send(new ProcessPaymentCommand(event.getPaymentId()));
        } else {
            // Compensate: Reject Payment
            sagaManager.send(new RejectPaymentCommand(event.getPaymentId()));
        }
    }
    
    @SagaOrchestrationStep
    public void handle(PaymentProcessedEvent event) {
        // Step 5: Send to Clearing
        sagaManager.send(new SendToClearingCommand(event.getPaymentId()));
    }
    
    // Compensation Logic
    @SagaOrchestrationCompensation
    public void compensate(PaymentInitiatedEvent event) {
        sagaManager.send(new CancelPaymentCommand(event.getPaymentId()));
    }
}
```

#### **Saga State Machine**
```java
@Component
public class PaymentSagaStateMachine {
    
    @StateMachine
    public class PaymentSaga {
        
        @State(initial = true)
        public class Initiated {}
        
        @State
        public class Validating {}
        
        @State
        public class Validated {}
        
        @State
        public class CheckingLimits {}
        
        @State
        public class LimitsChecked {}
        
        @State
        public class FraudChecking {}
        
        @State
        public class FraudChecked {}
        
        @State
        public class Processing {}
        
        @State
        public class Completed {}
        
        @State
        public class Failed {}
        
        // Transitions
        @Transition(from = "Initiated", to = "Validating")
        public void validatePayment() {}
        
        @Transition(from = "Validating", to = "Validated")
        public void paymentValidated() {}
        
        @Transition(from = "Validated", to = "CheckingLimits")
        public void checkLimits() {}
        
        @Transition(from = "CheckingLimits", to = "LimitsChecked")
        public void limitsChecked() {}
        
        @Transition(from = "LimitsChecked", to = "FraudChecking")
        public void fraudCheck() {}
        
        @Transition(from = "FraudChecking", to = "FraudChecked")
        public void fraudChecked() {}
        
        @Transition(from = "FraudChecked", to = "Processing")
        public void processPayment() {}
        
        @Transition(from = "Processing", to = "Completed")
        public void paymentCompleted() {}
        
        // Compensation Transitions
        @Transition(from = "Validating", to = "Failed")
        public void validationFailed() {}
        
        @Transition(from = "CheckingLimits", to = "Failed")
        public void limitsExceeded() {}
        
        @Transition(from = "FraudChecking", to = "Failed")
        public void fraudDetected() {}
    }
}
```

## 🔧 **Implementation Guidelines**

### **1. Hexagonal Architecture Implementation**
```yaml
Service Structure:
  - Ports: Define interfaces in domain layer
  - Adapters: Implement ports in infrastructure layer
  - Domain: Core business logic (no dependencies)
  - Application: Use cases and orchestration
  - Infrastructure: External system integrations

Benefits:
  - Testability: Mock external dependencies
  - Flexibility: Swap implementations
  - Maintainability: Clear boundaries
  - ISO 20022: Clean message processing
```

### **2. DDD Implementation**
```yaml
Domain Model:
  - Entities: Payment, Account, Transaction
  - Value Objects: Money, Uetr, PaymentId
  - Aggregates: Payment (root), Transaction (child)
  - Domain Services: Business logic coordination
  - Repositories: Data access abstractions

Benefits:
  - Business Logic: Centralized and testable
  - Domain Knowledge: Captured in code
  - Maintainability: Clear business rules
  - ISO 20022: Domain-specific message handling
```

### **3. CQRS Implementation**
```yaml
Command Side:
  - Commands: InitiatePayment, CompletePayment
  - Command Handlers: Business logic execution
  - Write Models: Optimized for writes
  - Event Publishing: Domain events

Query Side:
  - Queries: GetPayment, GetPaymentsByTenant
  - Query Handlers: Data retrieval
  - Read Models: Optimized for reads
  - Projections: Denormalized views

Benefits:
  - Performance: Optimized read/write models
  - Scalability: Independent scaling
  - Flexibility: Different data models
  - ISO 20022: Separate message processing
```

### **4. Event Sourcing Implementation**
```yaml
Event Store:
  - Events: Immutable business facts
  - Aggregates: Reconstructed from events
  - Snapshots: Performance optimization
  - Projections: Read model updates

Benefits:
  - Audit Trail: Complete event history
  - Replay: Reconstruct state from events
  - Debugging: Event sequence analysis
  - ISO 20022: Message correlation history
```

### **5. Saga Pattern Implementation**
```yaml
Saga Types:
  - Orchestration: Centralized coordination
  - Choreography: Distributed coordination
  - State Machine: Explicit state transitions
  - Compensation: Rollback mechanisms

Benefits:
  - Consistency: Distributed transaction management
  - Reliability: Compensation for failures
  - Scalability: Distributed processing
  - ISO 20022: End-to-end message flows
```

## 📊 **Pattern Integration with ISO 20022**

### **Hexagonal Architecture + ISO 20022**
```yaml
Ports:
  - Iso20022MessagePort: Message processing interface
  - UetrCorrelationPort: UETR tracking interface
  - SchemaValidationPort: XSD/JSON validation interface

Adapters:
  - Pain001Adapter: pain.001 message processing
  - Pain002Adapter: pain.002 message processing
  - Pacs008Adapter: pacs.008 message processing
  - XsdValidatorAdapter: Schema validation
```

### **DDD + ISO 20022**
```yaml
Domain Entities:
  - Iso20022Message: Message aggregate
  - UetrCorrelation: Correlation aggregate
  - MessageChain: Message sequence aggregate

Value Objects:
  - MessageId: ISO 20022 message identifier
  - Uetr: Unique end-to-end transaction reference
  - MessageType: pain.001, pain.002, etc.
```

### **CQRS + ISO 20022**
```yaml
Commands:
  - ProcessPain001Command: pain.001 processing
  - GeneratePain002Command: pain.002 generation
  - CorrelateMessagesCommand: UETR correlation

Queries:
  - GetMessageByUetrQuery: UETR-based retrieval
  - GetMessageChainQuery: Message sequence
  - GetMessageStatusQuery: Processing status
```

## 🚀 **Implementation Roadmap**

### **Phase 1: Foundation Patterns (Week 1)**
1. **Hexagonal Architecture**: Implement ports and adapters
2. **DDD**: Create domain models and services
3. **Basic CQRS**: Separate command and query models

### **Phase 2: Advanced Patterns (Week 2)**
1. **Event Sourcing**: Implement event store
2. **Saga Pattern**: Create saga orchestrator
3. **ISO 20022 Integration**: Message processing patterns

### **Phase 3: Optimization (Week 3)**
1. **Performance Tuning**: Optimize pattern implementations
2. **Testing**: Comprehensive pattern testing
3. **Documentation**: Pattern usage guidelines

---

**Version**: 2.0  
**Last Updated**: 2025-01-27  
**Status**: 🚀 Ready for Implementation  
**Next Review**: Weekly during implementation
