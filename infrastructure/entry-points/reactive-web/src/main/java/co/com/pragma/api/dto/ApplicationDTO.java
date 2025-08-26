package co.com.pragma.api.dto;

import co.com.pragma.api.common.ValidationPatterns;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record ApplicationDTO(Long id,
     @NotNull(message = "Amount is required")
     @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
     BigDecimal amount,
     @NotNull(message = "Term is required")
     @Min(value = 1, message = "Term must be at least 1")
     Long term,
     @NotBlank(message = "Email is required and cannot be empty")
     @Pattern(regexp = ValidationPatterns.EMAIL_PATTERN, message = "Invalid email format")
     String email,
     Long statusId,
     @NotNull(message = "Loan type is required")
     Long loanTypeId,
     @NotBlank(message = "Identity document is required and cannot be empty")
     @Pattern(regexp = ValidationPatterns.IDENTITY_DOCUMENT_PATTERN, message = "Identity document must be numeric")
     String identityDocument
) {
}