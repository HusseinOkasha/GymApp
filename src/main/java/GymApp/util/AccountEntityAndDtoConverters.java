package GymApp.util;

import GymApp.dto.AccountProfileDto;
import GymApp.dto.CreateAccountDto;
import GymApp.entity.Account;

public class AccountEntityAndDtoConverters {

    public  static AccountProfileDto convertAccountEntityToAccountProfileDto(Account account){
        AccountProfileDto accountProfileDto = new AccountProfileDto(
                account.getId(), account.getFirstName(), account.getSecondName(), account.getThirdName(),
                account.getEmail(), account.getPhoneNumber(), account.getCreatedAt(),
                account.getUpdatedAt()
        );
        return accountProfileDto;
    }

    public  static Account convertAccountProfileDtoToAccountEntity(AccountProfileDto accountProfileDto){
        Account account = new Account(accountProfileDto.firstName(), accountProfileDto.SecondName(), accountProfileDto.thirdName(),
                accountProfileDto.email(), accountProfileDto.phoneNumber(), null, accountProfileDto.createdAt(),
                accountProfileDto.updatedAt()
        );
        return account;
    }

    public  static Account convertCreateAndUpdateAccountDtoToAccountEntity(CreateAccountDto createAccountDto){
        Account account = new Account(createAccountDto.firstName(), createAccountDto.SecondName(),
                createAccountDto.thirdName(), createAccountDto.email(), createAccountDto.phoneNumber(),
                createAccountDto.password(), createAccountDto.createdAt(), createAccountDto.updatedAt()
        );
        return account;
    }


}
