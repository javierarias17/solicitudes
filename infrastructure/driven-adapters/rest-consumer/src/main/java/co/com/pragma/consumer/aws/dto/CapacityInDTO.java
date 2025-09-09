package co.com.pragma.consumer.aws.dto;

import java.math.BigDecimal;
import java.util.List;

public record CapacityInDTO(
        Double baseSalary,
        BigDecimal amount,
        Long term,
        Double interestRate,
        List<ActiveLoanDTO> activeLoans
) {}

