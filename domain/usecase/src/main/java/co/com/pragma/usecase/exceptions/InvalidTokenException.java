package co.com.pragma.usecase.exceptions;

import lombok.Getter;

@Getter
public class InvalidTokenException extends BusinessException {
    public InvalidTokenException(String error) {
        super(error);
    }
}
