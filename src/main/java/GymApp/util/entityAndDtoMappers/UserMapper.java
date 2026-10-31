package GymApp.util.entityAndDtoMappers;

import GymApp.dto.AccountProfileDto;
import GymApp.dto.CreateAccountDto;
import GymApp.entity.User;

public class UserMapper {
    // Account ==> AccountProfileDto
    public static AccountProfileDto accountEntityToAccountProfileDto(User user) {
        AccountProfileDto accountProfileDto = new AccountProfileDto(
                user.getId(), user.getFirstName(), user.getSecondName(), user.getThirdName(),
                user.getEmail(), user.getPhoneNumber(), user.getCreatedAt(),
                user.getUpdatedAt()
        );
        return accountProfileDto;
    }

    // Account ==> createAccountDto
    public static CreateAccountDto accountEntityToCreateAccountDto(User user) {
        return new CreateAccountDto(
                user.getFirstName(), user.getSecondName(), user.getThirdName(),
                user.getEmail(), user.getPhoneNumber(), user.getPassword());

    }

    // accountProfileDto ==> Account
    public static User accountProfileDtoToAccountEntity(AccountProfileDto accountProfileDto) {
        User.Builder accountBuilder = new User.Builder();
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
    public static User createAccountDtoToAccountEntity(CreateAccountDto createAccountDto) {
        User.Builder accountBuilder = new User.Builder();
        return accountBuilder.firstName(createAccountDto.firstName())
                .secondName(createAccountDto.secondName())
                .thirdName(createAccountDto.thirdName())
                .email(createAccountDto.email())
                .phoneNumber(createAccountDto.phoneNumber())
                .password(createAccountDto.password())
                .build();
    }

}
