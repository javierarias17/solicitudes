package co.com.pragma.api.validator.input;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ApproveOrRejectApplicationInput(
        @NotNull(message = "id is required")
        @Positive(message = "id must be greater than 0")
        Long id,

        @NotNull(message = "statudId is required")
        @Positive(message = "statudId must be greater than 0")
        Long statusId
) {}
