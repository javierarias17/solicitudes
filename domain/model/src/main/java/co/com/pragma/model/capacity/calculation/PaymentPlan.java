package co.com.pragma.model.capacity.calculation;

import java.math.BigDecimal;

public record PaymentPlan(
        Long month,
        BigDecimal installment,//Cuota mensual
        BigDecimal principalPayment,//Abono capital
        Double interestRate,
        BigDecimal remainingBalance//Saldo restante
) {}
