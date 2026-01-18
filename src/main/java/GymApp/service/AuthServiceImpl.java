package GymApp.service;

import GymApp.dao.AccountRepository;
import GymApp.entity.Account;
import GymApp.exception.AccountNotFoundException;
import org.apache.coyote.BadRequestException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {
    private final AccountRepository accountRepository;
    private final CurrentUserService currentUserService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public AuthServiceImpl(
            AccountRepository accountRepository, CurrentUserService currentUserService,
            BCryptPasswordEncoder bCryptPasswordEncoder
    ) {
        this.accountRepository = accountRepository;
        this.currentUserService = currentUserService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    public void setPassword(String password) {
//        // Extract the logged-in user's email from the authentication object
//        String email = authentication.getName();
//
//        // Fetch the account from the database.
//        Account account = accountRepository
//                .findByEmail(email)
//                .orElseThrow(()-> new AccountNotFoundException("Can't find Account with email: " + email));

        Account account = currentUserService.getCurrentUser();
        // Encrypt the password then save it to the database.
        account.setPassword(bCryptPasswordEncoder.encode(password));
        accountRepository.save(account);
    }


}
