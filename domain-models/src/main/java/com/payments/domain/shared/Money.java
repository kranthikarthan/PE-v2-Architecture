package com.payments.domain.shared;

import jakarta.persistence.Embeddable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.Value;

/**
 * Money - Value Object (Immutable) - V2 Enhanced
 *
 * <p>Encapsulates amount and currency with business rules
 * Enhanced for V2 with ISO 20022 currency support and multi-currency operations
 */
@Embeddable
@Value // Lombok: Immutable, equals/hashCode based on fields
@NoArgsConstructor(force = true, access = AccessLevel.PROTECTED)
public class Money {

  BigDecimal amount;
  Currency currency;

  // Private constructor - use factory methods
  private Money(BigDecimal amount, Currency currency) {
    if (amount == null) {
      throw new IllegalArgumentException("Amount cannot be null");
    }
    if (currency == null) {
      throw new IllegalArgumentException("Currency cannot be null");
    }

    // Store with consistent scale (2 decimal places)
    this.amount = amount.setScale(2, RoundingMode.HALF_UP);
    this.currency = currency;
  }

  // Factory methods
  public static Money of(BigDecimal amount, Currency currency) {
    return new Money(amount, currency);
  }

  public static Money zar(BigDecimal amount) {
    return new Money(amount, Currency.getInstance("ZAR"));
  }

  public static Money usd(BigDecimal amount) {
    return new Money(amount, Currency.getInstance("USD"));
  }

  public static Money eur(BigDecimal amount) {
    return new Money(amount, Currency.getInstance("EUR"));
  }

  public static Money gbp(BigDecimal amount) {
    return new Money(amount, Currency.getInstance("GBP"));
  }

  public static Money zero(Currency currency) {
    return new Money(BigDecimal.ZERO, currency);
  }

  // V2 Enhancement: ISO 20022 currency validation
  public static Money ofIso20022(BigDecimal amount, String currencyCode) {
    try {
      Currency currency = Currency.getInstance(currencyCode);
      return new Money(amount, currency);
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Invalid ISO 20022 currency code: " + currencyCode, e);
    }
  }

  // Business methods
  public Money add(Money other) {
    assertSameCurrency(other);
    return new Money(this.amount.add(other.amount), this.currency);
  }

  public Money subtract(Money other) {
    assertSameCurrency(other);
    return new Money(this.amount.subtract(other.amount), this.currency);
  }

  public Money multiply(BigDecimal multiplier) {
    return new Money(this.amount.multiply(multiplier), this.currency);
  }

  public boolean isGreaterThan(Money other) {
    assertSameCurrency(other);
    return this.amount.compareTo(other.amount) > 0;
  }

  public boolean isLessThan(Money other) {
    assertSameCurrency(other);
    return this.amount.compareTo(other.amount) < 0;
  }

  public boolean isNegativeOrZero() {
    return this.amount.compareTo(BigDecimal.ZERO) <= 0;
  }

  // V2 Enhancement: Currency conversion support
  public Money convertTo(Currency targetCurrency, BigDecimal exchangeRate) {
    if (this.currency.equals(targetCurrency)) {
      return this;
    }
    BigDecimal convertedAmount = this.amount.multiply(exchangeRate);
    return new Money(convertedAmount, targetCurrency);
  }

  // V2 Enhancement: ISO 20022 currency code
  public String getCurrencyCode() {
    return this.currency.getCurrencyCode();
  }

  // Getters for compatibility
  public BigDecimal getAmount() {
    return this.amount;
  }

  public Currency getCurrency() {
    return this.currency;
  }

  private void assertSameCurrency(Money other) {
    if (!this.currency.equals(other.currency)) {
      throw new IllegalArgumentException(
          "Cannot operate on different currencies: " + this.currency + " and " + other.currency);
    }
  }
}
