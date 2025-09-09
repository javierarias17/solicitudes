package co.com.pragma.consumer.app.dto;

import java.time.LocalDate;

public record UserDTO(Long id,
       String firstName,
       String lastName,
       String email,
       LocalDate birthDate,
       String address,
       String identityDocument,
       String phone,
       Long roleId,
       Double baseSalary,
       String password
) {
}
