package co.com.pragma.model.capacity.calculation;

import java.math.BigDecimal;
import java.util.List;

public record CapacityIn(
        BigDecimal baseSalary,
        BigDecimal amount,
        Long term,
        Double interestRate,
        List<ActiveLoan> activeLoans
) {}



