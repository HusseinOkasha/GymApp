package GymApp.service.Intrface;

import GymApp.dto.RegisterDto;
import GymApp.entity.Account;

public interface AuthService {

    void setPassword(String password);
    String register(RegisterDto dto);
}
