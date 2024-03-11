package GymApp.controller;


import GymApp.dto.AccountProfileDto;
import GymApp.dto.ChangePasswordDto;
import GymApp.dto.CreateAccountDto;
import GymApp.entity.Account;
import GymApp.entity.Owner;
import GymApp.enums.AccountType;
import GymApp.exception.AccountCreationFailureException;

import GymApp.exception.AccountNotFoundException;
import GymApp.exception.UpdateAccountFailureException;
import GymApp.security.EncryptionService;
import GymApp.service.AccountService;
import GymApp.service.OwnerService;
import GymApp.service.TokenService;
import GymApp.util.AccountEntityAndDtoConverters;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api")
public class OwnerAccountManagerController {
    @Autowired
    private final AccountService accountService;
    @Autowired
    private final OwnerService ownerService;
    @Autowired
    private final EncryptionService encryptionService;


    public OwnerAccountManagerController(AccountService accountService, OwnerService ownerService,
                                         EncryptionService encryptionService) {
        this.accountService = accountService;
        this.ownerService = ownerService;
        this.encryptionService = encryptionService;
    }


    // create new owner account.
    @PostMapping("/owner-account-manager")
    @PreAuthorize("hasAuthority('SCOPE_OWNER')")
    @Validated
    public AccountProfileDto createOwnerAccount(@RequestBody @Valid CreateAccountDto createAccountDto)
            throws Exception {

        // create entity account from the account dto
        Account newAccount = AccountEntityAndDtoConverters
                .convertCreateAccountDtoToAccountEntity(createAccountDto);

        // encrypt the password
        String password = newAccount.getPassword();
        String EncryptedPassword = encryptionService.encryptString(password);
        newAccount.setPassword(EncryptedPassword);

        Account dbAccount;
        dbAccount = accountService.save(newAccount)
                .orElseThrow(() -> new AccountCreationFailureException("failed to create the account in the database"));

        // save the account_id in the owner table
        ownerService.save(new Owner(dbAccount))
                .orElseThrow(() -> new AccountCreationFailureException("failed to create the account in the database"));

        // return the account profile dto (without password).
        return AccountEntityAndDtoConverters.convertAccountEntityToAccountProfileDto(dbAccount);
    }

}
