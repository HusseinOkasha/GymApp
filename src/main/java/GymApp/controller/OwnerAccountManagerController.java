package GymApp.controller;


import GymApp.entity.Account;
import GymApp.entity.Owner;
import GymApp.exception.AccountNotFoundException;
import GymApp.service.AccountService;
import GymApp.service.OwnerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api")
public class OwnerAccountManagerController {
    @Autowired
    private final AccountService accountService;
    @Autowired
    private final OwnerService ownerService;

    public OwnerAccountManagerController(AccountService accountService, OwnerService ownerService) {
        this.accountService = accountService;
        this.ownerService = ownerService;
    }

    // get list of all owner accounts
    @GetMapping("/owner-account-manager/all")
    @PreAuthorize("hasAuthority('SCOPE_OWNER')")
    public List<Account> getAllOwners(){
        List<Owner> owners = ownerService.findAll();
        return owners.stream().map(Owner::getAccount).toList();
    }

    @GetMapping("/owner-account-manager")
    @PreAuthorize("hasAuthority('SCOPE_OWNER')")
    public Account getOwner(Authentication authentication) throws AccountNotFoundException {
        String name  = authentication.getName();
        return ownerService
                .findByEmailOrPhoneNumber(name, name)
                .orElseThrow(()-> new AccountNotFoundException("Account not found")).getAccount();
    }

}
