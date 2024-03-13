package GymApp.controller;

import GymApp.dto.AccountProfileDto;
import GymApp.dto.ChangePasswordDto;
import GymApp.entity.Account;
import GymApp.exception.AccountNotFoundException;
import GymApp.exception.UpdateAccountFailureException;
import GymApp.security.EncryptionService;
import GymApp.service.AccountService;
import GymApp.util.AccountEntityAndDtoConverters;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

@Service
public class Util {
    @Autowired
    private final AccountService accountService;

    @Autowired
    private final EncryptionService encryptionService;

    public Util(AccountService accountService, EncryptionService encryptionService) {
        this.accountService = accountService;
        this.encryptionService = encryptionService;
    }

    public AccountProfileDto updateMyProfile(@RequestBody @Valid AccountProfileDto accountProfileDto,
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

    AccountProfileDto getMyAccount(Authentication authentication) throws Exception {
        // this function is used to get the account details in the account table.

        // we depend on account_id as an identifier
        long identifier = Long.parseLong(authentication.getName());

        // extract the account from the database and change it to dto which doesn't include the password.
        return AccountEntityAndDtoConverters
                .convertAccountEntityToAccountProfileDto(accountService.findById(identifier)
                        .orElseThrow(()->new AccountNotFoundException("")));
    }

    void changePassword(ChangePasswordDto changePasswordDto, Authentication authentication){
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




}
