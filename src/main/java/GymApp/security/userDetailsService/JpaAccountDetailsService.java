package GymApp.security.userDetailsService;

import GymApp.entity.Account;
import GymApp.security.userDetails.CustomUserDetails;
import GymApp.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.function.Supplier;

@Service
public class JpaAccountDetailsService implements UserDetailsService {
    @Autowired
    private AccountService accountService;

    @Override
    public CustomUserDetails loadUserByUsername(String username) {
        Supplier<UsernameNotFoundException> s =
                () -> new UsernameNotFoundException("Problem during authentication!");
        Account account = accountService.findByEmailOrPhoneNumber(username, username).orElseThrow(s);
        return new CustomUserDetails(account);

    }
}

