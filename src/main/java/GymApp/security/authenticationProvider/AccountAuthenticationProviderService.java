package GymApp.security.authenticationProvider;


import GymApp.security.userDetails.AccountDetails;
import GymApp.security.userDetailsService.JpaAccountDetailsService;
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
public class AccountAuthenticationProviderService  implements AuthenticationProvider {
    @Lazy
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private JpaAccountDetailsService accountDetailsService;


    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String emailOrPhoneNumber = authentication.getName();
        String password = authentication.getCredentials().toString();

        AccountDetails accountDetails =  accountDetailsService.loadUserByUsername(emailOrPhoneNumber);

        return checkPassword(accountDetails, password, bCryptPasswordEncoder);


    }
    private Authentication checkPassword(AccountDetails accountDetails, String rawPassword, PasswordEncoder encoder) {

        if(encoder.matches(rawPassword, accountDetails.getPassword())){
            return new UsernamePasswordAuthenticationToken(accountDetails.getAccountId(), accountDetails.getPassword(),
                    accountDetails.getAuthorities());
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
