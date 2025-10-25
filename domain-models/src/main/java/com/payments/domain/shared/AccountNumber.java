package com.payments.domain.shared;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.Value;

/**
 * AccountNumber - Value Object - V2 Enhanced
 * Enhanced for V2 with ISO 20022 account format support
 */
@Embeddable
@Value
@NoArgsConstructor(force = true, access = AccessLevel.PROTECTED)
public class AccountNumber {
  String value;

  public AccountNumber(String value) {
    if (value == null || value.isBlank()) {
      throw new IllegalArgumentException("AccountNumber cannot be null or blank");
    }
    // V2 Enhancement: Validate ISO 20022 account format
    if (!isValidIso20022Format(value)) {
      throw new IllegalArgumentException("Invalid ISO 20022 account format: " + value);
    }
    this.value = value;
  }

  public static AccountNumber of(String value) {
    return new AccountNumber(value);
  }

  // V2 Enhancement: ISO 20022 account format validation
  private boolean isValidIso20022Format(String accountNumber) {
    // ISO 20022 account format: up to 34 characters, alphanumeric
    return accountNumber.length() <= 34 && 
           accountNumber.matches("[A-Za-z0-9]+");
  }

  // V2 Enhancement: Extract bank code from account number
  public String extractBankCode() {
    // Assuming first 6 characters are bank code
    if (value.length() >= 6) {
      return value.substring(0, 6);
    }
    return null;
  }

  // V2 Enhancement: Extract account number without bank code
  public String extractAccountNumber() {
    if (value.length() > 6) {
      return value.substring(6);
    }
    return value;
  }

  // V2 Enhancement: Format for ISO 20022 message
  public String toIso20022Format() {
    return value;
  }

  // V2 Enhancement: Mask for logging (show first 4 and last 4 characters)
  public String toMaskedFormat() {
    if (value.length() <= 8) {
      return "*".repeat(value.length());
    }
    return value.substring(0, 4) + "*".repeat(value.length() - 8) + value.substring(value.length() - 4);
  }
}
