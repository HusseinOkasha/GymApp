package GymApp.controller;

import GymApp.dto.*;
import GymApp.entity.Account;
import GymApp.entity.Client;
import GymApp.entity.Owner;
import GymApp.service.ClientService;
import GymApp.service.OwnerService;
import com.github.dockerjava.zerodep.shaded.org.apache.commons.codec.binary.Base64;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = { "spring.datasource.url=jdbc:tc:postgres:latest:///database", "spring.sql.init.mode=always" })
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ClientAccountManagerControllerTest {
    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("database").withUsername("myuser");

    private static String ownerToken;
    private static String clientToken;
    private static List<Account> ownerAccounts = new ArrayList<>();
    private static List<Account> clientAccounts = new ArrayList<>();
    @Autowired
    RestTemplate restTemplate;
    @Autowired
    ClientService clientService;

    @Autowired
    OwnerService ownerService;

    @LocalServerPort
    private int port;

    @BeforeAll
    static void generalSetUp(){
        // "123" encoded with bCrypt
        String bCryptPassword = "$2a$12$fdQCjXHktjZczz5hlHg77u8bIXUQdzGQf5k7ulN.cxzhW2vidHzSu";

        // Create 2 accounts
        Account acc1 = new Account("f1", "s1", "t1","e1@gmail.com", "1",
                bCryptPassword, null, null);
        Account acc2 = new Account("f2", "s2", "t2","e2@gmail.com", "2",
                bCryptPassword, null, null);

        // this account will be used as an owner account.
        Account acc3 = new Account("f3", "s3", "t3","e3@gmail.com", "3",
                bCryptPassword, null, null);

        // add newly created accounts to the client accounts list.
        clientAccounts.add(acc1);
        clientAccounts.add(acc2);

        // add the owner account the owner account list
        ownerAccounts.add(acc3);

    }
    @BeforeEach
    void setUp() {
        // delete all clients & owners to start fresh.
        clientService.deleteAll();
        ownerService.deleteAll();

        // Make them client accounts.
        Client client1 = new Client(new Account(clientAccounts.get(0)), LocalDate.of(2024,3,1));
        Client client2 = new Client(new Account(clientAccounts.get(1)), LocalDate.of(2024,3,1));
        clientService.save(client1);
        clientService.save(client2);

        // create owner account.
        ownerService.save(new Owner(new Account(ownerAccounts.get(0))));

        // get token as a result of client login
        clientToken = login("1", "123",
                getBaseUrl()+ "/login/client");

        // get token as a result of owner login.
        ownerToken = login("3", "123",
                getBaseUrl()+ "/login/owner");
    }

    @Test
    void createNewClientAccount() {
        // because port number is generated randomly.
        String baseUrl = getBaseUrl();

        // Sending request associated with the token to get accountProfileDto for the owner embedded in the token
        HttpHeaders headers = new HttpHeaders();

        // token value is assigned during setUp method
        headers.add("Authorization", "Bearer " + ownerToken);

        // new owner account values
        String firstName = "f4";
        String secondName = "s4";
        String thirdName = "t4";
        String email = "e4@gmail.com";
        String phoneNumber = "4";
        String password = "123";
        LocalDate birthDate = LocalDate.of(2024, 3, 1);

        // initalize the dto
        CreateAccountDto createAccountDto = new CreateAccountDto(firstName, secondName, thirdName, email, phoneNumber,
                password);

        CreateClientAccountDto createClientAccountDto = new CreateClientAccountDto(createAccountDto, birthDate);

        HttpEntity<CreateClientAccountDto> request = new HttpEntity<>(createClientAccountDto, headers);

        ClientAccountProfileDto createdAccount = restTemplate.exchange(baseUrl + "/client-account-manager",
                HttpMethod.POST, request, ClientAccountProfileDto.class).getBody();

        assertThat(createdAccount).isNotNull();

        //  get accountProfile Dto, doesn't include birthdate.
        AccountProfileDto accountProfileDto = createdAccount.accountProfileDto();

        assertThat(accountProfileDto).isNotNull();
        assertThat(accountProfileDto.firstName()).isEqualTo(firstName);
        assertThat(accountProfileDto.SecondName()).isEqualTo(secondName);
        assertThat(accountProfileDto.thirdName()).isEqualTo(thirdName);
        assertThat(accountProfileDto.email()).isEqualTo(email);
        assertThat(accountProfileDto.phoneNumber()).isEqualTo(phoneNumber);
        assertThat(createdAccount.birthDate()).isEqualTo(birthDate);
    }

    @Test
    void shouldGetMyProfile() {
        // As port number as it's generated randomly.
        String baseUrl = getBaseUrl();

        // Sending request associated with the token to get accountProfileDto for the owner embedded in the token
        HttpHeaders headers = new HttpHeaders();

        // token value is assigned during setUp method
        headers.add("Authorization", "Bearer " + clientToken);
        HttpEntity<String> request = new HttpEntity<String>(headers);
        ClientAccountProfileDto clientAccount = restTemplate.exchange(baseUrl + "/client-account-manager",
                HttpMethod.GET, request, ClientAccountProfileDto.class).getBody();

        assertThat(clientAccount).isNotNull();
        assertThat(clientAccount.accountProfileDto()).isNotNull();
        assertThat(clientAccount.accountProfileDto().email()).isNotNull().isEqualTo("e1@gmail.com");
    }

    @Test
    void getAllClientsAccounts() {
        // As port number as it's generated randomly.
        String baseUrl = getBaseUrl() ;

        // Sending request associated with the token to get a list of all clients.
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + ownerToken);

        HttpEntity<String> request = new HttpEntity<String>(headers);
        ParameterizedTypeReference<List<ClientAccountProfileDto>>
                responseType = new ParameterizedTypeReference<List<ClientAccountProfileDto>>() {};
        List<ClientAccountProfileDto> allClientAccounts = restTemplate.exchange(baseUrl + "/client-account-manager/clients",
                HttpMethod.GET, request, responseType).getBody();

        // Check that there is 2 owner accounts returned
        assertThat(allClientAccounts).isNotNull();
        assertThat(allClientAccounts.size()).isEqualTo(2);

        // check that the owners returned are the same ones which we inserted by the checking their emails.
        List<String>possibleEmails = List.of(clientAccounts.get(0).getEmail(), clientAccounts.get(1).getEmail());

        assertThat(allClientAccounts.get(0).accountProfileDto()).isNotNull();
        assertThat(allClientAccounts.get(0).accountProfileDto().email()).isIn(possibleEmails);

        assertThat(allClientAccounts.get(1).accountProfileDto()).isNotNull();
        assertThat(allClientAccounts.get(1).accountProfileDto().email()).isIn(possibleEmails);

    }

    @Test
    void updateMyProfile() {
        // As port number as it's generated randomly.
        String baseUrl = getBaseUrl();

        // new values to update client account.
        String updatedFirstName = "updatedFirstName";
        String updatedSecondName = "updatedSecondName";
        String updatedThirdName = "updatedThirdName";
        String updatedEmail = "updatedE1@gmail.com";
        String updatedPhoneNumber = "updatedPhoneNumber";
        LocalDate birthDate = LocalDate.of(2024, 3, 1);

        AccountProfileDto accountProfileDto = new AccountProfileDto(0,updatedFirstName, updatedSecondName,
                updatedThirdName,updatedEmail, updatedPhoneNumber, null, null);
        ClientAccountProfileDto clientAccountProfileDto = new ClientAccountProfileDto(accountProfileDto, birthDate);

        HttpHeaders headers = new HttpHeaders();

        // token value is assigned during setUp method
        headers.add("Authorization", "Bearer " + clientToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<ClientAccountProfileDto> request = new HttpEntity<>(clientAccountProfileDto, headers);
        ClientAccountProfileDto updatedClientAccountProfileDto = restTemplate.exchange(baseUrl + "/client-account-manager",
                HttpMethod.PUT, request, ClientAccountProfileDto.class).getBody();

        AccountProfileDto updatedAccountProfileDto = updatedClientAccountProfileDto.accountProfileDto();

        // check that all updates are reflected.
        assertThat(updatedAccountProfileDto).isNotNull();
        assertThat(updatedAccountProfileDto.firstName()).isEqualTo(updatedFirstName);
        assertThat(updatedAccountProfileDto.SecondName()).isEqualTo(updatedSecondName);
        assertThat(updatedAccountProfileDto.thirdName()).isEqualTo(updatedThirdName);
        assertThat(updatedAccountProfileDto.email()).isEqualTo(updatedEmail);
        assertThat(updatedAccountProfileDto.phoneNumber()).isEqualTo(updatedPhoneNumber);

    }

    @Test
    void changePassword() {
        // As port number as it's generated randomly.
        String baseUrl = getBaseUrl();

        // new password
        String newPassword = "1234";

        ChangePasswordDto changePasswordDto = new ChangePasswordDto(newPassword);
        HttpHeaders headers = new HttpHeaders();

        // token value is assigned during setUp method
        headers.add("Authorization", "Bearer " + clientToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity request = new HttpEntity<>(changePasswordDto, headers);
        HttpStatusCode httpStatusCode = restTemplate.exchange(baseUrl + "/client-account-manager/password",
                HttpMethod.PUT, request, void.class).getStatusCode();

        assertThat(httpStatusCode).isNotNull();
        assertThat(httpStatusCode).isEqualTo(HttpStatus.OK);

        String token = login("1", newPassword, "http://localhost:" + port + "/api/login/client");

        assertThat(token).isNotNull();


    }

    @Test
    void deleteClientAccount() {
        // As port number as it's generated randomly.
        String baseUrl = getBaseUrl();

        HttpHeaders headers = new HttpHeaders();

        // token value is assigned during setUp method
        headers.add("Authorization", "Bearer " + ownerToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        DeleteAccountDto deleteAccountDto = new DeleteAccountDto(null, "1");
        HttpEntity request = new HttpEntity<>(deleteAccountDto,headers);
        HttpStatusCode responseStatusCode = restTemplate.exchange(baseUrl + "/client-account-manager",
                HttpMethod.DELETE, request, void.class).getStatusCode();

        assertThat(responseStatusCode).isNotNull().isEqualTo(HttpStatus.OK);
    }

    // utility method to encapsulate the login logic.
    String login(String username, String password, String url){

        // Create the basic auth request to the api/login/owner endpoint.
        String plainCredentials  = username + ":" + password;
        byte[] plainCredentialsBytes = plainCredentials.getBytes();

        // Encode the basic authentication request.
        byte[] base64CredentialsBytes = Base64.encodeBase64(plainCredentialsBytes);
        String base64Credentials= new String(base64CredentialsBytes);

        // Create the header object
        HttpHeaders basicAuthHeaders = new HttpHeaders();
        basicAuthHeaders.add("Authorization", "Basic " + base64Credentials);

        // Perform login to get the token.
        HttpEntity<String> basicAuthRequest = new HttpEntity<String>(basicAuthHeaders);
        String  token = restTemplate.postForObject(url, basicAuthRequest
                ,String.class);

        return token;
    }

    // utility method to get the base url
    String getBaseUrl(){
        String baseUrl = "http://localhost:" + port + "/api";
        return baseUrl;

    }

}