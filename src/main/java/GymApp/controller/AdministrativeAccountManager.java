package GymApp.controller;

import GymApp.dto.AccountProfileDto;
import GymApp.dto.DeleteAccountDto;
import GymApp.entity.Client;
import GymApp.entity.Coach;
import GymApp.entity.Owner;
import GymApp.security.EncryptionService;
import GymApp.service.*;
import GymApp.util.AccountEntityAndDtoConverters;
import jakarta.validation.Valid;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
// this controller represent the actions owner & coach can do to other accounts.
public class AdministrativeAccountManager {
    @Autowired
    private final CoachService coachService;
    @Autowired
    private final OwnerService ownerService;
    @Autowired
    private final ClientService clientService;

    public AdministrativeAccountManager(CoachService coachService, OwnerService ownerService,
                                        ClientService clientService) {
        this.coachService = coachService;
        this.ownerService = ownerService;
        this.clientService = clientService;
    }

    // get all owners accessible to owners only
    @GetMapping("/administrative-account-manager/owners")
    @Validated
    @PreAuthorize("hasAuthority('SCOPE_OWNER')")
    List<AccountProfileDto> getAllOwnerAccounts(){
        // get all owners from the database
        List<Owner> result = ownerService.findAll();

        // get their accounts and convert it to account profile dto
        return result.stream().map(Owner::getAccount)
                .map(AccountEntityAndDtoConverters::convertAccountEntityToAccountProfileDto).toList();

    }


    // get all owners accessible to owners only
    @GetMapping("/administrative-account-manager/coaches")
    @Validated
    @PreAuthorize("hasAuthority('SCOPE_OWNER')")
    List<AccountProfileDto> getAllCoachAccounts(){
        // get all owners from the database
        List<Coach> result = coachService.findAll();

        // get their accounts and convert it to account profile dto
        return result.stream().map(Coach::getAccount)
                .map(AccountEntityAndDtoConverters::convertAccountEntityToAccountProfileDto).toList();

    }



    // only an owner can delete coach account.
    @DeleteMapping("/administrative-account-manager/coaches")
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
            // in case there is no email or pass
            throw new BadRequestException("Empty email and phoneNumber");
        }
    }

    // get all owners accessible to owners & coaches only
    @GetMapping("/administrative-account-manager/clients")
    @Validated
    @PreAuthorize("hasAuthority('SCOPE_OWNER') or hasAuthority('SCOPE_COACH')")
    List<AccountProfileDto> getAllClientsAccounts(){
        // get all owners from the database
        List<Client> result = clientService.findAll();

        // get their accounts and convert it to account profile dto
        return result.stream().map(Client::getAccount)
                .map(AccountEntityAndDtoConverters::convertAccountEntityToAccountProfileDto).toList();

    }


    // only an owner can delete client account.
    @DeleteMapping("/administrative-account-manager/clients")
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
