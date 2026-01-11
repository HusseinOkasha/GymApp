package GymApp.controller;


import GymApp.dto.LoginDto;
import GymApp.dto.TokenDto;
import GymApp.security.userDetails.CustomUserDetails;
import GymApp.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/login")
@RestController
public class LoginController {


    private final TokenService tokenService;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public LoginController(TokenService tokenService, AuthenticationManager authenticationManager) {
        this.tokenService = tokenService;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping()
    public TokenDto login(@RequestBody LoginDto dto) {
        Authentication authentication =
                authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                dto.email(),
                dto.password()
        ));
        CustomUserDetails account = (CustomUserDetails) authentication.getPrincipal();
        return new TokenDto(tokenService.generateToken(
                account.getUsername(),
                account.getAuthorities()
        ));
    }

}
