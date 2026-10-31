package GymApp.util.entityAndDtoMappers.MapStruct;

import GymApp.dto.RegisterDto;
import GymApp.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;


@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    @Mapping(target = "branches", ignore = true)
    User toEntity(RegisterDto dto);
}