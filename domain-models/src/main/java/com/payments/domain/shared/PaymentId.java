package com.payments.domain.shared;

import jakarta.persistence.Embeddable;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.Value;

/** 
 * PaymentId - Value Object (Entity ID) - V2 Enhanced
 * Enhanced for V2 with UETR correlation support
 */
@Embeddable
@Value
@NoArgsConstructor(force = true, access = AccessLevel.PROTECTED)
public class PaymentId {
  String value;

  public PaymentId(String value) {
    if (value == null || value.isBlank()) {
      throw new IllegalArgumentException("PaymentId cannot be null or blank");
    }
    this.value = value;
  }

  public static PaymentId of(String value) {
    return new PaymentId(value);
  }

  public static PaymentId generate() {
    return new PaymentId("PAY-" + UUID.randomUUID().toString());
  }

  // V2 Enhancement: Generate with timestamp for better traceability
  public static PaymentId generateWithTimestamp() {
    return new PaymentId("PAY-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 8));
  }

  // V2 Enhancement: Generate for specific tenant
  public static PaymentId generateForTenant(String tenantId) {
    return new PaymentId("PAY-" + tenantId + "-" + UUID.randomUUID().toString().substring(0, 8));
  }
}
