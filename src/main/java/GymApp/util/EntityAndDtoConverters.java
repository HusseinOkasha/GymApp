package GymApp.util;

import GymApp.dto.AccountProfileDto;
import GymApp.dto.ClientAccountProfileDto;
import GymApp.dto.CreateAccountDto;
import GymApp.entity.Account;
import GymApp.entity.Client;

public class EntityAndDtoConverters {

    public static AccountProfileDto convertAccountEntityToAccountProfileDto(Account account) {
        AccountProfileDto accountProfileDto = new AccountProfileDto(
                account.getId(), account.getFirstName(), account.getSecondName(), account.getThirdName(),
                account.getEmail(), account.getPhoneNumber(), account.getCreatedAt(),
                account.getUpdatedAt()
        );
        return accountProfileDto;
    }

    public static Account convertAccountProfileDtoToAccountEntity(AccountProfileDto accountProfileDto) {
        Account.Builder accountBuilder = new Account.Builder();
        return accountBuilder.firstName(accountProfileDto.firstName())
                .secondName(accountProfileDto.SecondName())
                .thirdName(accountProfileDto.thirdName())
                .email(accountProfileDto.email())
                .phoneNumber(accountProfileDto.phoneNumber())
                .createdAt(accountProfileDto.createdAt())
                .updateAt(accountProfileDto.updatedAt())
                .build();
    }

    public static Account convertCreateAccountDtoToAccountEntity(CreateAccountDto createAccountDto){
        Account.Builder accountBuilder = new Account.Builder();
        return accountBuilder.firstName(createAccountDto.firstName())
                .secondName(createAccountDto.secondName())
                .thirdName(createAccountDto.thirdName())
                .email(createAccountDto.email())
                .phoneNumber(createAccountDto.phoneNumber())
                .password(createAccountDto.password())
                .build();
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