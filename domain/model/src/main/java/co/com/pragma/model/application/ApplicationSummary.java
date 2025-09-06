package co.com.pragma.model.application;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class ApplicationSummary {
    private Long id;
    private BigDecimal amount;
    private Long term;
    private String email;
    private String fullName;
    private String loanType;
    private Double interestRate;
    private String status;
    private BigDecimal baseSalary;
    private BigDecimal totalMonthlyDebt;
    private String identityDocument;
}