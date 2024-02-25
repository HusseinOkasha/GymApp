package GymApp.security.authenticationProvider;


import GymApp.security.userDetails.ClientDetails;
import GymApp.security.userDetailsService.JpaClientDetailsService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class ClientAuthenticationProviderService implements AuthenticationProvider {
    @Lazy
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private JpaClientDetailsService clientDetailsService;


    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String emailOrPhoneNumber = authentication.getName();
        String password = authentication.getCredentials().toString();

        ClientDetails clientDetails =  clientDetailsService.loadUserByUsername(emailOrPhoneNumber);

        return checkPassword(clientDetails, password, bCryptPasswordEncoder);
    }
    private Authentication checkPassword(ClientDetails clientDetails, String rawPassword, PasswordEncoder encoder) {

        if(encoder.matches(rawPassword, clientDetails.getPassword())){
            return new UsernamePasswordAuthenticationToken(clientDetails.getAccountId(), clientDetails.getPassword(),
                    clientDetails.getAuthorities());
        }
        else{
            throw new BadCredentialsException("Bad Credentials");
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
