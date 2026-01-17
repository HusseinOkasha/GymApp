package GymApp.service;

import GymApp.dao.RoleRepository;
import GymApp.dto.RegisterDto;
import GymApp.entity.Account;
import GymApp.entity.UserRole;
import GymApp.exception.BadRequestException;
import org.springframework.stereotype.Service;

import java.util.Set;
@Service
public class RegisterService {
    private final AccountService accountService;
    private final RoleRepository roleRepository;

    public RegisterService(AccountService accountService, RoleRepository roleRepository) {
        this.accountService = accountService;
        this.roleRepository = roleRepository;
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
        accountService.findByEmail(account.getEmail()).ifPresent((acc) -> {
            throw new BadRequestException("User with this email already exists");
        });

        // Assign role to the account.
        UserRole userRole = new UserRole(
                account,
                roleRepository
                        .findRoleByName(dto.role())
                        .orElseThrow(() -> new BadRequestException("Can't find role: " +
                                                                   dto.role()))
        );
        account.setRoles(Set.of(userRole));

        // save the account to the database
        return accountService.save(account);
    }
}
