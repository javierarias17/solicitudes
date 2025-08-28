package co.com.pragma.usecase.exceptions;

import lombok.Getter;

import java.util.Map;

@Getter
public abstract class BusinessException extends RuntimeException { // <- abstract
    private final Map<String, String> errors;

    protected BusinessException(String message) {
        super(message);
        this.errors = Map.of();
    }

    protected BusinessException(String message, Map<String, String> errors) {
        super(message);
        this.errors = errors;
    }
}
