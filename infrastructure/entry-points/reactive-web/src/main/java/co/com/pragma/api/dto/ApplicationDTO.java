package co.com.pragma.api.dto;

import java.math.BigDecimal;

public record ApplicationDTO(Long id,
    BigDecimal amount,
    Long term,
    String email,
    Long statusId,
    Long loanTypeId,
    String identityDocument) {
}
