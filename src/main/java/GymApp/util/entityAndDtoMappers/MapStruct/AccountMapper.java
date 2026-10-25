package GymApp.util.entityAndDtoMappers.MapStruct;

import GymApp.dto.RegisterDto;
import GymApp.entity.Account;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface AccountMapper {



    @Mapping(target = "branches", ignore = true)
    Account toEntity(RegisterDto dto);
}