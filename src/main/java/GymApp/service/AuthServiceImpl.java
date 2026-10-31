package GymApp.service;

import GymApp.dto.RegisterDto;
import GymApp.entity.User;
import GymApp.service.Intrface.UserService;
import GymApp.service.Intrface.AuthService;
import GymApp.service.Intrface.CurrentUserService;
import GymApp.service.Intrface.RegisterService;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
public class AuthServiceImpl implements AuthService {
    private final UserService userServiceImpl;
    private final CurrentUserService currentUserService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final RegisterService registerServiceImpl;
    private final TokenService tokenService;

    public AuthServiceImpl(
            UserService userServiceImpl,
            CurrentUserService currentUserService,
            BCryptPasswordEncoder bCryptPasswordEncoder,
            RegisterService registerServiceImpl,
            TokenService tokenService
    ) {
        this.userServiceImpl = userServiceImpl;
        this.currentUserService = currentUserService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.registerServiceImpl = registerServiceImpl;
        this.tokenService = tokenService;
    }

    @Override
    public void setPassword(String password) {
        User user = currentUserService.getCurrentUser();
        // Encrypt the password then save it to the database.
        user.setPassword(bCryptPasswordEncoder.encode(password));
        userServiceImpl.save(user);
    }

    @Transactional
    public String register(RegisterDto dto) {
        User user = registerServiceImpl.register(dto);
        String token = tokenService.generateToken(
                user.getEmail(),
                user.getRoles()
                    .stream()
                    .map((userRole) -> new SimpleGrantedAuthority(userRole.getRole()
                                                                             .getAuthority()))
                    .toList(),
                Map.of()
        );
        // save the account to the database
        return token;
    }


}
