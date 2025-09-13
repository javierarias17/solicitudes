package co.com.pragma.model.capacity.calculation;

import java.math.BigDecimal;

public record PaymentPlan(
        Long month,
        BigDecimal installment,
        BigDecimal principalPayment,
        Double interestRate,
        BigDecimal remainingBalance
) {}
