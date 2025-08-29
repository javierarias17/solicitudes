package co.com.pragma.usecase.exceptions;

import lombok.Getter;

@Getter
public class InvalidCredentialsException extends BusinessException {
    public InvalidCredentialsException() {
        super("Invalid credentials");
    }

    public InvalidCredentialsException(String error) {
        super(error);
    }
}
