package co.com.pragma.api.security;

public enum Role {
    ADMINISTRATOR("1"),
    ADVISOR("2"),
    APPLICANT("3");

    private final String code;

    Role(String code) {
        this.code = code;
    }

    public String code() {
        return code;
    }
}
