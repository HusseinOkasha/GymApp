package GymApp.security.userDetailsService;

import GymApp.entity.Account;
import GymApp.entity.Client;
import GymApp.security.userDetails.ClientDetails;
import GymApp.service.AccountServiceImpl;
import GymApp.service.ClientServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.function.Supplier;

@Service
public class JpaClientDetailsService implements UserDetailsService {
    @Autowired
    private ClientServiceImpl clientService;

    @Autowired
    private AccountServiceImpl accountService;

    @Override
    public ClientDetails loadUserByUsername(String username) {
        Supplier<UsernameNotFoundException> s =
                () -> new UsernameNotFoundException("Problem during authentication!");
        Account account = accountService.findByEmailOrPhoneNumber(username, username).orElseThrow(s);
        Client client = clientService.findByAccountId(account.getId()).orElseThrow(s);
        return new ClientDetails(client);

    }
}

