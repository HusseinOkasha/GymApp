package GymApp.util;

import GymApp.dto.AccountProfileDto;
import GymApp.dto.ChangePasswordDto;
import GymApp.dto.CreateAccountDto;
import GymApp.entity.Account;
import GymApp.entity.Owner;
import GymApp.util.entityAndDtoMappers.AccountMapper;
import org.assertj.core.api.Assertions;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


public class OwnerAccountManagerControllerTestUtil {
    public static ResponseEntity<AccountProfileDto> attemptCreateOwnerAccount
            (CreateAccountDto createAccountDto, String token, int port, RestTemplate restTemplate) {
        Request.Builder<CreateAccountDto, AccountProfileDto> requestBuilder = Request.Builder.builder();
        Request<CreateAccountDto, AccountProfileDto> request = requestBuilder
                .httpMethod(HttpMethod.POST)
                .portNumber(port)
                .endPointUrl("/owner-account-manager")
                .httpEntity(token, createAccountDto)
                .responseDataType(new ParameterizedTypeReference<>() {
                })
                .restTemplate(restTemplate)
                .build();
        return request.sendRequest();

    }

    public static ResponseEntity<AccountProfileDto> attemptGetOwnerProfile(
            String token, int port, RestTemplate restTemplate) {
        Request.Builder<String, AccountProfileDto> requestBuilder = Request.Builder.builder();
        Request<String, AccountProfileDto> request = requestBuilder
                .httpMethod(HttpMethod.GET)
                .portNumber(port)
                .endPointUrl("/owner-account-manager")
                .httpEntity(token, null)
                .responseDataType(new ParameterizedTypeReference<>() {
                })
                .restTemplate(restTemplate)
                .build();
        return request.sendRequest();

    }

    public static ResponseEntity<AccountProfileDto> attemptUpdateOwnerProfile(
            String token, AccountProfileDto accountProfileDto, int port, RestTemplate restTemplate) {

        Request.Builder<AccountProfileDto, AccountProfileDto> requestBuilder = Request.Builder.builder();
        Request<AccountProfileDto, AccountProfileDto> request = requestBuilder
                .httpMethod(HttpMethod.PUT)
                .portNumber(port)
                .endPointUrl("/owner-account-manager")
                .httpEntity(token, accountProfileDto)
                .responseDataType(new ParameterizedTypeReference<>() {
                })
                .restTemplate(restTemplate)
                .build();
        return request.sendRequest();

    }

    public static ResponseEntity<Void> attemptChangeOwnerAccountPassword(
            String token, String password, int port, RestTemplate restTemplate) {
        ChangePasswordDto changePasswordDto = new ChangePasswordDto(password);
        Request.Builder<ChangePasswordDto, Void> requestBuilder = Request.Builder.builder();

        Request<ChangePasswordDto, Void> request = requestBuilder
                .httpMethod(HttpMethod.PUT)
                .portNumber(port)
                .endPointUrl("/owner-account-manager/password")
                .httpEntity(token, changePasswordDto)
                .responseDataType(new ParameterizedTypeReference<>() {
                })
                .restTemplate(restTemplate)
                .build();
        return request.sendRequest();

    }

    public static ResponseEntity<Void> attemptDeleteOwnerAccount
            (String token, int port, RestTemplate restTemplate) {

        Request.Builder<String, Void> requestBuilder = Request.Builder.builder();
        Request<String, Void> request = requestBuilder
                .httpMethod(HttpMethod.DELETE)
                .portNumber(port)
                .endPointUrl("/owner-account-manager")
                .httpEntity(token, null)
                .responseDataType(new ParameterizedTypeReference<>() {
                })
                .restTemplate(restTemplate)
                .build();
        return request.sendRequest();
    }

    public static void shouldCreateOwnerAccount(Owner owner, String token, int port, RestTemplate restTemplate) {
        /*
         *  owner: is a sample owner to be created.
         *  token: is JWT token generated after login.
         *  port: is the port number the server is running on
         *  restTemplate: used to send Http requests.
         *
         * */
        CreateAccountDto expected = AccountMapper.accountEntityToCreateAccountDto(owner.getAccount());
        ResponseEntity<AccountProfileDto> response = attemptCreateOwnerAccount(expected, token,
                port, restTemplate);

        AccountProfileDto actual = response.getBody();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(actual.firstName()).isEqualTo(expected.firstName());
        assertThat(actual.secondName()).isEqualTo(expected.secondName());
        assertThat(actual.thirdName()).isEqualTo(expected.thirdName());
        assertThat(actual.email()).isEqualTo(expected.email());
        assertThat(actual.phoneNumber()).isEqualTo(expected.phoneNumber());
    }


    public static void shouldNotCreateOwnerAccountWithMissingRequiredFields(Owner owner, String token, int port,
                                                                            RestTemplate restTemplate) {
        CreateAccountDto expected = AccountMapper.accountEntityToCreateAccountDto(owner.getAccount());
        ResponseEntity<AccountProfileDto> response = attemptCreateOwnerAccount(expected, token, port, restTemplate);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    public static void unAuthenticatedOwnerShouldNotCreateOwnerAccount(Owner owner, String token, int port,
                                                                       RestTemplate restTemplate) {
        CreateAccountDto expected = AccountMapper.accountEntityToCreateAccountDto(owner.getAccount());
        ResponseEntity<AccountProfileDto> response = attemptCreateOwnerAccount(expected, token, port, restTemplate);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);

    }

    public static void clientAndCoachShouldNotCreateOwnerAccount(Owner owner, String token, int port,
                                                                 RestTemplate restTemplate) {
        CreateAccountDto expected = AccountMapper.accountEntityToCreateAccountDto(owner.getAccount());
        ResponseEntity<AccountProfileDto> response = attemptCreateOwnerAccount(expected, token, port, restTemplate);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);

    }

    public static void shouldGetOwnerProfile(Owner owner, String token, int port, RestTemplate restTemplate) {
        // expected
        AccountProfileDto expectedAccountProfileDto = AccountMapper.accountEntityToAccountProfileDto(owner.getAccount());

        ResponseEntity<AccountProfileDto> response = attemptGetOwnerProfile(token, port, restTemplate);

        // underTest
        AccountProfileDto underTestAccountProfileDto = response.getBody();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(underTestAccountProfileDto)
                .usingRecursiveComparison()
                .ignoringFields("createdAt", "updatedAt").isEqualTo(underTestAccountProfileDto)
                .isEqualTo(expectedAccountProfileDto);
    }

    public static void shouldNotGetOwnerProfile(String token, int port, RestTemplate restTemplate) {

        ResponseEntity<AccountProfileDto> response = attemptGetOwnerProfile(token, port, restTemplate);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    public static void coachAndClientShouldNotGetOwnerProfile(String token, int port, RestTemplate restTemplate) {

        ResponseEntity<AccountProfileDto> response = attemptGetOwnerProfile(token, port, restTemplate);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    public static ResponseEntity<List<AccountProfileDto>> attemptGetAllOwners(
            String token, int port, RestTemplate restTemplate) {
        Request.Builder<String, List<AccountProfileDto>> requestBuilder = Request.Builder.builder();
        Request<String, List<AccountProfileDto>> request = requestBuilder
                .httpMethod(HttpMethod.GET)
                .portNumber(port)
                .endPointUrl("/owner-account-manager/owners")
                .httpEntity(token, null)
                .responseDataType(new ParameterizedTypeReference<>() {
                })
                .restTemplate(restTemplate)
                .build();
        return request.sendRequest();
    }

    public static void shouldGetAllOwners(List<Owner> owners, String token, int port, RestTemplate restTemplate) {

        ResponseEntity<List<AccountProfileDto>> response = attemptGetAllOwners(token, port, restTemplate);

        List<AccountProfileDto> expected = owners
                .stream()
                .map(Owner::getAccount)
                .map(AccountMapper::accountEntityToAccountProfileDto)
                .toList();
        List<AccountProfileDto> actual = response.getBody();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(actual.size()).isEqualTo(expected.size());
        assertThat(actual.get(0)).usingRecursiveComparison().ignoringFieldsOfTypes(LocalDateTime.class).isIn(expected);
        assertThat(actual.get(1)).usingRecursiveComparison().ignoringFieldsOfTypes(LocalDateTime.class).isIn(expected);
    }

    public static void shouldNotGetAllOwners(String token, int port, RestTemplate restTemplate) {
        ResponseEntity<List<AccountProfileDto>> response = attemptGetAllOwners(token, port, restTemplate);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    public static void shouldNotGetAllOwnersWithoutAccessToken(int port, RestTemplate restTemplate) {
        ResponseEntity<List<AccountProfileDto>> response = attemptGetAllOwners(null, port, restTemplate);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    public static void shouldUpdateOwnerAccount(String token, Owner owner, int port, RestTemplate restTemplate) {
        Account account = owner.getAccount();
        account.setFirstName("updated first name");
        account.setSecondName("updated second name");
        account.setThirdName("updated third name");
        account.setEmail("updated@email.com");
        account.setPhoneNumber("updated Phone Number");

        AccountProfileDto expected = AccountMapper.accountEntityToAccountProfileDto(account);

        ResponseEntity<AccountProfileDto> response =
                attemptUpdateOwnerProfile(token, expected, port, restTemplate);

        AccountProfileDto actual = response.getBody();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        // to ignore createdAt and updatedAt.
        assertThat(expected).usingRecursiveComparison().ignoringFieldsOfTypes(LocalDateTime.class).isEqualTo(actual);

    }

    public static void coachAndClientShouldNotUpdateOwnerAccount(
            String token, Owner owner, int port, RestTemplate restTemplate) {
        Account account = owner.getAccount();
        account.setFirstName("updated first name");
        account.setSecondName("updated second name");
        account.setThirdName("updated third name");
        account.setEmail("updated@email.com");
        account.setPhoneNumber("updated Phone Number");

        AccountProfileDto expected = AccountMapper.accountEntityToAccountProfileDto(account);

        ResponseEntity<AccountProfileDto> response =
                attemptUpdateOwnerProfile(token, expected, port, restTemplate);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);

    }

    public static void shouldNotUpdateOwnerAccountProfileWithoutAccessToken(Owner owner, int port, RestTemplate restTemplate) {
        Account account = owner.getAccount();
        account.setFirstName("updated first name");
        account.setSecondName("updated second name");
        account.setThirdName("updated third name");
        account.setEmail("updated@email.com");
        account.setPhoneNumber("updated Phone Number");

        AccountProfileDto expected = AccountMapper.accountEntityToAccountProfileDto(account);

        ResponseEntity<AccountProfileDto> response =
                attemptUpdateOwnerProfile(null, expected, port, restTemplate);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);

    }

    public static void shouldNotUpdateOwnerAccountProfileWithMissingRequiredFields
            (Owner owner, String token, int port, RestTemplate restTemplate) {

        AccountProfileDto accountProfileDto = AccountMapper.accountEntityToAccountProfileDto(owner.getAccount());
        ResponseEntity<AccountProfileDto> response =
                attemptUpdateOwnerProfile(token, accountProfileDto, port, restTemplate);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

    }

    public static void shouldChangeOwnerPassword(String token, String password, int port, RestTemplate restTemplate) {
        ResponseEntity<Void> response = attemptChangeOwnerAccountPassword(token, password, port, restTemplate);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    public static void coachAndClientShouldNotChangeOwnerPassword(String token, String password, int port,
                                                                  RestTemplate restTemplate) {
        ResponseEntity<Void> response = attemptChangeOwnerAccountPassword(token, password, port, restTemplate);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    public static void shouldNotChangeOwnerPasswordWithoutAccessToken
            (String password, int port, RestTemplate restTemplate) {
        ResponseEntity<Void> response = attemptChangeOwnerAccountPassword(null, password, port, restTemplate);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    public static void shouldNotChangePasswordWithEmptyPassword(String token, String password, int port,
                                                                RestTemplate restTemplate) {
        ResponseEntity<Void> response = attemptChangeOwnerAccountPassword(token, password, port, restTemplate);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    public static void shouldDeleteOwnerAccount(String token, int port, RestTemplate restTemplate) {
        ResponseEntity<Void> response = attemptDeleteOwnerAccount(token, port, restTemplate);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    public static void coachAndClientShouldNotDeleteOwnerAccount(String token, int port, RestTemplate restTemplate) {

        ResponseEntity<Void> response = attemptDeleteOwnerAccount(token, port, restTemplate);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    public static void shouldNotDeleteOwnerAccountWithoutAccessToken(int port, RestTemplate restTemplate) {
        ResponseEntity<Void> response = attemptDeleteOwnerAccount(null, port, restTemplate);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }


}
