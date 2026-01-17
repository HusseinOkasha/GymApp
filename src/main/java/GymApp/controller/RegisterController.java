package GymApp.controller;

import GymApp.dto.RegisterDto;
import GymApp.entity.Account;
import GymApp.service.AccountService;
import GymApp.service.RegisterService;
import GymApp.service.TokenService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/register")
public class RegisterController {

    private final RegisterService registerService;
    private final TokenService tokenService;

    @Autowired
    public RegisterController(RegisterService registerService, TokenService tokenService) {
        this.registerService = registerService;
        this.tokenService = tokenService;
    }

    @PostMapping()
    @Validated
    public ResponseEntity register(@Valid @RequestBody RegisterDto dto) {
        // Create account
        Account dbAccount = registerService.register(dto);

        // Generate token to be embedded in the invitation link as a path variable
        String token = tokenService.generateToken(
                dbAccount.getEmail(),
                dbAccount
                        .getRoles()
                        .stream()
                        .map((userRole) -> new SimpleGrantedAuthority(userRole
                                                                              .getRole()
                                                                              .getAuthority()))
                        .toList()
        );
        return ResponseEntity.ok(token);

    }
}
