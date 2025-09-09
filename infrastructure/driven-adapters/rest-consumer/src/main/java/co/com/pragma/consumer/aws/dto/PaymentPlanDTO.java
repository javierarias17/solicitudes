package co.com.pragma.consumer.aws.dto;

import java.math.BigDecimal;

public record PaymentPlanDTO(
        Long month,
        BigDecimal installment,//Cuota mensual
        BigDecimal principalPayment,//Abono capital
        Double interestRate,
        BigDecimal remainingBalance//Saldo restante
) {}
