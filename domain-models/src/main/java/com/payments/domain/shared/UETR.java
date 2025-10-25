package com.payments.domain.shared;

import jakarta.persistence.Embeddable;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.Value;

/**
 * UETR (Unique End-to-End Transaction Reference) - V2 New
 * 
 * <p>ISO 20022 UETR for end-to-end transaction correlation
 * Format: 32-character UUID (without hyphens)
 */
@Embeddable
@Value
@NoArgsConstructor(force = true, access = AccessLevel.PROTECTED)
public class UETR {
  String value;

  public UETR(String value) {
    if (value == null || value.isBlank()) {
      throw new IllegalArgumentException("UETR cannot be null or blank");
    }
    if (value.length() != 32) {
      throw new IllegalArgumentException("UETR must be exactly 32 characters");
    }
    // Validate it's a valid UUID format (without hyphens)
    try {
      String formattedUuid = value.substring(0, 8) + "-" + 
                            value.substring(8, 12) + "-" + 
                            value.substring(12, 16) + "-" + 
                            value.substring(16, 20) + "-" + 
                            value.substring(20, 32);
      UUID.fromString(formattedUuid);
    } catch (Exception e) {
      throw new IllegalArgumentException("UETR must be a valid UUID format", e);
    }
    this.value = value;
  }

  public static UETR of(String value) {
    return new UETR(value);
  }

  public static UETR generate() {
    UUID uuid = UUID.randomUUID();
    return new UETR(uuid.toString().replace("-", ""));
  }

  // V2 Enhancement: Generate with tenant prefix for better traceability
  public static UETR generateForTenant(String tenantId) {
    UUID uuid = UUID.randomUUID();
    String prefix = tenantId.substring(0, Math.min(4, tenantId.length())).toUpperCase();
    return new UETR(prefix + uuid.toString().replace("-", "").substring(4));
  }

  // V2 Enhancement: Generate with payment type prefix
  public static UETR generateForPaymentType(String paymentType) {
    UUID uuid = UUID.randomUUID();
    String prefix = paymentType.substring(0, Math.min(4, paymentType.length())).toUpperCase();
    return new UETR(prefix + uuid.toString().replace("-", "").substring(4));
  }

  // V2 Enhancement: Format for ISO 20022 message
  public String toIso20022Format() {
    return value.substring(0, 8) + "-" + 
           value.substring(8, 12) + "-" + 
           value.substring(12, 16) + "-" + 
           value.substring(16, 20) + "-" + 
           value.substring(20, 32);
  }

  // V2 Enhancement: Extract tenant from UETR (if generated with tenant prefix)
  public String extractTenantPrefix() {
    if (value.length() >= 4) {
      String prefix = value.substring(0, 4);
      // Check if it's a valid tenant prefix (letters only)
      if (prefix.matches("[A-Z]{4}")) {
        return prefix;
      }
    }
    return null;
  }
}
