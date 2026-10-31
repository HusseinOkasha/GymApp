package GymApp.security.userDetailsService;

import GymApp.entity.User;
import GymApp.security.userDetails.CustomUserDetails;
import GymApp.service.Intrface.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.function.Supplier;

@Service
public class JpaAccountDetailsService implements UserDetailsService {
    @Autowired
    private UserService userService;

    @Override
    public CustomUserDetails loadUserByUsername(String username) {
        Supplier<UsernameNotFoundException> s =
                () -> new UsernameNotFoundException("Problem during authentication!");
        User user = userService.findByEmailOrPhoneNumber(username, username).orElseThrow(s);
        return new CustomUserDetails(user);

    }
}

