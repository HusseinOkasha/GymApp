package GymApp.controller;

import GymApp.dto.AccountProfileDto;
import GymApp.dto.ChangePasswordDto;


import GymApp.entity.Account;

import GymApp.exception.AccountNotFoundException;
import GymApp.exception.UpdateAccountFailureException;
import GymApp.security.EncryptionService;
import GymApp.service.*;
import GymApp.util.AccountEntityAndDtoConverters;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
// this controller represents the common actions that owner, coach or client can do to their own accounts.
public class AccountManagerController {

    @Autowired
    private final OwnerService ownerService;
    @Autowired
    private final EncryptionService encryptionService;
    @Autowired
    private final AccountService accountService;

    public AccountManagerController(OwnerService ownerService, EncryptionService encryptionService,
                                    AccountService accountService) {
        this.ownerService = ownerService;
        this.encryptionService = encryptionService;
        this.accountService = accountService;

    }

    // Get my profile details
    @GetMapping("/account-manager")
    @Validated
    AccountProfileDto getMyAccount(Authentication authentication) throws Exception {
        // we depend on account_id as an identifier
        long identifier = Long.parseLong(authentication.getName());

     // extract the account from the database and change it to dto which doesn't include the password.
     return AccountEntityAndDtoConverters
             .convertAccountEntityToAccountProfileDto(accountService.findById(identifier)
             .orElseThrow(()->new AccountNotFoundException("")));
    }

    // update my account details
    @PutMapping("/account-manager")
    @Validated
    AccountProfileDto UpdateMyProfile(@RequestBody @Valid AccountProfileDto accountProfileDto,
                                      Authentication authentication) throws Exception {

        // extract account_id from authentication object as I rely on it as an identifier.
        long identifier = Long.parseLong(authentication.getName());

        // extract the account from the database.
        Account dbAccount = accountService.findById(identifier).orElseThrow(() -> new AccessDeniedException(""));

        // update the owner's account with the new data.
        dbAccount.setFirstName(accountProfileDto.firstName());
        dbAccount.setSecondName(accountProfileDto.SecondName());
        dbAccount.setThirdName(accountProfileDto.thirdName());
        dbAccount.setEmail(accountProfileDto.email());
        dbAccount.setPhoneNumber(accountProfileDto.phoneNumber());

        // reflect the updates to the database.
        dbAccount = accountService.save(dbAccount).orElseThrow(()->new UpdateAccountFailureException(""));

        // return the account profile dto (without password).
        return AccountEntityAndDtoConverters.convertAccountEntityToAccountProfileDto(dbAccount);

    }

    // change password
    @PutMapping("/account-manager/password")
    @Validated
    void changePassword(@RequestBody @Valid ChangePasswordDto changePasswordDto, Authentication authentication){
        //  extract the account id from the authentication object.
        long accountId = Long.parseLong(authentication.getName());

        // fetch the account from the database.
        Account dbAccount  = accountService.findById(accountId).orElseThrow(()-> new AccessDeniedException(""));

        // encrypt the password before saving it in the database.
        String encryptedPassword = encryptionService.encryptString(changePasswordDto.password());

        // replace the old password with the new one.
        dbAccount.setPassword(encryptedPassword);

        // save changes to the database.
        accountService.save(dbAccount);

    }


    // only an owner can delete his own account.
    @DeleteMapping("account-manager")
    @Validated
    @PreAuthorize("hasAuthority('SCOPE_OWNER')")
    void deleteMyOwnerAccount(Authentication authentication){
        // as I use the account_id as a principle while creating usernamePasswordAuthenticationToken.
        long identifier = Long.parseLong(authentication.getName());

        ownerService.deleteByAccountId(identifier);
    }

}
