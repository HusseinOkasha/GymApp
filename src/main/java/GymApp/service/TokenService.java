package GymApp.service;

import GymApp.entity.Account;
import GymApp.enums.AccountType;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;
import org.springframework.security.core.GrantedAuthority;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TokenService {

    private final JwtEncoder encoder;
    private final JwtDecoder jwtDecoder;
    private final AccountService accountService;
    public TokenService(JwtEncoder encoder, JwtDecoder jwtDecoder, AccountService accountService) {
        this.encoder = encoder;
        this.jwtDecoder = jwtDecoder;
        this.accountService = accountService;
    }

    public String generateToken(String name, AccountType accountType) {
        Instant now = Instant.now();
        String scope = accountType.toString();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plus(1, ChronoUnit.HOURS))
                .subject(name)
                .claim("scope", scope)
                .build();
        return this.encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }
    public Optional<Account> getAccount(String authorizationHeader){
        Jwt decodedToken = getToken(authorizationHeader);
        String userEmail = decodedToken.getClaim("sub");
        Optional<Account> account = accountService.findByEmail(userEmail);
        return account;
    }

    public Jwt getToken(String authorizationHeader){
        String encodedToken = extractToken(authorizationHeader);
        Jwt decodedToken = decodeToken(encodedToken);
        return  decodedToken ;
    }
    public String extractToken(String authorizationHeader){
        // authorizationHeader is like: Bearer token
        String encodedToken = authorizationHeader.substring(7);
        return encodedToken;

    }
    public Jwt decodeToken(String encodedToken){
        Jwt decodedToken = jwtDecoder.decode(encodedToken);
        return decodedToken ;
    }


}
