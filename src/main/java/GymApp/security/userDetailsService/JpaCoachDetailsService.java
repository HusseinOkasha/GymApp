package GymApp.security.userDetailsService;

import GymApp.entity.Account;
import GymApp.entity.Coach;
import GymApp.security.userDetails.CoachDetails;
import GymApp.service.AccountService;
import GymApp.service.CoachService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.function.Supplier;


@Service
public class JpaCoachDetailsService implements UserDetailsService {

    @Autowired
    private AccountService accountService;

    @Autowired
    private CoachService coachService;

    @Override
    public CoachDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Supplier<UsernameNotFoundException> s =
                () -> new UsernameNotFoundException("Problem during authentication!");
        Account account = accountService.findByEmailOrPhoneNumber(username, username).orElseThrow(s);
        Coach coach = coachService.findByAccountId(account.getId()).orElseThrow(s);
        return new CoachDetails(coach);

    }
}
