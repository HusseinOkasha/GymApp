package GymApp.security.userDetailsService;

import GymApp.entity.Account;
import GymApp.entity.Coach;
import GymApp.entity.Owner;
import GymApp.security.userDetails.OwnerDetails;
import GymApp.service.AccountService;
import GymApp.service.OwnerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.function.Supplier;


@Service

public class JpaOwnerDetailsService implements UserDetailsService {

    @Autowired
    private AccountService accountService;

    @Autowired
    private OwnerService ownerService;


    @Override
    public OwnerDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Supplier<UsernameNotFoundException> s =
                () -> new UsernameNotFoundException("Problem during authentication!");
        Account account = accountService.findByEmailOrPhoneNumber(username, username).orElseThrow(s);
        Owner owner = ownerService.findByAccountId(account.getId()).orElseThrow(s);
        return new OwnerDetails(owner);
    }
}
