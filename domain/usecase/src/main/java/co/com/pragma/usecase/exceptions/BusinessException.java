package co.com.pragma.usecase.exceptions;

import java.util.Map;

public class BusinessException extends RuntimeException {
    private final Map<String, String> errors;

    public BusinessException(Map<String, String> errors) {
        super("Business validation error");
        this.errors = errors;
    }

    public Map<String, String> getErrors() {
        return errors;
    }
}
