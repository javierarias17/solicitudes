package co.com.pragma.usecase.exceptions;

import lombok.Getter;

@Getter
public class TechnicalException extends RuntimeException {
    public TechnicalException(String error) {
        super(error);
    }
}
