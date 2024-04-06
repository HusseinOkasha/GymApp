package GymApp.controller;

import GymApp.dto.AccountProfileDto;


import GymApp.dto.ChangePasswordDto;
import GymApp.dto.CreateAccountDto;
import GymApp.entity.Account;
import GymApp.entity.Owner;
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


import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;


import java.util.ArrayList;
import java.util.List;


import static org.assertj.core.api.Assertions.assertThat;


@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = { "spring.datasource.url=jdbc:tc:postgres:latest:///database", "spring.sql.init.mode=always" })
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class OwnerAccountManagerControllerTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("database").withUsername("myuser");

    private static String token;
    private static List<Account> accounts = new ArrayList<>();
    @Autowired
    RestTemplate restTemplate;
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

        // add newly created accounts to the accounts list.
        accounts.add(acc1);
        accounts.add(acc2);


    }
    @BeforeEach
    void setUp() {
        // delete all owners to start fresh.
        ownerService.deleteAll();


        // Make them owner accounts.
        Owner owner1 = new Owner(new Account(accounts.get(0)));
        Owner owner2 = new Owner(new Account(accounts.get(1)));
        ownerService.save(owner1);
        ownerService.save(owner2);

        token = login("1", "123",
                getBaseUrl()+ "/login/owner");
    }

    @Test
    void postgresContainerShouldBeRunning (){
        assertThat(postgres.isRunning()).isTrue();
    }

    @Test
    void shouldCreateOwnerAccount() {
        // As port number as it's generated randomly.
        String baseUrl = getBaseUrl();

        // Sending request associated with the token to get accountProfileDto for the owner embedded in the token
        HttpHeaders headers = new HttpHeaders();

        // token value is assigned during setUp method
        headers.add("Authorization", "Bearer " + token);

        // new owner account values
        String firstName = "f3";
        String secondName = "s3";
        String thirdName = "t3";
        String email = "e3@gmail.com";
        String phoneNumber = "3";
        String password = "123";

        // initalize the dto
        CreateAccountDto createAccountDto = new CreateAccountDto(firstName, secondName, thirdName, email, phoneNumber,
                password);

        HttpEntity<CreateAccountDto> request = new HttpEntity<>(createAccountDto, headers);

        AccountProfileDto createdAccount = restTemplate.exchange(baseUrl + "/owner-account-manager",
                HttpMethod.POST, request, AccountProfileDto.class).getBody();

        assertThat(createdAccount).isNotNull();
        assertThat(createdAccount.firstName()).isEqualTo(firstName);
        assertThat(createdAccount.SecondName()).isEqualTo(secondName);
        assertThat(createdAccount.thirdName()).isEqualTo(thirdName);
        assertThat(createdAccount.email()).isEqualTo(email);
        assertThat(createdAccount.phoneNumber()).isEqualTo(phoneNumber);

    }

    @Test
    void shouldNotCreateOwnerAccountWithoutFirstName(){
        // As port number as it's generated randomly.
        String baseUrl = getBaseUrl();

        // Sending request associated with the token to get accountProfileDto for the owner embedded in the token
        HttpHeaders headers = new HttpHeaders();

        // token value is assigned during setUp method
        headers.add("Authorization", "Bearer " + token);

        // initialize the dto
        CreateAccountDto createAccountDto = new CreateAccountDto(null, "s3", "t3", "e3",
                "3", "123");

        HttpEntity<CreateAccountDto> request = new HttpEntity<>(createAccountDto, headers);
        try{
            HttpStatusCode responseCode = restTemplate.exchange(baseUrl + "/owner-account-manager",
                    HttpMethod.POST, request, HttpStatusCode.class).getStatusCode();
        }
        catch (HttpClientErrorException e){
            assertThat(e.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }


    }
    @Test
    void shouldNotCreateOwnerAccountWithoutSecondName(){
        // As port number as it's generated randomly.
        String baseUrl = getBaseUrl();

        // Sending request associated with the token to get accountProfileDto for the owner embedded in the token
        HttpHeaders headers = new HttpHeaders();

        // token value is assigned during setUp method
        headers.add("Authorization", "Bearer " + token);

        // initialize the dto
        CreateAccountDto createAccountDto = new CreateAccountDto("f3", null, "t3", "e3",
                "3", "123");

        HttpEntity<CreateAccountDto> request = new HttpEntity<>(createAccountDto, headers);
        try{
            HttpStatusCode responseCode = restTemplate.exchange(baseUrl + "/owner-account-manager",
                    HttpMethod.POST, request, HttpStatusCode.class).getStatusCode();
        }
        catch (HttpClientErrorException e){
            assertThat(e.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }


    }
    @Test
    void shouldNotCreateOwnerAccountWithoutThirdName(){
        // As port number as it's generated randomly.
        String baseUrl = getBaseUrl();

        // Sending request associated with the token to get accountProfileDto for the owner embedded in the token
        HttpHeaders headers = new HttpHeaders();

        // token value is assigned during setUp method
        headers.add("Authorization", "Bearer " + token);

        // initialize the dto
        CreateAccountDto createAccountDto = new CreateAccountDto("f3", "s2", null, "e3",
                "3", "123");

        HttpEntity<CreateAccountDto> request = new HttpEntity<>(createAccountDto, headers);
        try{
            HttpStatusCode responseCode = restTemplate.exchange(baseUrl + "/owner-account-manager",
                    HttpMethod.POST, request, HttpStatusCode.class).getStatusCode();
        }
        catch (HttpClientErrorException e){
            assertThat(e.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }


    }
    @Test
    void shouldNotCreateOwnerAccountWithoutEmail(){
        // As port number as it's generated randomly.
        String baseUrl = getBaseUrl();

        // Sending request associated with the token to get accountProfileDto for the owner embedded in the token
        HttpHeaders headers = new HttpHeaders();

        // token value is assigned during setUp method
        headers.add("Authorization", "Bearer " + token);

        // initialize the dto
        CreateAccountDto createAccountDto = new CreateAccountDto("f3", "s2", "t3", null,
                "3", "123");

        HttpEntity<CreateAccountDto> request = new HttpEntity<>(createAccountDto, headers);
        try{
            HttpStatusCode responseCode = restTemplate.exchange(baseUrl + "/owner-account-manager",
                    HttpMethod.POST, request, HttpStatusCode.class).getStatusCode();
        }
        catch (HttpClientErrorException e){
            assertThat(e.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }


    }
    @Test
    void shouldNotCreateOwnerAccountWithoutPhoneNumber(){
        // As port number as it's generated randomly.
        String baseUrl = getBaseUrl();

        // Sending request associated with the token to get accountProfileDto for the owner embedded in the token
        HttpHeaders headers = new HttpHeaders();

        // token value is assigned during setUp method
        headers.add("Authorization", "Bearer " + token);

        // initialize the dto
        CreateAccountDto createAccountDto = new CreateAccountDto("f3", "s2", "t3", "e3@gmail.com",
                null, "123");

        HttpEntity<CreateAccountDto> request = new HttpEntity<>(createAccountDto, headers);
        try{
            HttpStatusCode responseCode = restTemplate.exchange(baseUrl + "/owner-account-manager",
                    HttpMethod.POST, request, HttpStatusCode.class).getStatusCode();
        }
        catch (HttpClientErrorException e){
            assertThat(e.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }


    }


    @Test
    void shouldNotCreateOwnerAccountWithoutPassword(){
        // As port number as it's generated randomly.
        String baseUrl = getBaseUrl();

        // Sending request associated with the token to get accountProfileDto for the owner embedded in the token
        HttpHeaders headers = new HttpHeaders();

        // token value is assigned during setUp method
        headers.add("Authorization", "Bearer " + token);

        // initialize the dto
        CreateAccountDto createAccountDto = new CreateAccountDto("f3", "s3", "t3", "e3",
                "3", null);

        HttpEntity<CreateAccountDto> request = new HttpEntity<>(createAccountDto, headers);
        try{
            HttpStatusCode responseCode = restTemplate.exchange(baseUrl + "/owner-account-manager",
                    HttpMethod.POST, request, HttpStatusCode.class).getStatusCode();
        }
        catch (HttpClientErrorException e){
            assertThat(e.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }


    }

    @Test
    void shouldGetOwner() {
        // As port number as it's generated randomly.
        String baseUrl = getBaseUrl();

        // Sending request associated with the token to get accountProfileDto for the owner embedded in the token
        HttpHeaders headers = new HttpHeaders();

        // token value is assigned during setUp method
        headers.add("Authorization", "Bearer " + token);
        HttpEntity<String> request = new HttpEntity<String>(headers);
        AccountProfileDto ownerAccount = restTemplate.exchange(baseUrl + "/owner-account-manager",
                HttpMethod.GET, request, AccountProfileDto.class).getBody();

        assertThat(ownerAccount).isNotNull();
        assertThat(ownerAccount.email()).isNotNull().isEqualTo("e1@gmail.com");
    }


    @Test
    void shouldGetAllOwners() {
        // As port number as it's generated randomly.
        String baseUrl = getBaseUrl() ;

        // Sending request associated with the token to get a list of all owners.
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token);

        HttpEntity<String> request = new HttpEntity<String>(headers);
        ParameterizedTypeReference<List<AccountProfileDto>>
                responseType = new ParameterizedTypeReference<List<AccountProfileDto>>() {};
        List<AccountProfileDto> allOwnerAccounts = restTemplate.exchange(baseUrl + "/owner-account-manager/owners",
                HttpMethod.GET, request, responseType).getBody();

        // Check that there is 2 owner accounts returned
        assertThat(allOwnerAccounts).isNotNull();
        assertThat(allOwnerAccounts.size()).isEqualTo(2);

        // check that the owners returned are the same ones which we inserted by the checking their emails.
        List<String>possibleEmails = List.of(accounts.get(0).getEmail(), accounts.get(1).getEmail());
        assertThat(allOwnerAccounts.get(0).email()).isIn(possibleEmails);
        assertThat(allOwnerAccounts.get(1).email()).isIn(possibleEmails);
    }



    @Test
    void shouldUpdateOwnerAccount() {
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
        headers.add("Authorization", "Bearer " + token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<AccountProfileDto> request = new HttpEntity<>(accountProfileDto, headers);
        AccountProfileDto updatedAccountProfileDto = restTemplate.exchange(baseUrl + "/owner-account-manager",
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
    void shouldNotUpdateOwnerAccountWithoutFirstName() {
        // As port number as it's generated randomly.
        String baseUrl = getBaseUrl();


        AccountProfileDto accountProfileDto = new AccountProfileDto(0,null, "updatedSecondName",
                "updatedThirdName","updatedE1@gmail.com", "updatedPhoneNumber", null, null);


        HttpHeaders headers = new HttpHeaders();

        // token value is assigned during setUp method
        headers.add("Authorization", "Bearer " + token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<AccountProfileDto> request = new HttpEntity<>(accountProfileDto, headers);
        try{
            AccountProfileDto updatedAccountProfileDto = restTemplate.exchange(baseUrl + "/owner-account-manager",
                    HttpMethod.PUT, request, AccountProfileDto.class).getBody();
        }
        catch (HttpClientErrorException e){
            assertThat(e.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }
    }

    @Test
    void shouldNotUpdateOwnerAccountWithoutSecondName() {
        // As port number as it's generated randomly.
        String baseUrl = getBaseUrl();


        AccountProfileDto accountProfileDto = new AccountProfileDto(0,"updatedFirstName", null,
                "updatedThirdName","updatedE1@gmail.com", "updatedPhoneNumber", null, null);


        HttpHeaders headers = new HttpHeaders();

        // token value is assigned during setUp method
        headers.add("Authorization", "Bearer " + token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<AccountProfileDto> request = new HttpEntity<>(accountProfileDto, headers);
        try{
            AccountProfileDto updatedAccountProfileDto = restTemplate.exchange(baseUrl + "/owner-account-manager",
                    HttpMethod.PUT, request, AccountProfileDto.class).getBody();
        }
        catch (HttpClientErrorException e){
            assertThat(e.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }
    }
    @Test
    void shouldNotUpdateOwnerAccountWithoutThirdName() {
        // As port number as it's generated randomly.
        String baseUrl = getBaseUrl();


        AccountProfileDto accountProfileDto = new AccountProfileDto(0,"updatedFirstName", "updatedSecondName",
                null,"updatedE1@gmail.com", "updatedPhoneNumber", null, null);


        HttpHeaders headers = new HttpHeaders();

        // token value is assigned during setUp method
        headers.add("Authorization", "Bearer " + token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<AccountProfileDto> request = new HttpEntity<>(accountProfileDto, headers);
        try{
            AccountProfileDto updatedAccountProfileDto = restTemplate.exchange(baseUrl + "/owner-account-manager",
                    HttpMethod.PUT, request, AccountProfileDto.class).getBody();
        }
        catch (HttpClientErrorException e){
            assertThat(e.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }
    }

    @Test
    void shouldNotUpdateOwnerAccountWithoutEmail() {
        // As port number as it's generated randomly.
        String baseUrl = getBaseUrl();


        AccountProfileDto accountProfileDto = new AccountProfileDto(0,"updatedFirstName", "updatedSecondName",
                "updatedThirdName",null, "updatedPhoneNumber", null, null);


        HttpHeaders headers = new HttpHeaders();

        // token value is assigned during setUp method
        headers.add("Authorization", "Bearer " + token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<AccountProfileDto> request = new HttpEntity<>(accountProfileDto, headers);
        try{
            AccountProfileDto updatedAccountProfileDto = restTemplate.exchange(baseUrl + "/owner-account-manager",
                    HttpMethod.PUT, request, AccountProfileDto.class).getBody();
        }
        catch (HttpClientErrorException e){
            assertThat(e.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }
    }
    @Test
    void shouldNotUpdateOwnerAccountWithoutPhoneNumber() {
        // As port number as it's generated randomly.
        String baseUrl = getBaseUrl();


        AccountProfileDto accountProfileDto = new AccountProfileDto(0,"updatedFirstName", "updatedSecondName",
                "updatedThirdName","updatedE1@gmail.com", null, null, null);


        HttpHeaders headers = new HttpHeaders();

        // token value is assigned during setUp method
        headers.add("Authorization", "Bearer " + token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<AccountProfileDto> request = new HttpEntity<>(accountProfileDto, headers);
        try{
            AccountProfileDto updatedAccountProfileDto = restTemplate.exchange(baseUrl + "/owner-account-manager",
                    HttpMethod.PUT, request, AccountProfileDto.class).getBody();
        }
        catch (HttpClientErrorException e){
            assertThat(e.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }
    }

    @Test
    void shouldChangeOwnerAccountPassword() {
        // As port number as it's generated randomly.
        String baseUrl = getBaseUrl();


        // new password
        String newPassword = "1234";

        ChangePasswordDto changePasswordDto = new ChangePasswordDto(newPassword);
        HttpHeaders headers = new HttpHeaders();

        // token value is assigned during setUp method
        headers.add("Authorization", "Bearer " + token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity request = new HttpEntity<>(changePasswordDto, headers);
        HttpStatusCode httpStatusCode = restTemplate.exchange(baseUrl + "/owner-account-manager/password",
                HttpMethod.PUT, request, void.class).getStatusCode();

        assertThat(httpStatusCode).isNotNull();
        assertThat(httpStatusCode).isEqualTo(HttpStatus.OK);

        String token = login("1", newPassword, "http://localhost:" + port + "/api/login/owner");

        assertThat(token).isNotNull();

    }
    @Test
    void shouldNotUpdateOwnerAccountPasswordWithoutTheNewPassword() {
        // As port number as it's generated randomly.
        String baseUrl = getBaseUrl();

        // new password
        String newPassword = null;

        ChangePasswordDto changePasswordDto = new ChangePasswordDto(newPassword);
        HttpHeaders headers = new HttpHeaders();

        // token value is assigned during setUp method
        headers.add("Authorization", "Bearer " + token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity request = new HttpEntity<>(changePasswordDto, headers);

        try{
            HttpStatusCode httpStatusCode = restTemplate.exchange(baseUrl + "/owner-account-manager/password",
                    HttpMethod.PUT, request, void.class).getStatusCode();
        }
        catch (HttpClientErrorException e){
            assertThat(e.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }
        
    }

    @Test
    void shouldDeleteOwnerAccount() {
        // As port number as it's generated randomly.
        String baseUrl = getBaseUrl();


                HttpHeaders headers = new HttpHeaders();

        // token value is assigned during setUp method
        headers.add("Authorization", "Bearer " + token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity request = new HttpEntity<>(headers);
        HttpStatusCode responseStatusCode = restTemplate.exchange(baseUrl + "/owner-account-manager",
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