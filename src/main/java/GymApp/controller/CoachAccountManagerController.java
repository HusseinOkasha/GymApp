package GymApp.controller;

import GymApp.dto.AccountProfileDto;
import GymApp.dto.ChangePasswordDto;
import GymApp.dto.CreateAccountDto;
import GymApp.dto.DeleteAccountDto;
import GymApp.entity.Account;

import GymApp.entity.Coach;

import GymApp.security.EncryptionService;
import GymApp.service.*;
import GymApp.util.entityAndDtoMappers.AccountMapper;
import jakarta.validation.Valid;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CoachAccountManagerController {
    @Autowired
    private final CoachService coachService;
    @Autowired
    private final EncryptionService encryptionService;
    @Autowired
    private final Util util;



    public CoachAccountManagerController(CoachService coachService, EncryptionService encryptionService, Util util) {
        this.coachService = coachService;
        this.encryptionService = encryptionService;
        this.util = util;
    }

    @PostMapping("/coach-account-manager")
    @Validated
    @PreAuthorize("hasAuthority('SCOPE_OWNER')")
    public AccountProfileDto createNewCoachAccount(@RequestBody @Valid CreateAccountDto createAccountDto)
            throws Exception {

        // create entity account from the account dto
        Account newAccount = AccountMapper
                .createAccountDtoToAccountEntity(createAccountDto);

        // encrypt the password
        String password = newAccount.getPassword();
        String EncryptedPassword = encryptionService.encryptString(password);
        newAccount.setPassword(EncryptedPassword);

        // save the account_id in the owner table
        Coach.Builder coachBuilder = new Coach.Builder();
        Coach coach  = coachBuilder.account(newAccount).build();
        coachService.save(coach);

        // return the account profile dto (without password).
        return AccountMapper.accountEntityToAccountProfileDto(newAccount);
    }

    // Get my profile details
    @GetMapping("/coach-account-manager")
    @Validated
    AccountProfileDto getMyAccount(Authentication authentication) throws Exception {
        return util.getMyAccount(authentication);
    }

    // get all coaches accessible to owners only
    @GetMapping("/coach-account-manager/coaches")
    @Validated
    @PreAuthorize("hasAuthority('SCOPE_OWNER')")
    List<AccountProfileDto> getAllCoachAccounts(){
        // get all owners from the database
        List<Coach> result = coachService.findAll();

        // get their accounts and convert it to account profile dto
        return result.stream().map(Coach::getAccount)
                .map(AccountMapper::accountEntityToAccountProfileDto).toList();

    }

    @PutMapping("/coach-account-manager")
    @Validated
    AccountProfileDto updateMyProfile(@RequestBody @Valid AccountProfileDto accountProfileDto,
                                      Authentication authentication) throws Exception {
        return util.updateMyProfile(accountProfileDto, authentication);
    }

    // change password
    @PutMapping("/coach-account-manager/password")
    @Validated
    void changePassword(@RequestBody @Valid ChangePasswordDto changePasswordDto, Authentication authentication){
        util.changePassword(changePasswordDto, authentication);
    }

    // only an owner can delete coach account.
    @DeleteMapping("/coach-account-manager")
    @Validated
    @PreAuthorize("hasAuthority('SCOPE_OWNER')")
    public void deleteCoachAccount(@RequestBody @Valid DeleteAccountDto deleteAccountDto) throws BadRequestException {
        // Here we depend on email or phone number to delete coach account.
        // So we check the existence of both
        if(deleteAccountDto.email() != null){
            coachService.deleteByAccount_Email(deleteAccountDto.email());
        }
        else if (deleteAccountDto.phoneNumber()!=null) {
            coachService.deleteByAccount_PhoneNumber(deleteAccountDto.phoneNumber());
        }
        else {
            // in case there is no email or pass
            throw new BadRequestException("Empty email and phoneNumber");
        }
    }
}
