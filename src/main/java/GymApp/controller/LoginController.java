package GymApp.controller;


import GymApp.dto.TokenDto;
import GymApp.enums.AccountType;
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

    @PostMapping("/owner")
    public TokenDto ownerLogin(Authentication authentication) {
        return new TokenDto(tokenService.generateToken(authentication.getName(), AccountType.OWNER));
    }

    @PostMapping("/client")
    public TokenDto clientLogin(Authentication authentication) {
        return new TokenDto(tokenService.generateToken(authentication.getName(), AccountType.CLIENT));
    }

    @PostMapping("/coach")
    public TokenDto coachLogin(Authentication authentication) {
        return new TokenDto(tokenService.generateToken(authentication.getName(), AccountType.COACH));
    }
}
