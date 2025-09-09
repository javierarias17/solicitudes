package co.com.pragma.usecase.exceptions;

import lombok.Getter;

import java.util.Map;

@Getter
public class ValidationException extends BusinessException {
    public ValidationException(Map<String, String> errors) {
        super("Validation errors", errors);
    }
    public ValidationException(String message) {
        super(message);
    }
}
