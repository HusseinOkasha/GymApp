package GymApp.controller;


import GymApp.entity.Account;
import GymApp.enums.AccountType;
import GymApp.service.AccountService;
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
    private final AccountService accountService;
    @Autowired
    private final TokenService tokenService;

    public LoginController(AccountService accountService, TokenService tokenService){
        this.accountService = accountService;
        this.tokenService = tokenService;
    }
    @PostMapping("/owner")
    public String ownerLogin(Authentication authentication) {
        return tokenService.generateToken(authentication.getName(), AccountType.OWNER);
    }
    @PostMapping("/client")
    public String clientLogin(Authentication authentication) {
        return tokenService.generateToken(authentication.getName(), AccountType.CLIENT);
    }
    @PostMapping("/coach")
    public String coachLogin(Authentication authentication) {
        return tokenService.generateToken(authentication.getName(), AccountType.COACH);
    }
}
