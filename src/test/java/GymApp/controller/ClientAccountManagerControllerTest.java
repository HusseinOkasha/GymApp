package GymApp.controller;

import GymApp.dto.*;
import GymApp.entity.Account;
import GymApp.entity.Client;
import GymApp.entity.Coach;
import GymApp.entity.Owner;
import GymApp.service.AccountService;
import GymApp.service.ClientService;
import GymApp.service.CoachService;
import GymApp.service.OwnerService;
import GymApp.util.ClientAccountManagerTestUtil;
import GymApp.util.CoachAccountManagerControllerTestUtil;
import GymApp.util.GeneralUtil;
import GymApp.util.entityAndDtoMappers.ClientMapper;
import com.github.dockerjava.zerodep.shaded.org.apache.commons.codec.binary.Base64;
import org.junit.jupiter.api.AfterEach;
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
import org.testcontainers.shaded.org.checkerframework.checker.units.qual.A;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = { "spring.datasource.url=jdbc:tc:postgres:latest:///db", "spring.sql.init.mode=always" })
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ClientAccountManagerControllerTest {
    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("db").withUsername("myuser");

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    private OwnerService ownerService;
    @Autowired
    private CoachService coachService;
    @Autowired
    private ClientService clientService;

    @LocalServerPort
    private int port; // holds the random port number.

    private static String ownerToken;  // holds token with owner scope.
    private static String coachToken;  // holds token with coach scope.
    private static String clientToken; // holds token with client scope.
    private static final String rawPassword = "123"; // hold raw password "123".
    // BCrypt password for raw password "123"
    private static final String bCryptPassword = "$2a$12$fdQCjXHktjZczz5hlHg77u8bIXUQdzGQf5k7ulN.cxzhW2vidHzSu";


    private static final Owner.Builder ownerBuilder = new Owner.Builder();
    private static final Coach.Builder coachBuilder = new Coach.Builder();
    private static final Client.Builder clientBuilder = new Client.Builder();
    private static final Account.Builder accountBuilder = new Account.Builder();

    private static Client client1 = clientBuilder.account(
            accountBuilder
                    .firstName("f1")
                    .secondName("s1")
                    .thirdName("t1")
                    .email("e1@gmail.com")
                    .phoneNumber("1")
                    .password(bCryptPassword)
                    .build()
    ).birthDate(LocalDate.of(2024, 5, 16)).build();

    private static final Client client2 = clientBuilder.account(
            accountBuilder
                    .firstName("f2")
                    .secondName("s2")
                    .thirdName("t2")
                    .email("e2@gmail.com")
                    .phoneNumber("2")
                    .password(bCryptPassword)
                    .build()
    ).birthDate(LocalDate.of(2024, 5, 16)).build();

    private static final Client client3 = clientBuilder.account(
            accountBuilder
                    .firstName("f3")
                    .secondName("s3")
                    .thirdName("t3")
                    .email("e3@gmail.com")
                    .phoneNumber("3")
                    .password(bCryptPassword)
                    .build()
    ).birthDate(LocalDate.of(2024, 5, 16)).build();

    private static final Coach coach = coachBuilder.account(
            accountBuilder
                    .firstName("f4")
                    .secondName("s4")
                    .thirdName("t4")
                    .email("e4@gmail.com")
                    .phoneNumber("4")
                    .password(bCryptPassword)
                    .build()
    ).build();

    private static final Owner owner = ownerBuilder.account(
            accountBuilder
                    .firstName("f5")
                    .secondName("s5")
                    .thirdName("t5")
                    .email("e5@gmail.com")
                    .phoneNumber("5")
                    .password(bCryptPassword)
                    .build()
    ).build();

    private ClientAccountManagerTestUtil testUtil;


    @BeforeEach
    void setUp() {
        // initialize the database.
        ownerService.save(owner);
        coachService.save(coach);
        clientService.save(client1);
        clientService.save(client2);

        // perform login to get token with scopes (owner, coach, and client) respectively.
        ownerToken = GeneralUtil.login(owner.getAccount().getEmail(), rawPassword,
                GeneralUtil.getBaseUrl(port) + "/login/owner", restTemplate);
        coachToken = GeneralUtil.login(coach.getAccount().getEmail(), rawPassword,
                GeneralUtil.getBaseUrl(port) + "/login/coach", restTemplate);
        clientToken = GeneralUtil.login(client1.getAccount().getEmail(), rawPassword,
                GeneralUtil.getBaseUrl(port) + "/login/client", restTemplate);

        // set up the util class
        testUtil = new ClientAccountManagerTestUtil(port, restTemplate);
    }
    @AfterEach
    void tearDown(){
        ownerService.deleteAll();
        coachService.deleteAll();
        clientService.deleteAll();
    }

    /*
    * Create New client account tests
    * */
    @Test
    void ownerShouldCreateNewClientAccount(){
        /*
        * Given access token with scope owner you should be able to create client account.
        * It checks that the response status code is 200 (Ok)
        * It checks that the returned Client account profile dto has the same data as the send in
        * create client account dto
        * Here I have chosen to use client3 as it isn't saved to the database in the setUp method.
        * */

        ClientAccountProfileDto expected = ClientMapper.clientEntityToClientAccountProfileDto(client3);
        ResponseEntity<ClientAccountProfileDto> response = testUtil.attemptCreateNewClientAccount(client3, ownerToken);

        ClientAccountProfileDto actual = response.getBody();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        // 1) I have Ignored the id from the comparison as id represents the database id which I couldn't predict
        // before saving the client to the database.
        // 2) I have Ignored fields of time "LocalDateTime" to avoid precision errors.
        assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFields("accountProfileDto.id")
                .ignoringFieldsOfTypes(LocalDateTime.class)
                .isEqualTo(expected);

    }
    @Test
    void coachAndClientShouldNotCreateNewClientAccount(){
        /*
        * Given access token with scope coach or client you should not be able to create client accounts.
        * It checks that the response status code is 403 (FORBIDDEN).
        * */
        ResponseEntity<ClientAccountProfileDto> response = testUtil.attemptCreateNewClientAccount(client3, coachToken);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);

        response = testUtil.attemptCreateNewClientAccount(client3, clientToken);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }
    @Test
    void shouldNotCreateNewClientAccountWithoutRequiredFields(){
       /*
       * It checks that you can't create client account without required fields.
       * Required fields are: firstName, secondName, thirdName, email, phoneNumber, password and birthdate
       * It checks that the response status code is 400 (BAD_REQUEST).
       * Here I have chosen to use client3 as it isn't saved to the database in the setUp method.
       * */

        // without firstName
        Client sampleClient = clientBuilder.copyFrom(client3).build();
        sampleClient.getAccount().setFirstName(null);
        testUtil.shouldNotCreateNewClientAccountWithoutRequiredFields(sampleClient, ownerToken);

        // without secondName
        sampleClient = clientBuilder.copyFrom(client3).build();
        sampleClient.getAccount().setSecondName(null);
        testUtil.shouldNotCreateNewClientAccountWithoutRequiredFields(sampleClient, ownerToken);

        // without thirdName
        sampleClient = clientBuilder.copyFrom(client3).build();
        sampleClient.getAccount().setThirdName(null);
        testUtil.shouldNotCreateNewClientAccountWithoutRequiredFields(sampleClient, ownerToken);

        // without email
        sampleClient = clientBuilder.copyFrom(client3).build();
        sampleClient.getAccount().setEmail(null);
        testUtil.shouldNotCreateNewClientAccountWithoutRequiredFields(sampleClient, ownerToken);

        // without phone number
        sampleClient = clientBuilder.copyFrom(client3).build();
        sampleClient.getAccount().setPhoneNumber(null);
        testUtil.shouldNotCreateNewClientAccountWithoutRequiredFields(sampleClient, ownerToken);

        // without password
        sampleClient = clientBuilder.copyFrom(client3).build();
        sampleClient.getAccount().setPassword(null);
        testUtil.shouldNotCreateNewClientAccountWithoutRequiredFields(sampleClient, ownerToken);

        // without birthdate
        sampleClient = clientBuilder.copyFrom(client3).build();
        sampleClient.setBirthDate(null);
        testUtil.shouldNotCreateNewClientAccountWithoutRequiredFields(sampleClient, ownerToken);

    }
    @Test
    void shouldNotCreateNewClientAccountWithoutAccessToken(){
        /*
        * It checks that you can't create client account without access token.
        * It checks that the response status code is 401 (UNAUTHORIZED).
        * I have used client3 as it's not saved to the database in the setUp method.
        * */
        ResponseEntity<ClientAccountProfileDto> response = testUtil.attemptCreateNewClientAccount(client3, null);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    /*
    * Get My account tests.
    * */
    @Test
    void clientShouldGetHisOwnAccountDetails(){
        /*
        * Given access token with scope client, you should be able to get the account details of the client to whom
        * the access token belongs.
        * It checks that the response status code is 200 (OK).
        * It checks that the return client account profile dto contains the correct data.
        * */
        ClientAccountProfileDto expected = ClientMapper.clientEntityToClientAccountProfileDto(client1);

        ResponseEntity<ClientAccountProfileDto> response = testUtil.attemptGetMyAccountDetails(client1, clientToken);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        ClientAccountProfileDto actual = response.getBody();


        // 1) I have Ignored the id from the comparison as id represents the database id which I couldn't predict
        // before saving the client to the database.
        // 2) I have Ignored fields of time "LocalDateTime" to avoid precision errors.
        assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFieldsOfTypes(LocalDateTime.class)
                .ignoringFields("accountProfileDto.id")
                .isEqualTo(expected);
    }
    @Test
    void ownerAndCoachShouldNotGetClientAccountDetails(){
        /*
        * Given access token with scope owner or coach you should not get client account details.
        * It checks that the response status code is 403 (FORBIDDEN).
        * */

        // Check that owner can't
        ResponseEntity<ClientAccountProfileDto> response = testUtil.attemptGetMyAccountDetails(client1, ownerToken);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);

        // Check that coach can't
        response = testUtil.attemptGetMyAccountDetails(client1, coachToken);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }
    @Test
    void shouldNotGetClientAccountDetailsWithoutAccessToken(){
        /*
        * It checks that you can't get the client account details without access token.
        * It checks that the response status code is 401 (UNAUTHORIZED).
        * */
        ResponseEntity<ClientAccountProfileDto> response = testUtil.attemptGetMyAccountDetails(client1, null);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    /*
    * Get All clients tests.
    * */
    @Test
    void ownerAndCoachShouldGetAllClients(){
        /*
        * Given access token with scope (owner / coach) you should be able to get all client accounts.
        * It checks that the response status code is 200 (OK).
        * It checks that the returned client accounts are the same as the ones saved to the database in the setUp method.
        * Here I have used client1 & client2 in the expected as they are already saved to the database in the setUp method.
        * */
        List<Client> clients = List.of(client1, client2);
        testUtil.shouldGetAllClients(ownerToken, clients);
        testUtil.shouldGetAllClients(coachToken, clients);
    }
    @Test
    void clientShouldNotGetAllClients(){
        /*
        * Given access token with scope client you shouldn't be able to get all client accounts.
        * It checks that the response status code is 403 (FORBIDDEN).
        * */
        ResponseEntity<List<ClientAccountProfileDto>> response = testUtil.attemptGetAllClients(clientToken);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);

    }
    @Test
    void shouldNotGetAllClientsWithoutAccessToken(){
        /*
        * Given no access token you should not be able to get all clients.
        * It checks that the response status code is 401 (UNAUTHORIZED)
        * */
        ResponseEntity<List<ClientAccountProfileDto>> response = testUtil.attemptGetAllClients(null);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);

    }

    /*
    * update client account tests
    * */
    @Test
    void clientShouldUpdateHisOwnAccount(){
        /*
        * Given access token with scope client you should be able to update the client to which the access token belongs.
        * It checks that the response status code is 200 (OK).
        * It checks that the returned clientAccountProfileDto in the response body is the same as
        * the one sent in the request body.
        * */

        // update firstName, secondName, thirdName, email, password, and phone number
        client1 = testUtil.performUpdatesOnClientAccount(client1);

        // expected
        ClientAccountProfileDto expected = ClientMapper.clientEntityToClientAccountProfileDto(client1);

        ResponseEntity<ClientAccountProfileDto> response = testUtil.attemptUpdateClientAccount(client1, clientToken);

        // actual
        ClientAccountProfileDto actual = response.getBody();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        //I have Ignored fields of time "LocalDateTime" to avoid precision errors.
        assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFieldsOfTypes(LocalDateTime.class)
                .isEqualTo(expected);

    }
    @Test
    void ownerAndCoachShouldNotUpdateClientAccount(){
        /*
        * Given access toke with scope (coach / owner), you shouldn't be able to update client account.
        * It checks that the response status code is 403 (FORBIDDEN).
        * */

        client1 = testUtil.performUpdatesOnClientAccount(client1);

        // checks that owner can't update client account.
        ResponseEntity<ClientAccountProfileDto> response = testUtil.attemptUpdateClientAccount(client1, ownerToken);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);

        // checks that coach can't update client account.
        response = testUtil.attemptUpdateClientAccount(client1, coachToken);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }
    @Test
    void shouldNotUpdateClientAccountWithoutRequiredFields(){
        /*
         * It checks that you can't create client account without required fields.
         * Required fields are: firstName, secondName, thirdName, email, phoneNumber, password and birthdate
         * It checks that the response status code is 400 (BAD_REQUEST).
         * Here I have chosen client1 as it is already saved to the database in the setUp method and clientToken belongs to it.
         */

        // without firstName
        Client sampleClient = clientBuilder.copyFrom(client1).build();
        sampleClient.getAccount().setFirstName(null);
        ResponseEntity<ClientAccountProfileDto> response = testUtil.attemptUpdateClientAccount(sampleClient, clientToken);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        // without secondName
        sampleClient = clientBuilder.copyFrom(client1).build();
        sampleClient.getAccount().setSecondName(null);
        response = testUtil.attemptUpdateClientAccount(sampleClient, clientToken);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        // without thirdName
        sampleClient = clientBuilder.copyFrom(client1).build();
        sampleClient.getAccount().setThirdName(null);
        response = testUtil.attemptUpdateClientAccount(sampleClient, clientToken);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        // without email
        sampleClient = clientBuilder.copyFrom(client1).build();
        sampleClient.getAccount().setEmail(null);
        response = testUtil.attemptUpdateClientAccount(sampleClient, clientToken);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);


        // without phone number
        sampleClient = clientBuilder.copyFrom(client1).build();
        sampleClient.getAccount().setPhoneNumber(null);
        response = testUtil.attemptUpdateClientAccount(sampleClient, clientToken);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        // without birthdate
        sampleClient = clientBuilder.copyFrom(client1).build();
        sampleClient.setBirthDate(null);
        response = testUtil.attemptUpdateClientAccount(sampleClient, clientToken);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

    }
    @Test
    void shouldNotUpdateClientAccountWithoutAccessToken(){
        /*
        * It checks that without access token you can't update client account.
        * It checks that the response status code is 400 (BAD_REQUEST).
        * */
        ResponseEntity<ClientAccountProfileDto> response = testUtil.attemptUpdateClientAccount(client1, null);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    /*
    * change client account password tests.
    * */
    @Test
    void clientShouldChangeHisOwnPassword(){
        /*
        * Given access token with scope client, you should be able to change the password
        * for the client to which the access token belongs.
        * It checks that the response status code is 200 (OK).
        * */
        ResponseEntity<String> response = testUtil.attemptChangeClientAccountPassword("1234", clientToken);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
    @Test
    void ownerAndCoachShouldNotChangeClientPassword(){
        /*
         * Given access token with scope owner / coach, you should not be able to change the password
            for the client to which the access token belongs.
         * It checks that the response status code is 403 (FORBIDDEN).
         * */
        ResponseEntity<String> response = testUtil.attemptChangeClientAccountPassword("1234", ownerToken);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);

        response = testUtil.attemptChangeClientAccountPassword("1234", coachToken);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }
    @Test
    void shouldNotChangeClientPasswordWithEmptyPassword(){
        /*
         * Given access token with scope client, you should not be able to change the password
            for the client to which the access token belongs by empty password.
         * It checks that the response status code is 400 (BAD_REQUEST).
         * */
        ResponseEntity<String> response = testUtil.attemptChangeClientAccountPassword("", clientToken);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
    @Test
    void shouldNotChangeClientPasswordWithoutAccessToken(){
        /*
         * You should not be able to change the password of client account without access token.
         * It checks that the response status code is 401 (UNAUTHORIZED).
         * */
        ResponseEntity<String> response = testUtil.attemptChangeClientAccountPassword("1234", null);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    /*
    * Delete Client account tests
    * */
    @Test
    void ownerShouldDeleteClientAccount(){
        /*
        * Given access token of scope owner and a DeleteAccountDto you should be able to delete client account.
        * DeleteAccountDto: contains the email and phone number of the account you want to delete.
        * It checks that the response status code is 200 (Ok).
        * In addition to checking that you can delete client account with email and/or phone number.
        * Here I have used client1 as it's already saved to the database in the setUp method.
        * */

        // tests deletion using email and phone number.
        testUtil.shouldDeleteClientAccount(
                new DeleteAccountDto(client1.getAccount().getEmail(), client1.getAccount().getPhoneNumber()),
                ownerToken);

        // tests deletion using email only.
        testUtil.shouldDeleteClientAccount(
                new DeleteAccountDto(client1.getAccount().getEmail(), null),
                ownerToken);

        // tests deletion using phone number only.
        testUtil.shouldDeleteClientAccount(
                new DeleteAccountDto(null, client1.getAccount().getPhoneNumber()),
                ownerToken);

    }
    @Test
    void clientAndCoachShouldNotDeleteClientAccount(){
        /*
         * Given access token of scope (coach / client) and a DeleteAccountDto you should Not
            be able to delete client account.
         * DeleteAccountDto: contains the email and phone number of the account you want to delete.
         * It checks that the response status code is 403 (FORBIDDEN).
         * Here I have used client1 as it's already saved to the database in the setUp method.
         * */


        // checks that coach can't delete client account.
        testUtil.coachAndClientShouldNotDeleteClientAccount(
                new DeleteAccountDto(client1.getAccount().getEmail(), client1.getAccount().getPhoneNumber()),
                coachToken);

        // checks that client can't delete client account.
        testUtil.coachAndClientShouldNotDeleteClientAccount(
                new DeleteAccountDto(client1.getAccount().getEmail(), client1.getAccount().getPhoneNumber()),
                clientToken);

    }
    @Test
    void shouldNotDeleteClientAccountWithoutAccessToken(){
        /*
        * It checks that you can't delete client account without access token.
        * It checks that the response status code is 401 (UNAUTHORIZED).
        * Here I have used client1 as it's already saved to the database in the setUp method.
        * */

        ResponseEntity<String> response = testUtil.attemptDeleteClientAccount(
                new DeleteAccountDto(client1.getAccount().getEmail(), client1.getAccount().getPhoneNumber()), null);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }
}