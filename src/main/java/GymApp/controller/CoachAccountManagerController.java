package GymApp.controller;

import GymApp.dto.AccountProfileDto;
import GymApp.dto.CreateAccountDto;
import GymApp.entity.Account;

import GymApp.entity.Coach;

import GymApp.exception.AccountCreationFailureException;
import GymApp.security.EncryptionService;
import GymApp.service.*;
import GymApp.util.AccountEntityAndDtoConverters;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class CoachAccountManagerController {
    @Autowired
    private final CoachService coachService;
    @Autowired
    private final EncryptionService encryptionService;
    @Autowired
    private final TokenService tokenService;

    public CoachAccountManagerController(CoachService coachService, EncryptionService encryptionService,
                                         TokenService tokenService) {
        this.coachService = coachService;
        this.encryptionService = encryptionService;
        this.tokenService = tokenService;
    }

    @PostMapping("/coach-account-manager")
    @Validated
    @PreAuthorize("hasAuthority('SCOPE_OWNER')")
    public AccountProfileDto createNewCoachAccount(@RequestBody @Valid CreateAccountDto createAccountDto)
            throws Exception {

        // create entity account from the account dto
        Account newAccount = AccountEntityAndDtoConverters
                .convertCreateAndUpdateAccountDtoToAccountEntity(createAccountDto);

        // encrypt the password
        String password = newAccount.getPassword();
        String EncryptedPassword = encryptionService.encryptString(password);
        newAccount.setPassword(EncryptedPassword);

        // save the account_id in the owner table
        coachService.save(new Coach(newAccount)).orElseThrow(
                () -> new AccountCreationFailureException("failed to create the account in the database")
        );

        // return the account profile dto (without password).
        return AccountEntityAndDtoConverters.convertAccountEntityToAccountProfileDto(newAccount);
    }
}
