package co.com.pragma.api.dto;

import java.math.BigDecimal;

public record ApplicationSummaryDTO(
        Long id,
        BigDecimal amount,
        Long term,
        String email,
        String fullName,
        String loanType,
        Double interestRate,
        String status,
        BigDecimal baseSalary,
        BigDecimal totalMonthlyDebt
) {}
