package GymApp.controller;

import GymApp.dto.AccountProfileDto;
import GymApp.dto.ChangePasswordDto;
import GymApp.dto.CreateAccountDto;
import GymApp.dto.DeleteAccountDto;
import GymApp.entity.Account;
import GymApp.entity.Coach;
import GymApp.entity.Owner;
import GymApp.service.CoachService;

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

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = { "spring.datasource.url=jdbc:tc:postgres:latest:///database", "spring.sql.init.mode=always" })
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CoachAccountManagerControllerTest {
    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("database").withUsername("myuser");

    private static String ownerToken;
    private static String coachToken;
    private static List<Account> ownerAccounts = new ArrayList<>();
    private static List<Account> coachAccounts = new ArrayList<>();
    @Autowired
    RestTemplate restTemplate;
    @Autowired
    CoachService coachService;

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

        // add newly created accounts to the coach accounts list.
        coachAccounts.add(acc1);
        coachAccounts.add(acc2);

        // add the owner account the owner account list
        ownerAccounts.add(acc3);

    }

    @BeforeEach
    void setUp() {
        // delete all coaches & owners to start fresh.
        coachService.deleteAll();
        ownerService.deleteAll();

        // Make them coach accounts.
        Coach coach1 = new Coach(new Account(coachAccounts.get(0)));
        Coach coach2 = new Coach(new Account(coachAccounts.get(1)));
        coachService.save(coach1);
        coachService.save(coach2);

        // create owner account.
        ownerService.save(new Owner(new Account(ownerAccounts.get(0))));

        // get token as a result of coach login
        coachToken = login("1", "123",
                getBaseUrl()+ "/login/coach");

        // get token as a result of owner login.
        ownerToken = login("3", "123",
                getBaseUrl()+ "/login/owner");
    }

    @Test
    void shouldCreateNewCoachAccount() {
        // As port number as it's generated randomly.
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

        // initalize the dto
        CreateAccountDto createAccountDto = new CreateAccountDto(firstName, secondName, thirdName, email, phoneNumber,
                password);

        HttpEntity<CreateAccountDto> request = new HttpEntity<>(createAccountDto, headers);

        AccountProfileDto createdAccount = restTemplate.exchange(baseUrl + "/coach-account-manager",
                HttpMethod.POST, request, AccountProfileDto.class).getBody();

        assertThat(createdAccount).isNotNull();
        assertThat(createdAccount.firstName()).isEqualTo(firstName);
        assertThat(createdAccount.SecondName()).isEqualTo(secondName);
        assertThat(createdAccount.thirdName()).isEqualTo(thirdName);
        assertThat(createdAccount.email()).isEqualTo(email);
        assertThat(createdAccount.phoneNumber()).isEqualTo(phoneNumber);

    }
    @Test
    void shouldGetMyAccount() {

        // As port number as it's generated randomly.
        String baseUrl = getBaseUrl();

        // Sending request associated with the token to get accountProfileDto for the owner embedded in the token
        HttpHeaders headers = new HttpHeaders();

        // token value is assigned during setUp method
        headers.add("Authorization", "Bearer " + coachToken);
        HttpEntity<String> request = new HttpEntity<String>(headers);
        AccountProfileDto coachAccount = restTemplate.exchange(baseUrl + "/coach-account-manager",
                HttpMethod.GET, request, AccountProfileDto.class).getBody();

        assertThat(coachAccount).isNotNull();
        assertThat(coachAccount.email()).isNotNull().isEqualTo("e1@gmail.com");
    }

    @Test
    void shouldGetAllCoachAccounts() {
        // As port number as it's generated randomly.
        String baseUrl = getBaseUrl() ;

        // Sending request associated with the token to get a list of all coaches.
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + ownerToken);

        HttpEntity<String> request = new HttpEntity<String>(headers);
        ParameterizedTypeReference<List<AccountProfileDto>>
                responseType = new ParameterizedTypeReference<List<AccountProfileDto>>() {};
        List<AccountProfileDto> allCoachAccounts = restTemplate.exchange(baseUrl + "/coach-account-manager/coaches",
                HttpMethod.GET, request, responseType).getBody();

        // Check that there is 2 owner accounts returned
        assertThat(allCoachAccounts).isNotNull();
        assertThat(allCoachAccounts.size()).isEqualTo(2);

        // check that the owners returned are the same ones which we inserted by the checking their emails.
        List<String>possibleEmails = List.of(coachAccounts.get(0).getEmail(), coachAccounts.get(1).getEmail());
        assertThat(allCoachAccounts.get(0).email()).isIn(possibleEmails);
        assertThat(allCoachAccounts.get(1).email()).isIn(possibleEmails);

    }

    @Test
    void shouldUpdateMyProfile() {
        // As port number as it's generated randomly.
        String baseUrl = getBaseUrl();

        // new values to update owner account.
        String updatedFirstName = "updatedFirstName";
        String updatedSecondName = "updatedSecondName";
        String updatedThirdName = "updatedThirdName";
        String updatedEmail = "updatedE1@gmail.com";
        String updatedPhoneNumber = "updatedPhoneNumber";

        AccountProfileDto accountProfileDto = new AccountProfileDto(0,updatedFirstName, updatedSecondName,
                updatedThirdName,updatedEmail, updatedPhoneNumber, null, null);


        HttpHeaders headers = new HttpHeaders();

        // token value is assigned during setUp method
        headers.add("Authorization", "Bearer " + coachToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<AccountProfileDto> request = new HttpEntity<>(accountProfileDto, headers);
        AccountProfileDto updatedAccountProfileDto = restTemplate.exchange(baseUrl + "/coach-account-manager",
                HttpMethod.PUT, request, AccountProfileDto.class).getBody();


        // check that all updates are reflected.
        assertThat(updatedAccountProfileDto).isNotNull();
        assertThat(updatedAccountProfileDto.firstName()).isEqualTo(updatedFirstName);
        assertThat(updatedAccountProfileDto.SecondName()).isEqualTo(updatedSecondName);
        assertThat(updatedAccountProfileDto.thirdName()).isEqualTo(updatedThirdName);
        assertThat(updatedAccountProfileDto.email()).isEqualTo(updatedEmail);
        assertThat(updatedAccountProfileDto.phoneNumber()).isEqualTo(updatedPhoneNumber);

    }

    @Test
    void shouldChangePassword() {

        // As port number as it's generated randomly.
        String baseUrl = getBaseUrl();

        // new password
        String newPassword = "1234";

        ChangePasswordDto changePasswordDto = new ChangePasswordDto(newPassword);
        HttpHeaders headers = new HttpHeaders();

        // token value is assigned during setUp method
        headers.add("Authorization", "Bearer " + coachToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity request = new HttpEntity<>(changePasswordDto, headers);
        HttpStatusCode httpStatusCode = restTemplate.exchange(baseUrl + "/coach-account-manager/password",
                HttpMethod.PUT, request, void.class).getStatusCode();

        assertThat(httpStatusCode).isNotNull();
        assertThat(httpStatusCode).isEqualTo(HttpStatus.OK);

        String token = login("1", newPassword, "http://localhost:" + port + "/api/login/coach");

        assertThat(token).isNotNull();

    }

    @Test
    void deleteCoachAccount() {
        // As port number as it's generated randomly.
        String baseUrl = getBaseUrl();

        HttpHeaders headers = new HttpHeaders();

        // token value is assigned during setUp method
        headers.add("Authorization", "Bearer " + ownerToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        DeleteAccountDto deleteAccountDto = new DeleteAccountDto(null, "1");
        HttpEntity request = new HttpEntity<>(deleteAccountDto,headers);
        HttpStatusCode responseStatusCode = restTemplate.exchange(baseUrl + "/coach-account-manager",
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