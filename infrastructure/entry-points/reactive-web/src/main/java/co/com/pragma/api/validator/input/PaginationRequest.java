package co.com.pragma.api.validator.input;

import jakarta.validation.constraints.*;

public record PaginationRequest(
        @NotNull(message = "Page is required")
        @PositiveOrZero(message = "Page must be greater than or equal to 0")
        Integer page,

        @NotNull(message = "Size is required")
        @Positive(message = "Size must be greater than 0")
        Integer size
) {}
