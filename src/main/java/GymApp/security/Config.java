package GymApp.security;


import GymApp.security.authenticationProvider.CoachAuthenticationProviderService;
import GymApp.security.authenticationProvider.OwnerAuthenticationProviderService;
import GymApp.security.authenticationProvider.ClientAuthenticationProviderService;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class Config {

    private final RsaKeyProperties rsaKeys;

    @Autowired
    private final OwnerAuthenticationProviderService ownerAuthenticationProviderService;

    @Autowired
    private final CoachAuthenticationProviderService coachAuthenticationProviderService;
    @Autowired
    private final ClientAuthenticationProviderService clientAuthenticationProviderService;

    public Config(RsaKeyProperties rsaKeys, OwnerAuthenticationProviderService ownerAuthenticationProviderService,
                  CoachAuthenticationProviderService coachAuthenticationProviderService,
                  ClientAuthenticationProviderService clientAuthenticationProviderService) {
        this.rsaKeys = rsaKeys;
        this.ownerAuthenticationProviderService = ownerAuthenticationProviderService;
        this.coachAuthenticationProviderService = coachAuthenticationProviderService;
        this.clientAuthenticationProviderService = clientAuthenticationProviderService;
    }


    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    };
    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withPublicKey(rsaKeys.publicKey()).build();
    }
    @Bean
    JwtEncoder jwtEncoder() {
        JWK jwk = new RSAKey.Builder(rsaKeys.publicKey()).privateKey(rsaKeys.privateKey()).build();
        JWKSource<SecurityContext> jwks = new ImmutableJWKSet<>(new JWKSet(jwk));
        return new NimbusJwtEncoder(jwks);
    }

    @Order(Ordered.HIGHEST_PRECEDENCE)
    @Bean
    SecurityFilterChain ownerLoginFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/api/login/owner")
                .csrf((a)->a.disable())
                .authenticationProvider(ownerAuthenticationProviderService)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .httpBasic(withDefaults());
        return http.build();
    }


    @Order(Ordered.HIGHEST_PRECEDENCE)
    @Bean
    SecurityFilterChain coachLoginFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/api/login/coach")
                .csrf((a)->a.disable())
                .authenticationProvider(coachAuthenticationProviderService)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .httpBasic(withDefaults());
        return http.build();
    }
    @Order(Ordered.HIGHEST_PRECEDENCE)
    @Bean
    SecurityFilterChain clientLoginFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/api/login/client")
                .csrf((a)->a.disable())
                .authenticationProvider(clientAuthenticationProviderService)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .httpBasic(withDefaults());
        return http.build();
    }


    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf((a)->a.disable())
                .authorizeHttpRequests((authz) -> authz
                        .requestMatchers(HttpMethod.POST, "/api/accounts").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer((oauth2)-> oauth2.jwt(withDefaults()))
                .authenticationProvider(ownerAuthenticationProviderService)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        return http.build();
    }

}
