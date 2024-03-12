package GymApp.controller;


import GymApp.dto.AccountProfileDto;

import GymApp.dto.ClientAccountProfileDto;
import GymApp.dto.CreateClientAccountDto;
import GymApp.entity.Account;
import GymApp.entity.Client;

import GymApp.exception.AccountCreationFailureException;
import GymApp.exception.AccountNotFoundException;
import GymApp.security.EncryptionService;
import GymApp.service.ClientService;
import GymApp.util.AccountEntityAndDtoConverters;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ClientAccountManagerController {
    @Autowired
    private ClientService clientService;
    @Autowired
    private EncryptionService encryptionService;

    public ClientAccountManagerController(ClientService clientService, EncryptionService encryptionService){
        this.clientService = clientService;
        this.encryptionService = encryptionService;
    }

    // get client profile
    @GetMapping("/client-account-manager")
    @Validated
    public ClientAccountProfileDto getMyProfile(Authentication authentication) throws Exception {
        // we depend on account_id as an identifier
        long identifier = Long.parseLong(authentication.getName());

        // extract the account from the database and change it to dto which doesn't include the password.
        // but it also includes the client birthdate
        return AccountEntityAndDtoConverters
                .convertClientEntityToClientAccountProfileDto(clientService.findByAccountId(identifier)
                        .orElseThrow(()->new AccountNotFoundException("")));
    }


    // create new client account
    @PostMapping("/client-account-manager")
    @Validated
    @PreAuthorize("hasAuthority('SCOPE_OWNER')")
    public ClientAccountProfileDto createNewClientAccount(@RequestBody @Valid CreateClientAccountDto createClientAccountDto)
            throws Exception {

        // create entity account from the account dto
        Account newAccount = AccountEntityAndDtoConverters
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
        return AccountEntityAndDtoConverters.convertClientEntityToClientAccountProfileDto(dbClient);
    }


}
