package co.com.pragma.consumer.app.mapper;

import co.com.pragma.consumer.app.dto.UserDTO;
import co.com.pragma.model.application.User;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel="spring")
public interface UserDTOMapper {
    User toModel(UserDTO userDTO);
    List<User> toModelList(List<UserDTO> userDTO);
}
