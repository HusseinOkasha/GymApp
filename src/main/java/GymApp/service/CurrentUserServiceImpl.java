package GymApp.service;

import GymApp.dao.AccountRepository;
import GymApp.entity.Account;
import GymApp.exception.AccountNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class CurrentUserServiceImpl implements CurrentUserService {
    private final AccountRepository accountRepository;

    public CurrentUserServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }


    public Account getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new RuntimeException("No authenticated user");
        }
        String email = auth.getName();

        return accountRepository
                .findByEmail(email)
                .orElseThrow(() -> new AccountNotFoundException("Can't find account with email: " +
                                                                email));
    }
}
