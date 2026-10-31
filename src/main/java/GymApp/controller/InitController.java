package GymApp.controller;


import GymApp.dao.RoleRepository;
import GymApp.dto.AccountProfileDto;
import GymApp.entity.User;
import GymApp.entity.Role;
import GymApp.entity.UserRole;
import GymApp.enums.Roles;
import GymApp.service.Intrface.UserService;
import GymApp.util.entityAndDtoMappers.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api")
public class InitController {
    /*
    * It is intended to be used to initialize the DB, by adding an owner account.
    * I have added this controller to be easy for others to run and test the project.
    * Instead of writing SQL queries directly to the database, you just send a request to this
    * controller,
      and it will handle the creation of owner account, so you can use the application.
    * */
    @Autowired
    private final UserService userService;

    @Autowired
    private final RoleRepository roleRepository;

    public InitController(UserService userService, RoleRepository roleRepository) {
        this.userService = userService;
        this.roleRepository = roleRepository;
    }

    @PostMapping("/init")
    public AccountProfileDto init() {
        /*
         * It creates an owner account for demo purposes.
         * */

        List<Role> roles = List.of(
                new Role(Roles.ADMIN),
                new Role(Roles.CLIENT),
                new Role(Roles.EMPLOYEE)
        );

        this.roleRepository.saveAll(roles);

        User.Builder accountBuilder = new User.Builder();

        // BCrypt password for raw password "123"
        String bCryptPassword = "$2a$12$fdQCjXHktjZczz5hlHg77u8bIXUQdzGQf5k7ulN.cxzhW2vidHzSu";

        // save the account_id in the owner table

        User user = accountBuilder
                .firstName("f1")
                .secondName("s1")
                .thirdName("t1")
                .email("e1@gmail.com")
                .phoneNumber("1")
                .password(bCryptPassword).build();
        UserRole userRoleLink = new UserRole(user, roleRepository.findByName(Roles.ADMIN).orElseThrow());
        user.setRoles(Set.of(userRoleLink));
        user = userService.save(user);

        // return the account profile dto (without password).
        return UserMapper.accountEntityToAccountProfileDto(user);
    }

}
