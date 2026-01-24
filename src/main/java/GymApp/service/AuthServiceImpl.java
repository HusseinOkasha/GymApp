package GymApp.service;

import GymApp.dao.AccountRepository;
import GymApp.dao.RoleRepository;
import GymApp.dto.RegisterDto;
import GymApp.entity.Account;
import GymApp.entity.UserRole;
import GymApp.exception.AccountAlreadyExistsException;
import GymApp.exception.AccountNotFoundException;
import org.apache.coyote.BadRequestException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class AuthServiceImpl implements AuthService {
    private final AccountRepository accountRepository;
    private final RoleRepository roleRepository;
    private final CurrentUserService currentUserService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public AuthServiceImpl(
            AccountRepository accountRepository,
            RoleRepository roleRepository,
            CurrentUserService currentUserService,
            BCryptPasswordEncoder bCryptPasswordEncoder
    ) {
        this.accountRepository = accountRepository;
        this.roleRepository = roleRepository;
        this.currentUserService = currentUserService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    public void setPassword(String password) {
        Account account = currentUserService.getCurrentUser();
        // Encrypt the password then save it to the database.
        account.setPassword(bCryptPasswordEncoder.encode(password));
        accountRepository.save(account);
    }

    public Account register(RegisterDto dto) {

        // Map RegisterDto to Account object
        Account account = new Account.Builder()
                .firstName(dto.firstName())
                .secondName(dto.secondName())
                .thirdName(dto.thirdName())
                .email(dto.email())
                .phoneNumber(dto.phoneNumber())
                .password("RandomValue")
                .build();

        // Check the existence of the user in the system and throw exception if it already exists
        // in the database
        accountRepository.findByEmail(account.getEmail()).ifPresent((acc) -> {
            throw new AccountAlreadyExistsException("User with this email already exists");
        });

        // Assign role to the account.
        UserRole userRole = new UserRole(
                account,
                roleRepository
                        .findRoleByName(dto.role())
                        .orElseThrow(() -> new AccountAlreadyExistsException("Can't find role: " +
                                                                             dto.role()))
        );
        account.setRoles(Set.of(userRole));

        // save the account to the database
        return accountRepository.save(account);
    }


}
