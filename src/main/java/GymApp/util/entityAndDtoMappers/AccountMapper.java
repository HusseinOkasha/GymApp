package GymApp.util.entityAndDtoMappers;

import GymApp.dto.AccountProfileDto;
import GymApp.dto.ClientAccountProfileDto;
import GymApp.dto.CreateAccountDto;
import GymApp.entity.Account;
import GymApp.entity.Client;

public class AccountMapper {
    // Account ==> AccountProfileDto
    public static AccountProfileDto accountEntityToAccountProfileDto(Account account) {
        AccountProfileDto accountProfileDto = new AccountProfileDto(
                account.getId(), account.getFirstName(), account.getSecondName(), account.getThirdName(),
                account.getEmail(), account.getPhoneNumber(), account.getCreatedAt(),
                account.getUpdatedAt()
        );
        return accountProfileDto;
    }

    // Account ==> createAccountDto
    public static CreateAccountDto accountEntityToCreateAccountDto(Account account) {
        return new CreateAccountDto(account.getFirstName(), account.getSecondName(), account.getThirdName(),
                account.getEmail(), account.getPhoneNumber(), account.getPassword());

    }

    // accountProfileDto ==> Account
    public static Account accountProfileDtoToAccountEntity(AccountProfileDto accountProfileDto) {
        Account.Builder accountBuilder = new Account.Builder();
        return accountBuilder.firstName(accountProfileDto.firstName())
                .secondName(accountProfileDto.secondName())
                .thirdName(accountProfileDto.thirdName())
                .email(accountProfileDto.email())
                .phoneNumber(accountProfileDto.phoneNumber())
                .createdAt(accountProfileDto.createdAt())
                .updateAt(accountProfileDto.updatedAt())
                .build();
    }

    //  createAccountDto ==> Account
    public static Account createAccountDtoToAccountEntity(CreateAccountDto createAccountDto) {
        Account.Builder accountBuilder = new Account.Builder();
        return accountBuilder.firstName(createAccountDto.firstName())
                .secondName(createAccountDto.secondName())
                .thirdName(createAccountDto.thirdName())
                .email(createAccountDto.email())
                .phoneNumber(createAccountDto.phoneNumber())
                .password(createAccountDto.password())
                .build();
    }

}
