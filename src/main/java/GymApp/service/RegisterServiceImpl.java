package GymApp.service;

import GymApp.dto.RegisterDto;
import GymApp.entity.User;
import GymApp.entity.UserRole;
import GymApp.service.Intrface.UserService;
import GymApp.service.Intrface.RegisterService;
import GymApp.service.Intrface.RoleService;
import GymApp.util.entityAndDtoMappers.MapStruct.UserMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;


import java.util.Set;

@Service
public class RegisterServiceImpl implements RegisterService {
    private final RoleService roleServiceImpl;
    private final UserService userServiceImpl;
    private final UserMapper userMapper;
    private final BCryptPasswordEncoder encoder;
    public RegisterServiceImpl(RoleService roleServiceImpl, UserService userServiceImpl,
                               UserMapper userMapper, BCryptPasswordEncoder encoder
    ) {
        this.roleServiceImpl = roleServiceImpl;
        this.userServiceImpl = userServiceImpl;
        this.userMapper = userMapper;
        this.encoder = encoder;
    }

    public User register(RegisterDto dto){
        // Map RegisterDto to Account object
        User user = userMapper.toEntity(dto);

        // Assign Branches to the account
        //        List<Branch> branches = branchService.findAllByIds(dto.branches());
        //        account
        //                .getBranches()
        //                .addAll(branches
        //                                .stream()
        //                                .map(branch -> new UserBranch.Builder()
        //                                        .account(account)
        //                                        .branch(branch)
        //                                        .id(new UserBranch.Id(account.getId(), branch.getId()))
        //                                        .build())
        //                                .toList());

        // Assign role to the account.
        UserRole userRole = new UserRole(
                user,
                roleServiceImpl.findByName(dto.role())
        );
        user.setRoles(Set.of(userRole));
        // Default password
        user.setPassword(encoder.encode("123"));
        // save the account to the database
        return userServiceImpl.save(user);
    }
}
