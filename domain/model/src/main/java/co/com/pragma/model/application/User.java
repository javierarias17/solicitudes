package co.com.pragma.model.application;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder(toBuilder = true)
public class User {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private LocalDate birthDate;
    private String address;
    private String identityDocument;
    private String phone;
    private Long roleId;
    private BigDecimal baseSalary;
    private String password;
}
