package co.com.pragma.api.mapper;

import co.com.pragma.api.dto.ApplicationDTO;
import co.com.pragma.model.application.Application;
import org.mapstruct.Mapper;

@Mapper(componentModel="spring")
public interface ApplicationDTOMapper {
    Application toModel(ApplicationDTO applicationDTO);
    ApplicationDTO toResponse(Application application);
}
