package GymApp.security.authenticationProvider;


import GymApp.security.userDetails.OwnerDetails;
import GymApp.security.userDetailsService.JpaOwnerDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class OwnerAuthenticationProviderService  implements AuthenticationProvider {
    @Lazy
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private JpaOwnerDetailsService ownerDetailsService;


    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String emailOrPhoneNumber = authentication.getName();
        String password = authentication.getCredentials().toString();

        OwnerDetails ownerDetails =  ownerDetailsService.loadUserByUsername(emailOrPhoneNumber);

        return checkPassword(ownerDetails, password, bCryptPasswordEncoder);


    }
    private Authentication checkPassword(OwnerDetails ownerDetails, String rawPassword, PasswordEncoder encoder) {

        if(encoder.matches(rawPassword, ownerDetails.getPassword())){
            return new UsernamePasswordAuthenticationToken(ownerDetails.getAccountId(), ownerDetails.getPassword(),
                    ownerDetails.getAuthorities());
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
