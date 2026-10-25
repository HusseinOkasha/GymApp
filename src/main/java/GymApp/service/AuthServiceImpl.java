package GymApp.service;

import GymApp.dao.RoleRepository;
import GymApp.dto.RegisterDto;
import GymApp.entity.Account;
import GymApp.entity.UserRole;
import GymApp.exception.AccountAlreadyExistsException;
import GymApp.service.Intrface.AccountService;
import GymApp.service.Intrface.AuthService;
import GymApp.service.Intrface.BranchService;
import GymApp.service.Intrface.CurrentUserService;
import GymApp.service.Intrface.RegisterService;
import GymApp.service.Intrface.RoleService;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Set;

@Service
public class AuthServiceImpl implements AuthService {
    private final AccountService accountServiceImpl;
    private final CurrentUserService currentUserService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final RegisterService registerServiceImpl;
    private final TokenService tokenService;

    public AuthServiceImpl(
            AccountService accountServiceImpl,
            CurrentUserService currentUserService,
            BCryptPasswordEncoder bCryptPasswordEncoder,
            RegisterService registerServiceImpl,
            TokenService tokenService
    ) {
        this.accountServiceImpl = accountServiceImpl;
        this.currentUserService = currentUserService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.registerServiceImpl = registerServiceImpl;
        this.tokenService = tokenService;
    }

    @Override
    public void setPassword(String password) {
        Account account = currentUserService.getCurrentUser();
        // Encrypt the password then save it to the database.
        account.setPassword(bCryptPasswordEncoder.encode(password));
        accountServiceImpl.save(account);
    }

    @Transactional
    public String register(RegisterDto dto) {
        Account account = registerServiceImpl.register(dto);
        String token = tokenService.generateToken(
                account.getEmail(),
                account.getRoles()
                       .stream()
                       .map((userRole) -> new SimpleGrantedAuthority(userRole.getRole()
                                                                             .getAuthority()))
                       .toList(),
                Map.of()
        );
        // save the account to the database
        return token;
    }


}
