package GymApp.util;

import GymApp.dto.AccountProfileDto;
import GymApp.dto.ClientAccountProfileDto;
import GymApp.dto.CreateAccountDto;
import GymApp.entity.Account;
import GymApp.entity.Client;

public class AccountEntityAndDtoConverters {

    public static AccountProfileDto convertAccountEntityToAccountProfileDto(Account account) {
        AccountProfileDto accountProfileDto = new AccountProfileDto(
                account.getId(), account.getFirstName(), account.getSecondName(), account.getThirdName(),
                account.getEmail(), account.getPhoneNumber(), account.getCreatedAt(),
                account.getUpdatedAt()
        );
        return accountProfileDto;
    }

    public static Account convertAccountProfileDtoToAccountEntity(AccountProfileDto accountProfileDto) {
        Account account = new Account(accountProfileDto.firstName(), accountProfileDto.SecondName(), accountProfileDto.thirdName(),
                accountProfileDto.email(), accountProfileDto.phoneNumber(), null, accountProfileDto.createdAt(),
                accountProfileDto.updatedAt()
        );
        return account;
    }

    public static Account convertCreateAccountDtoToAccountEntity(CreateAccountDto createAccountDto){
        Account account = new Account();
        account.setFirstName(createAccountDto.firstName());
        account.setSecondName(createAccountDto.SecondName());
        account.setThirdName(createAccountDto.thirdName());
        account.setEmail(createAccountDto.email());
        account.setPassword(createAccountDto.password());
        account.setPhoneNumber(createAccountDto.phoneNumber());
        return account;
    }

    public static ClientAccountProfileDto convertClientEntityToClientAccountProfileDto(Client client) {
        Account account = client.getAccount();
        AccountProfileDto accountProfileDto = new AccountProfileDto(account.getId(), account.getFirstName(),
                account.getSecondName(), account.getThirdName(), account.getEmail(), account.getPhoneNumber(),
                account.getCreatedAt(), account.getUpdatedAt()) ;

        return new ClientAccountProfileDto(accountProfileDto,
                client.getBirthDate());
    }



}