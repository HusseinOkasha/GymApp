package GymApp.controller;

import GymApp.dao.CoachRepository;
import GymApp.dto.AccountProfileDto;
import GymApp.dto.CreateAccountDto;
import GymApp.dto.DeleteAccountDto;
import GymApp.entity.Account;

import GymApp.entity.Coach;

import GymApp.entity.Owner;
import GymApp.exception.AccountCreationFailureException;
import GymApp.exception.AccountNotFoundException;
import GymApp.exception.UpdateAccountFailureException;
import GymApp.security.EncryptionService;
import GymApp.service.*;
import GymApp.util.AccountEntityAndDtoConverters;
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
    private final AccountService accountService;
    @Autowired
    private final TokenService tokenService;

    public CoachAccountManagerController(CoachService coachService, EncryptionService encryptionService, AccountService accountService,
                                         TokenService tokenService) {
        this.coachService = coachService;
        this.encryptionService = encryptionService;
        this.accountService = accountService;
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

    @GetMapping("/coach-account-manager/all")
    @Validated
    @PreAuthorize("hasAuthority('SCOPE_OWNER')")
    public List<AccountProfileDto> getAllCoaches(){
        // extract all accounts of type coach from the database
        List<Coach> coaches = coachService.findAll();
        return coaches.stream().map(Coach::getAccount)
                .map(AccountEntityAndDtoConverters::convertAccountEntityToAccountProfileDto).toList();
    }

    @DeleteMapping("/coach-account-manager")
    @Validated
    @PreAuthorize("hasAuthority('SCOPE_OWNER')")
    public void deleteCoachAccount(@RequestBody @Valid DeleteAccountDto deleteAccountDto) throws BadRequestException {
        // Here we depend on email or phone number to delete coach account.
        // So we check the existance of both
        if(deleteAccountDto.email() != null){
            coachService.deleteByAccount_Email(deleteAccountDto.email());
        }
        else if (deleteAccountDto.phoneNumber()!=null) {
            coachService.deleteByAccount_PhoneNumber(deleteAccountDto.phoneNumber());
        }
        else {
            // incase there is no email or pass
            throw new BadRequestException("Empty email and phoneNumber");
        }
    }

    @GetMapping("/coach-account-manager")
    @Validated
    @PreAuthorize("hasAuthority('SCOPE_COACH')")
    public AccountProfileDto getMyProfile(Authentication authentication) throws  Exception {

        // extract the account id from the authentication object.
        long id = Long.parseLong(authentication.getName());

        // get the coach with this account id.
        Coach coach = coachService.findByAccountId(id).orElseThrow(()-> new AccountNotFoundException(""));

        // return coach profile.
        return AccountEntityAndDtoConverters.convertAccountEntityToAccountProfileDto(coach.getAccount());
    }


    @PutMapping("/coach-account-manager")
    @Validated
    @PreAuthorize("hasAuthority('SCOPE_COACH')")
    public AccountProfileDto updateMyProfile(@RequestBody @Valid AccountProfileDto accountProfileDto,
                                             Authentication authentication) throws  Exception {

        // extract the account id from the authentication object.
        long id = Long.parseLong(authentication.getName());

        // get the coach with this account id.
        Account dbAccount = accountService.findById(id).orElseThrow(()-> new AccountNotFoundException(""));

        // update the coach's account with the new data.
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

}
