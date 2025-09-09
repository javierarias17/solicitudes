package co.com.pragma.consumer.aws.mapper;

import co.com.pragma.consumer.aws.dto.CapacityInDTO;
import co.com.pragma.consumer.aws.dto.CapacityOutDTO;
import co.com.pragma.model.capacity.calculation.CapacityIn;
import co.com.pragma.model.capacity.calculation.CapacityOut;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CapacidadMapper {

    CapacityInDTO toDTO(CapacityIn capacityIn);

    CapacityIn toModel(CapacityInDTO capacityInDTO);

    CapacityOut toModel(CapacityOutDTO capacityOutDTO);

    CapacityOutDTO toDTO(CapacityOut capacityOut);
}