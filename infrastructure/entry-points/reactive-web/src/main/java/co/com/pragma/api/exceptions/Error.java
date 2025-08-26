package co.com.pragma.api.exceptions;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Error {
    protected static final String DEFAULT_ERROR_MESSAGE =
            "An unexpected error occurred, please contact the administrator";
    protected static final int DEFAULT_ERROR_CODE = 500;

    private int status;
    private String message;

    public Error() {
        this(DEFAULT_ERROR_CODE, DEFAULT_ERROR_MESSAGE);
    }

    public Error(int status, String message) {
        this.status = status;
        this.message = message;
    }
}
