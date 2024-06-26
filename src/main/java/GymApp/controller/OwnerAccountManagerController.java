package GymApp.controller;


import GymApp.dto.AccountProfileDto;
import GymApp.dto.ChangePasswordDto;
import GymApp.dto.CreateAccountDto;
import GymApp.entity.Account;
import GymApp.entity.Owner;

import GymApp.security.EncryptionService;
import GymApp.service.OwnerService;
import GymApp.util.entityAndDtoMappers.AccountMapper;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api")
public class OwnerAccountManagerController {
    @Autowired
    private final OwnerService ownerService;
    @Autowired
    private final EncryptionService encryptionService;
    @Autowired
    private final Util util;


    public OwnerAccountManagerController(OwnerService ownerService, EncryptionService encryptionService, Util util) {
        this.ownerService = ownerService;
        this.encryptionService = encryptionService;
        this.util = util;
    }


    // create new owner account.
    @PostMapping("/owner-account-manager")
    @PreAuthorize("hasAuthority('SCOPE_OWNER')")
    @Validated
    public AccountProfileDto createOwnerAccount(@RequestBody @Valid CreateAccountDto createAccountDto)
            throws Exception {

        // create entity account from the account dto
        Account newAccount = AccountMapper
                .createAccountDtoToAccountEntity(createAccountDto);

        // encrypt the password
        String password = newAccount.getPassword();
        String EncryptedPassword = encryptionService.encryptString(password);
        newAccount.setPassword(EncryptedPassword);

        // save the account_id in the owner table
        Owner.Builder ownerBuilder = new Owner.Builder();
        Owner owner = ownerBuilder.account(newAccount).build();
        ownerService.save(owner);

        // return the account profile dto (without password).
        return AccountMapper.accountEntityToAccountProfileDto(newAccount);
    }

    // Get my profile details
    @GetMapping("/owner-account-manager")
    @Validated
    @PreAuthorize("hasAuthority('SCOPE_OWNER')")
    AccountProfileDto getMyAccount(Authentication authentication) throws Exception {
       return util.getMyAccount(authentication);
    }

    // get all owners accessible to owners only
    @GetMapping("/owner-account-manager/owners")
    @Validated
    @PreAuthorize("hasAuthority('SCOPE_OWNER')")
    List<AccountProfileDto> getAllOwnerAccounts(){
        // get all owners from the database
        List<Owner> result = ownerService.findAll();

        // get their accounts and convert it to account profile dto
        return result.stream().map(Owner::getAccount)
                .map(AccountMapper::accountEntityToAccountProfileDto).toList();
    }

    @PutMapping("/owner-account-manager")
    @Validated
    @PreAuthorize("hasAuthority('SCOPE_OWNER')")
    AccountProfileDto updateMyProfile(@RequestBody @Valid AccountProfileDto accountProfileDto,
                                      Authentication authentication) throws Exception {
        return util.updateMyProfile(accountProfileDto, authentication);
    }

    // change password
    @PutMapping("/owner-account-manager/password")
    @Validated
    @PreAuthorize("hasAuthority('SCOPE_OWNER')")
    void changePassword(@RequestBody @Valid ChangePasswordDto changePasswordDto, Authentication authentication){
       util.changePassword(changePasswordDto, authentication);
    }


    // only an owner can delete his own account.
    @DeleteMapping("/owner-account-manager")
    @Validated
    @PreAuthorize("hasAuthority('SCOPE_OWNER')")
    void deleteMyOwnerAccount(Authentication authentication){
        // as I use the account_id as a principle while creating usernamePasswordAuthenticationToken.
        long identifier = Long.parseLong(authentication.getName());
        ownerService.deleteByAccountId(identifier);
    }

}
