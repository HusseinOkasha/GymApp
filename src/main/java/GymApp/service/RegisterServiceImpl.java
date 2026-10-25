package GymApp.service;

import GymApp.dto.RegisterDto;
import GymApp.entity.Account;
import GymApp.entity.UserRole;
import GymApp.service.Intrface.AccountService;
import GymApp.service.Intrface.RegisterService;
import GymApp.service.Intrface.RoleService;
import GymApp.util.entityAndDtoMappers.MapStruct.AccountMapper;


import java.util.Set;

public class RegisterServiceImpl implements RegisterService {
    private final RoleService roleServiceImpl;
    private final AccountService accountServiceImpl;
    private final AccountMapper accountMapper;
    public RegisterServiceImpl(RoleService roleServiceImpl, AccountService accountServiceImpl,
                               AccountMapper accountMapper
    ) {
        this.roleServiceImpl = roleServiceImpl;
        this.accountServiceImpl = accountServiceImpl;
        this.accountMapper = accountMapper;
    }

    public Account register(RegisterDto dto){
        // Map RegisterDto to Account object
        Account account = accountMapper.toEntity(dto);

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
                account,
                roleServiceImpl.findByName(dto.role().toString())
        );
        account.setRoles(Set.of(userRole));

        // save the account to the database
        return accountServiceImpl.save(account);
    }
}
