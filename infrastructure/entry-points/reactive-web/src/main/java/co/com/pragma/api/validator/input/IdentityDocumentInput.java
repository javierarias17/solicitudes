package co.com.pragma.api.validator.input;

import co.com.pragma.api.common.ValidationPatterns;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record IdentityDocumentInput(
    @NotBlank(message = "Identity document cannot be blank")
    @Pattern(regexp = ValidationPatterns.IDENTITY_DOCUMENT_PATTERN, message = "Identity document must be numeric")
    String identityDocument){
}
