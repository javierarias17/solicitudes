package co.com.pragma.consumer.app;

import co.com.pragma.consumer.app.dto.UserDTO;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {
    List<UserDTO> lstUserDTO;
}