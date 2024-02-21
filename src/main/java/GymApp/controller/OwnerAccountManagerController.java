package GymApp.controller;


import GymApp.dto.AccountProfileDto;
import GymApp.dto.CreateAccountDto;
import GymApp.entity.Account;
import GymApp.entity.Owner;
import GymApp.enums.AccountType;
import GymApp.exception.AccountCreationFailureException;

import GymApp.exception.AccountNotFoundException;
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
    @Autowired
    private final TokenService tokenService;

    public OwnerAccountManagerController(AccountService accountService, OwnerService ownerService,
                                         EncryptionService encryptionService, TokenService tokenService) {
        this.accountService = accountService;
        this.ownerService = ownerService;
        this.encryptionService = encryptionService;
        this.tokenService = tokenService;
    }

    // get list of all owner accounts
    @GetMapping("/owner-account-manager/all")
    @PreAuthorize("hasAuthority('SCOPE_OWNER')")
    public List<AccountProfileDto> getAllOwners() {
        List<Owner> owners = ownerService.findAll();
        return owners.stream().map(Owner::getAccount)
                .map(AccountEntityAndDtoConverters::convertAccountEntityToAccountProfileDto).toList();
    }

    // get owner account details
    @GetMapping("/owner-account-manager")
    @PreAuthorize("hasAuthority('SCOPE_OWNER')")
    public AccountProfileDto getOwner(Authentication authentication) throws AccountNotFoundException {
        String name = authentication.getName();
        return AccountEntityAndDtoConverters
                .convertAccountEntityToAccountProfileDto(
                        ownerService.findByEmailOrPhoneNumber(name, name)
                                .orElseThrow(() -> new AccountNotFoundException("Account not found")).getAccount()
                );
    }

    // create new owner account.
    @PostMapping("/owner-account-manager")
    @PreAuthorize("hasAuthority('SCOPE_OWNER')")
    @Validated
    public AccountProfileDto createOwnerAccount(@RequestBody @Valid CreateAccountDto createAccountDto)
            throws Exception {

        // create entity account from the account dto
        Account newAccount = AccountEntityAndDtoConverters
                .convertCreateAndUpdateAccountDtoToAccountEntity(createAccountDto);

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


    // update owner account (an owner can update his own account.)
    @PutMapping("/owner-account-manager")
    @PreAuthorize("hasAuthority('SCOPE_OWNER')")
    @Validated
    public String updateOwnerAccount(@RequestBody @Valid AccountProfileDto accountProfileDto,
                                     Authentication authentication) throws Exception {

        // extract the email / phone number from authentication object.
        String name = authentication.getName();

        // extract the account from the database.
        Account dbAccount = accountService.findByEmailOrPhoneNumber(name, name).orElseThrow(() -> new AccessDeniedException(""));

        // update the owner's account with the new data.
        dbAccount.setFirstName(accountProfileDto.firstName());
        dbAccount.setSecondName(accountProfileDto.SecondName());
        dbAccount.setThirdName(accountProfileDto.thirdName());
        dbAccount.setEmail(accountProfileDto.email());
        dbAccount.setPhoneNumber(accountProfileDto.phoneNumber());

        // reflect the updates to the database.
        accountService.save(dbAccount);

        // return new token in case that the email or phone number are changed.
        return tokenService.generateToken(dbAccount.getEmail(), AccountType.OWNER);
    }

    // Delete owner account (an owner can delete his own account.)
    @DeleteMapping("/owner-account-manager")
    @PreAuthorize("hasAuthority('SCOPE_OWNER')")
    public ResponseEntity deleteOwnerAccount(Authentication authentication){
        // extract email / phone number from the authentication object.
        String name  = authentication.getName();

        ownerService.deleteByAccount_EmailOrAccount_phoneNumber(name, name);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
