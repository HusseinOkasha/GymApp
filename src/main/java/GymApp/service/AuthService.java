package GymApp.service;

import GymApp.dto.RegisterDto;
import GymApp.entity.Account;

public interface AuthService {

    void setPassword(String password);
    Account register(RegisterDto dto);
}
