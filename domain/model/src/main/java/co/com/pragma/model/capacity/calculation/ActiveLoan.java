package co.com.pragma.model.capacity.calculation;

import java.math.BigDecimal;

public record ActiveLoan(
        BigDecimal amount,
        Long term,
        Double interestRate
) {}