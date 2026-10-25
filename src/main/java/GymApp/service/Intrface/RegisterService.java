package GymApp.service.Intrface;

import GymApp.dto.RegisterDto;
import GymApp.entity.Account;

public interface RegisterService {

    Account register(RegisterDto dto);
}
