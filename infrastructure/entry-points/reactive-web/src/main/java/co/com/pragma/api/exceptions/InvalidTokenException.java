package co.com.pragma.api.exceptions;

import co.com.pragma.usecase.exceptions.BusinessException;
import lombok.Getter;

@Getter
public class InvalidTokenException extends BusinessException {
    public InvalidTokenException(String message) {
        super(message);
    }
}
