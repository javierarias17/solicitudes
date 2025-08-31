package co.com.pragma.api.mapper;

import co.com.pragma.api.dto.ApplicationSummaryDTO;
import co.com.pragma.model.application.ApplicationSummary;
import org.mapstruct.Mapper;

@Mapper(componentModel="spring")
public interface ApplicationSummaryDTOMapper {
    ApplicationSummaryDTO toResponse(ApplicationSummary applicationSummary);
}
