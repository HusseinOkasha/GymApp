package GymApp.service.Intrface;

import GymApp.dto.RegisterDto;
import GymApp.entity.User;

public interface RegisterService {

    User register(RegisterDto dto);
}
