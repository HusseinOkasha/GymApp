package GymApp.controller;


import GymApp.dto.TokenDto;
import GymApp.enums.UserRoles;
import GymApp.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/login")
@RestController
public class LoginController {

    @Autowired
    private final TokenService tokenService;

    public LoginController(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @PostMapping()
    public TokenDto login(Authentication authentication) {
        return new TokenDto(tokenService.generateToken(authentication.getName(), UserRoles.ADMIN));
    }

}
