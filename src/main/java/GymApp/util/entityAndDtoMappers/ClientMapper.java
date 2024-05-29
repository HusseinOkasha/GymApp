package GymApp.util.entityAndDtoMappers;

import GymApp.dto.AccountProfileDto;
import GymApp.dto.ClientAccountProfileDto;
import GymApp.entity.Account;
import GymApp.entity.Client;

public class ClientMapper {

    // Client ==> clientAccountProfileDto.
    public static ClientAccountProfileDto clientEntityToClientAccountProfileDto(Client client) {
        Account account = client.getAccount();

        AccountProfileDto accountProfileDto = new AccountProfileDto(account.getId(), account.getFirstName(),
                account.getSecondName(), account.getThirdName(), account.getEmail(), account.getPhoneNumber(),
                account.getCreatedAt(), account.getUpdatedAt());

        return new ClientAccountProfileDto(accountProfileDto,
                client.getBirthDate());
    }
}
