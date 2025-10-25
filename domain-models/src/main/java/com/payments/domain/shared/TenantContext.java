package com.payments.domain.shared;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Transient;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

/** 
 * TenantContext - Value Object - V2 Enhanced
 * Enhanced for V2 with 3-level hierarchy and RLS support
 */
@Embeddable
@Value
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true, access = AccessLevel.PROTECTED)
public class TenantContext {
  String tenantId;
  @Transient String tenantName;
  String businessUnitId;
  @Transient String businessUnitName;
  
  // V2 Enhancement: Customer level for 3-level hierarchy
  String customerId;
  @Transient String customerName;

  public static TenantContext of(
      String tenantId, String tenantName, String businessUnitId, String businessUnitName) {
    return new TenantContext(tenantId, tenantName, businessUnitId, businessUnitName, null, null);
  }

  // V2 Enhancement: 3-level hierarchy constructor
  public static TenantContext of(
      String tenantId, String tenantName, 
      String businessUnitId, String businessUnitName,
      String customerId, String customerName) {
    return new TenantContext(tenantId, tenantName, businessUnitId, businessUnitName, customerId, customerName);
  }

  public static Builder builder() {
    return new Builder();
  }

  // V2 Enhancement: RLS policy key generation
  public String getRlsPolicyKey() {
    return tenantId + ":" + businessUnitId + ":" + (customerId != null ? customerId : "*");
  }

  // V2 Enhancement: Check if customer level is present
  public boolean hasCustomerLevel() {
    return customerId != null && !customerId.isBlank();
  }

  public static class Builder {
    private String tenantId;
    private String tenantName;
    private String businessUnitId;
    private String businessUnitName;
    private String customerId;
    private String customerName;

    public Builder tenantId(String tenantId) {
      this.tenantId = tenantId;
      return this;
    }

    public Builder tenantName(String tenantName) {
      this.tenantName = tenantName;
      return this;
    }

    public Builder businessUnitId(String businessUnitId) {
      this.businessUnitId = businessUnitId;
      return this;
    }

    public Builder businessUnitName(String businessUnitName) {
      this.businessUnitName = businessUnitName;
      return this;
    }

    // V2 Enhancement: Customer level builder methods
    public Builder customerId(String customerId) {
      this.customerId = customerId;
      return this;
    }

    public Builder customerName(String customerName) {
      this.customerName = customerName;
      return this;
    }

    public TenantContext build() {
      return new TenantContext(tenantId, tenantName, businessUnitId, businessUnitName, customerId, customerName);
    }
  }
}
