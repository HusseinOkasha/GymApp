package GymApp.controller;


import GymApp.dto.SetPasswordDto;
import GymApp.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final AuthService authService;
    public AuthController(BCryptPasswordEncoder bCryptPasswordEncoder, AuthService authService){
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.authService = authService;
    }

    @PostMapping("/set-password")
    @Validated
    public ResponseEntity setPassword(@Valid @RequestBody SetPasswordDto dto){

        authService.setPassword(dto.password());

        return ResponseEntity.ok().build();
    }

}
