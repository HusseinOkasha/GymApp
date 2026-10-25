package GymApp.controller;


import GymApp.dto.LoginDto;
import GymApp.dto.RegisterDto;
import GymApp.dto.SetPasswordDto;
import GymApp.dto.TokenDto;
import GymApp.entity.Account;
import GymApp.security.userDetails.CustomUserDetails;
import GymApp.service.Intrface.AuthService;
import GymApp.service.Intrface.RegisterService;
import GymApp.service.TokenService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    private final TokenService tokenService;
    private final AuthenticationManager authenticationManager;

    public AuthController(
            BCryptPasswordEncoder bCryptPasswordEncoder,
            AuthService authService,
            TokenService tokenService,
            AuthenticationManager authenticationManager

    ) {

        this.authService = authService;
        this.tokenService = tokenService;
        this.authenticationManager = authenticationManager;

    }

    @PostMapping("/login")
    public TokenDto login(@RequestBody LoginDto dto) {
        Authentication authentication =
                authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(dto.email(),
                                                                                                                   dto.password()
        ));
        CustomUserDetails account = (CustomUserDetails) authentication.getPrincipal();
        return new TokenDto(tokenService.generateToken(
                account.getUsername(),
                account.getAuthorities(),
                Map.of()
        ));
    }

    @PostMapping("/register")
    @Validated
    public ResponseEntity register(@Valid @RequestBody RegisterDto dto) {

        return ResponseEntity.ok(authService.register(dto));

    }

    @PostMapping("/set-password")
    @Validated
    public ResponseEntity setPassword(@Valid @RequestBody SetPasswordDto dto) {

        authService.setPassword(dto.password());

        return ResponseEntity.ok().build();
    }

}
