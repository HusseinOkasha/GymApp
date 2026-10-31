package GymApp.service.Intrface;

import GymApp.dto.RegisterDto;

public interface AuthService {

    void setPassword(String password);
    String register(RegisterDto dto);
}
