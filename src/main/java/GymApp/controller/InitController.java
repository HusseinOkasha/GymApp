package GymApp.controller;


import GymApp.dao.RoleRepository;
import GymApp.dto.AccountProfileDto;
import GymApp.entity.Account;
import GymApp.entity.Role;
import GymApp.enums.UserRoles;
import GymApp.service.AccountService;
import GymApp.util.entityAndDtoMappers.AccountMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class InitController {
    /*
    * It is intended to be used to initialize the DB, by adding an owner account.
    * I have added this controller to be easy for others to run and test the project.
    * Instead of writing SQL queries directly to the database, you just send a request to this controller,
      and it will handle the creation of owner account, so you can use the application.
    * */
    @Autowired
    private final AccountService accountService;

    @Autowired
    private final RoleRepository roleRepository;
    public InitController(AccountService accountService, RoleRepository roleRepository) {
        this.accountService = accountService;
        this.roleRepository = roleRepository;
    }

    @PostMapping("/init")
    public AccountProfileDto init(){
        /*
        * It creates an owner account for demo purposes.
        * */

        Account.Builder accountBuilder = new Account.Builder();

        // BCrypt password for raw password "123"
        String bCryptPassword = "$2a$12$fdQCjXHktjZczz5hlHg77u8bIXUQdzGQf5k7ulN.cxzhW2vidHzSu";

        // save the account_id in the owner table

        Account account = accountBuilder
                        .firstName("f1")
                        .secondName("s1")
                        .thirdName("t1")
                        .email("e1@gmail.com")
                        .phoneNumber("1")
                        .password(bCryptPassword)
                        .role(UserRoles.ADMIN)
                        .build();

        account = accountService.save(account);

        List<Role> roles = List.of(
            new Role(UserRoles.ADMIN.toString()),
            new Role(UserRoles.CLIENT.toString()),
            new Role(UserRoles.EMPLOYEE.toString())
        );

        this.roleRepository.saveAll(roles);

        // return the account profile dto (without password).
        return AccountMapper.accountEntityToAccountProfileDto(account);
    }

}
