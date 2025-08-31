package co.com.pragma.api.dto;

import co.com.pragma.api.common.validations.ValidationPatterns;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record ApplicationDTO(Long id,
     @NotNull(message = "Amount is required")
     @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
     BigDecimal amount,
     @NotNull(message = "Term is required")
     @Min(value = 1, message = "Term must be at least 1")
     Long term,

     @JsonProperty(access = JsonProperty.Access.READ_ONLY)
     String email,
     Long statusId,
     @NotNull(message = "Loan type is required")
     Long loanTypeId,

     @JsonProperty(access = JsonProperty.Access.READ_ONLY)
     String identityDocument
) {
}