package co.com.pragma.consumer.aws.dto;

import java.math.BigDecimal;

public record ActiveLoanDTO(
        BigDecimal amount,
        Double interestRate,
        Long term
) {}

