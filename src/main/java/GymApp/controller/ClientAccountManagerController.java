package GymApp.controller;


import GymApp.dto.*;

import GymApp.entity.Account;
import GymApp.entity.Client;

import GymApp.exception.AccountCreationFailureException;
import GymApp.exception.AccountNotFoundException;
import GymApp.exception.UpdateAccountFailureException;
import GymApp.security.EncryptionService;
import GymApp.service.ClientService;
import GymApp.util.EntityAndDtoConverters;
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
public class ClientAccountManagerController {
    @Autowired
    private final ClientService clientService;
    @Autowired
    private final EncryptionService encryptionService;

    @Autowired
    private final Util util;

    public ClientAccountManagerController(ClientService clientService, EncryptionService encryptionService, Util util){
        this.clientService = clientService;
        this.encryptionService = encryptionService;
        this.util = util;
    }

    // create new client account
    @PostMapping("/client-account-manager")
    @Validated
    @PreAuthorize("hasAuthority('SCOPE_OWNER')")
    public ClientAccountProfileDto createNewClientAccount(@RequestBody @Valid CreateClientAccountDto createClientAccountDto)
            throws Exception {

        // create entity account from the account dto
        Account newAccount = EntityAndDtoConverters
                .convertCreateAccountDtoToAccountEntity(createClientAccountDto.createAccountDto());

        // encrypt the password
        String password = newAccount.getPassword();
        String EncryptedPassword = encryptionService.encryptString(password);
        newAccount.setPassword(EncryptedPassword);

        // create new client and set it's birthDate
        Client newClient  = new Client(newAccount);
        newClient.setBirthDate(createClientAccountDto.birthDate());

        // save the account_id in the owner table
        Client dbClient = clientService.save(newClient).orElseThrow(
                () -> new AccountCreationFailureException("failed to create the account in the database")
        );

        // return the account profile dto (without password).
        return EntityAndDtoConverters.convertClientEntityToClientAccountProfileDto(dbClient);
    }

    // get client profile
    @GetMapping("/client-account-manager")
    @Validated
    public ClientAccountProfileDto getMyProfile(Authentication authentication) throws Exception {
        // we depend on account_id as an identifier
        long identifier = Long.parseLong(authentication.getName());

        // extract the account from the database and change it to dto which doesn't include the password.
        // but it also includes the client birthdate
        return EntityAndDtoConverters
                .convertClientEntityToClientAccountProfileDto(clientService.findByAccountId(identifier)
                        .orElseThrow(()->new AccountNotFoundException("")));
    }


    // get all owners accessible to owners & coaches only
    @GetMapping("/client-account-manager/clients")
    @Validated
    @PreAuthorize("hasAuthority('SCOPE_OWNER') or hasAuthority('SCOPE_COACH')")
    List<ClientAccountProfileDto> getAllClientsAccounts(){
        // get all clients from the database
        List<Client> result = clientService.findAll();

        // get their accounts and convert it to account profile dto
        return result.stream()
                .map(EntityAndDtoConverters::convertClientEntityToClientAccountProfileDto).toList();

    }

    // update my profile
    @PutMapping("/client-account-manager")
    @Validated
    ClientAccountProfileDto updateMyProfile(@RequestBody @Valid ClientAccountProfileDto clientAccountProfileDto,
                                      Authentication authentication) throws Exception {
        AccountProfileDto accountProfileDto = util.updateMyProfile(clientAccountProfileDto.accountProfileDto()
                , authentication);

        // extract account_id from authentication object as I rely on it as an identifier.
        long identifier = Long.parseLong(authentication.getName());

        Client dbClient = clientService.findByAccountId(identifier).orElseThrow(()-> new AccountNotFoundException(""));

        dbClient.setBirthDate(clientAccountProfileDto.birthDate());
        clientService.save(dbClient).orElseThrow(()-> new UpdateAccountFailureException(""));

        return EntityAndDtoConverters.convertClientEntityToClientAccountProfileDto(dbClient);
    }

    // change password
    @PutMapping("/client-account-manager/password")
    @Validated
    void changePassword(@RequestBody @Valid ChangePasswordDto changePasswordDto, Authentication authentication){
        util.changePassword(changePasswordDto, authentication);
    }

    // only an owner can delete client account.
    @DeleteMapping("/client-account-manager")
    @Validated
    @PreAuthorize("hasAuthority('SCOPE_OWNER')")
    public void deleteClientAccount(@RequestBody @Valid DeleteAccountDto deleteAccountDto) throws BadRequestException {
        // Here we depend on email or phone number to delete coach account.
        // So we check the existence of both
        if(deleteAccountDto.email() != null){
            clientService.deleteByAccount_Email(deleteAccountDto.email());
        }
        else if (deleteAccountDto.phoneNumber()!=null) {
            clientService.deleteByAccount_PhoneNumber(deleteAccountDto.phoneNumber());
        }
        else {
            // in case there is no email or pass
            throw new BadRequestException("Empty email and phoneNumber");
        }
    }









}
