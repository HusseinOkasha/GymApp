package GymApp.security.authenticationProvider;


import GymApp.security.userDetails.CoachDetails;
import GymApp.security.userDetailsService.JpaCoachDetailsService;
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
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Service;

@Service
public class CoachAuthenticationProviderService implements AuthenticationProvider {
    @Lazy
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private JpaCoachDetailsService coachDetailsService;


    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String emailOrPhoneNumber = authentication.getName();
        String password = authentication.getCredentials().toString();

        CoachDetails coachDetails =  coachDetailsService.loadUserByUsername(emailOrPhoneNumber);

        return checkPassword(coachDetails, password, bCryptPasswordEncoder);


    }
    private Authentication checkPassword(CoachDetails coachDetails, String rawPassword, PasswordEncoder encoder) {

        if(encoder.matches(rawPassword, coachDetails.getPassword())){
            return new UsernamePasswordAuthenticationToken(coachDetails.getAccountId(), coachDetails.getPassword(),
                    coachDetails.getAuthorities());
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
