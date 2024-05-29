package GymApp.controller;

import GymApp.dto.AccountProfileDto;
import GymApp.dto.ChangePasswordDto;
import GymApp.dto.CreateAccountDto;
import GymApp.dto.DeleteAccountDto;
import GymApp.entity.Account;
import GymApp.entity.Client;
import GymApp.entity.Coach;
import GymApp.entity.Owner;
import GymApp.service.ClientService;
import GymApp.service.CoachService;

import GymApp.service.OwnerService;
import GymApp.util.CoachAccountManagerControllerTestUtil;
import GymApp.util.GeneralUtil;
import GymApp.util.entityAndDtoMappers.AccountMapper;
import com.github.dockerjava.zerodep.shaded.org.apache.commons.codec.binary.Base64;
import org.assertj.core.api.AssertionsForClassTypes;
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
        properties = {"spring.datasource.url=jdbc:tc:postgres:latest:///database", "spring.sql.init.mode=always"})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CoachAccountManagerControllerTest {
    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("database").withUsername("myuser");
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

    private static Coach coach1 = coachBuilder.account(
            accountBuilder
                    .firstName("f1")
                    .secondName("s1")
                    .thirdName("t1")
                    .email("e1@gmail.com")
                    .phoneNumber("1")
                    .password(bCryptPassword)
                    .build()
    ).build();
    private static final Coach coach2 = coachBuilder.account(
            accountBuilder
                    .firstName("f2")
                    .secondName("s2")
                    .thirdName("t2")
                    .email("e2@gmail.com")
                    .phoneNumber("2")
                    .password(bCryptPassword)
                    .build()
    ).build();
    private static final Coach coach3 = coachBuilder.account(
            accountBuilder
                    .firstName("f3")
                    .secondName("s3")
                    .thirdName("t3")
                    .email("e3@gmail.com")
                    .phoneNumber("3")
                    .password(bCryptPassword)
                    .build()
    ).build();

    private static final Owner owner = ownerBuilder.account(
            accountBuilder
                    .firstName("f4")
                    .secondName("s4")
                    .thirdName("t4")
                    .email("e4@gmail.com")
                    .phoneNumber("4")
                    .password(bCryptPassword)
                    .build()
    ).build();
    private static final Client client = clientBuilder.account(
            accountBuilder
                    .firstName("f5")
                    .secondName("s5")
                    .thirdName("t5")
                    .email("e5@gmail.com")
                    .phoneNumber("5")
                    .password(bCryptPassword)
                    .build()
    ).birthDate(LocalDate.of(2024, 5, 16)).build();

    private CoachAccountManagerControllerTestUtil testUtil;


    @BeforeEach
    void setUp() {
        // initialize the database.
        ownerService.save(owner);
        coachService.save(coach1);
        coachService.save(coach2);
        clientService.save(client);

        // perform login to get token with scopes (owner, coach, and client) respectively.
        ownerToken = GeneralUtil.login(owner.getAccount().getEmail(), rawPassword,
                GeneralUtil.getBaseUrl(port) + "/login/owner", restTemplate);
        coachToken = GeneralUtil.login(coach1.getAccount().getEmail(), rawPassword,
                GeneralUtil.getBaseUrl(port) + "/login/coach", restTemplate);
        clientToken = GeneralUtil.login(client.getAccount().getEmail(), rawPassword,
                GeneralUtil.getBaseUrl(port) + "/login/client", restTemplate);

        // set up the util class
        testUtil = new CoachAccountManagerControllerTestUtil(port, restTemplate);
    }

    @AfterEach
    void tearDown() {
        // clear the database to start fresh.
        ownerService.deleteAll();
        coachService.deleteAll();
        clientService.deleteAll();
    }

    /*
     * Create new coach account tests
     * */
    @Test
    void ownerShouldCreateNewCoachAccount() {
        /*
         * This method tests that owner can create coach accounts.
         * It checks that the response status code is 200 (OK).
         * In addition to checking that the returned account profile Dto has the same values
         * as the provided createAccountDto when sending the request
         * I have chosen coach3 as it's not saved to the database in the setup method.
         * "ownerShouldCreateCoachAccount" is a util method used to encapsulate the test logic.
         * */

        // the expected account profile dto (the one that should be returned in the response).
        AccountProfileDto expected = AccountMapper.accountEntityToAccountProfileDto(coach3.getAccount());

        ResponseEntity<AccountProfileDto> response = testUtil.attemptCreateCoachAccount(coach3, ownerToken);

        AccountProfileDto actual = response.getBody();

        AssertionsForClassTypes.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        AssertionsForClassTypes.assertThat(actual).isEqualTo(expected);
    }

    @Test
    void coachAndClientShouldNotCreateNewCoachAccount() {
        /*
         * This method tests that coaches and clients both can't create coach accounts.
         * It checks that Coaches & clients can't create coach accounts.
         * In Addition to checking that the response status code is 403 (FORBIDDEN)
         * User is known to be (owner / coach / client) from the scope of the access token.
         * Here I have chosen coach3 as it's not saved to the database in the setup method.
         * */

        // tests that coach can't create coach account.
        testUtil.coachAndClientShouldNotCreateCoachAccount(coach3, coachToken);

        // tests that client can't create coach account.
        testUtil.coachAndClientShouldNotCreateCoachAccount(coach3, clientToken);

    }

    @Test
    void shouldNotCreateCoachAccountWithMissingRequiredFields() {
        /*
         * Given that you have an access token with scope owner, you can't create new
         * coach account while missing any of the required fields.
         * Required fields are: firstName, secondName, thirdName, email, phoneNumber, and  password.
         * It checks that the response status code it 400 (BAD_REQUEST).
         * coach3: is the coach to be created, I have chosen it as it isn't saved on the database in setup method.
         * token: is an access token with scope owner. (result of owner login in the setup method).
         * */

        // test with missing firstname.
        Coach coachToBeCreated = coachBuilder.copyFrom(coach3).build();
        coachToBeCreated.getAccount().setFirstName(null);
        testUtil.shouldNotCreateNewCoachAccountWithMissingRequiredFields(coachToBeCreated, ownerToken);

        // test with missing secondName.
        coachToBeCreated = coachBuilder.copyFrom(coach3).build();
        coachToBeCreated.getAccount().setSecondName(null);
        testUtil.shouldNotCreateNewCoachAccountWithMissingRequiredFields(coachToBeCreated, ownerToken);

        // test with missing thirdName.
        coachToBeCreated = coachBuilder.copyFrom(coach3).build();
        coachToBeCreated.getAccount().setThirdName(null);
        testUtil.shouldNotCreateNewCoachAccountWithMissingRequiredFields(coachToBeCreated, ownerToken);

        // test with missing email.
        coachToBeCreated = coachBuilder.copyFrom(coach3).build();
        coachToBeCreated.getAccount().setEmail(null);
        testUtil.shouldNotCreateNewCoachAccountWithMissingRequiredFields(coachToBeCreated, ownerToken);

        // test with missing phoneNumber.
        coachToBeCreated = coachBuilder.copyFrom(coach3).build();
        coachToBeCreated.getAccount().setPhoneNumber(null);
        testUtil.shouldNotCreateNewCoachAccountWithMissingRequiredFields(coachToBeCreated, ownerToken);

        // test with missing password.
        coachToBeCreated = coachBuilder.copyFrom(coach3).build();
        coachToBeCreated.getAccount().setPassword(null);
        testUtil.shouldNotCreateNewCoachAccountWithMissingRequiredFields(coachToBeCreated, ownerToken);
    }

    @Test
    void shouldNotCreateNewCoachAccountWithoutAccessToken() {
        /*
         * It checks that you can't create new coach account without access token.
         * It checks that the response status code is 400 (BAD_REQUEST).
         * I have chosen coach3 as it's not saved to the database in the setup method.
         * */
        ResponseEntity<AccountProfileDto> response = testUtil.attemptCreateCoachAccount(coach3, null);
        AssertionsForClassTypes.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);

    }

    /*
     * Get My Account
     * */
    @Test
    void coachShouldGetHisAccountDetails() {
        /*
         * It checks that authenticated coach can get his account details.
         * account details: firstName, secondName, thirdName, email, and phoneNumber.
         * It checks that the response status code is 200 (OK).
         * It checks that the returned accountProfileDto is the same as the accountProfileDto of the provided coach.
         * Here I have used coach1 as it's logged in the setup method resulting coachToken
         * */
        AccountProfileDto expected = AccountMapper.accountEntityToAccountProfileDto(coach1.getAccount());

        ResponseEntity<AccountProfileDto> response = testUtil.attemptGetCoachAccountDetails(coach1, coachToken);
        AccountProfileDto actual = response.getBody();

        AssertionsForClassTypes.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        // I Ignore fields of LocalDataTime to avoid precision issues
        AssertionsForClassTypes.assertThat(actual).usingRecursiveComparison().ignoringFieldsOfTypes(LocalDateTime.class).isEqualTo(expected);
    }

    @Test
    void ownerAndClientShouldNotGetCoachAccountDetails() {
        /*
         * It checks that owners and clients can't get coach accounts details.
         * account details: firstName, secondName, thirdName, email, and phoneNumber.
         * It checks that the response status code is 403 (FORBIDDEN).
         * Here I have used coach1 as it's logged in the setup method resulting coachToken.
         * user is known to be (owner / coach / client) from the scope of the access token.
         * */

        // Test that owner can't get coach account details
        ResponseEntity<AccountProfileDto> response = testUtil.attemptGetCoachAccountDetails(coach1, ownerToken);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);

        // Test that client can't get coach account details
        response = testUtil.attemptGetCoachAccountDetails(coach1, clientToken);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void shouldNotGetCoachAccountDetailsWithoutAccessToken() {
        /*
         * It checks that getting the coach account details without access token isn't achievable.
         * It checks that the response status code is 401 (UNAUTHORIZED).
         * Here I have chosen coach3 as it's not saved to the database in the setup method.
         * */
        ResponseEntity<AccountProfileDto> response = testUtil.attemptGetCoachAccountDetails(coach3, null);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    /*
     * Get all coaches tests
     * */
    @Test
    void ownerShouldGetAllCoaches() {
        /*
         * Given access token with owner scope you should be able to get a list of all coaches in the system.
         * It checks that the response status code is 200 (OK).
         * It checks that all coaches in the system are returned.
         * It checks that all coaches returned are the same ones saved previously in the coach table in the database.
         * */

        // I have chosen coach1 & coach2 as they are saved to the database in the setup method.
        List<AccountProfileDto> expected = Stream.of(coach1, coach2)
                .map(Coach::getAccount)
                .map(AccountMapper::accountEntityToAccountProfileDto)
                .toList();

        ResponseEntity<List<AccountProfileDto>> response = testUtil.attemptGetAllCoaches(ownerToken);

        List<AccountProfileDto> actual = response.getBody();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        // I ignore fields of type LocalDateTime to avoid precision errors.
        assertThat(actual.size()).isEqualTo(expected.size());
        actual
                .forEach(
                        e -> assertThat(e)
                                .usingRecursiveComparison()
                                .ignoringFieldsOfTypes(LocalDateTime.class)
                                .isIn(expected)
                );
    }

    @Test
    void clientAndCoachShouldNotGetAllCoaches() {
        /*
         * Given access token with scope coach or client, you shouldn't be able to get a list of all coaches.
         * It checks that the response status code is 403 (FORBIDDEN).
         * */

        // checks that coach can't get list of all coaches
        testUtil.coachAndClientShouldNotGetAllCoaches(coachToken);

        // checks that client can't get list of all coaches
        testUtil.coachAndClientShouldNotGetAllCoaches(clientToken);
    }

    @Test
    void shouldNotGetAllCoachesWithoutAccessToken() {
        /*
         * Given No access token, you shouldn't be able to get a list of all coaches.
         * It checks that the response status code is 401 (UNAUTHORIZED).
         * */
        ResponseEntity<List<AccountProfileDto>> response = testUtil.attemptGetAllCoaches(null);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    /*
     * Update coach account tests
     * */
    @Test
    void coachShouldUpdateHisOwnAccount() {
        /*
         * Given access token with scope coach, you should be able to update the coach account.
         * Fields you should be able to update are (firstName, secondName, thirdName, email, and phoneNumber)
         * It checks that the response status code is 200 (OK).
         * In addition to checking that the updates are reflected (the accountProfileDto sent, is the same as the one
         * returned in the response body).
         * Here I have chosen "coach1" as it's saved on the database on the setup method and "coachToken" belongs to it.
         * */
        Account coachAccount = coach1.getAccount();

        //perform updates.
        coach1 = testUtil.performUpdatesOnCoachAccount(coach1);

        // expected
        AccountProfileDto expected = AccountMapper.accountEntityToAccountProfileDto(coachAccount);

        ResponseEntity<AccountProfileDto> response = testUtil.attemptUpdateCoachAccount(coach1, coachToken);

        // actual
        AccountProfileDto actual = response.getBody();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        // I ignore fields of type LocalDateTime to avoid precision errors in createdAt and updatedAt fields.
        assertThat(actual).usingRecursiveComparison().ignoringFieldsOfTypes(LocalDateTime.class).isEqualTo(expected);

    }

    @Test
    void ownerAncClientShouldNotUpdateCoachAccount() {
        /*
         * Given access token with scope owner or client, you shouldn't be able to update coach account.
         * It checks that the response status code is 403 (FORBIDDEN).
         * */

        // perform updates
        coach1 = testUtil.performUpdatesOnCoachAccount(coach1);

        // checks that owner can't update coach account.
        testUtil.ownerAndClientShouldNotUpdateCoachAccount(coach1, ownerToken);

        // checks that client can't update coach account.
        testUtil.ownerAndClientShouldNotUpdateCoachAccount(coach1, clientToken);
    }

    @Test
    void shouldNotUpdateCoachAccountWithMissingRequiredFields() {
        /*
         * Given that you have an access token with scope coach, you can't update
         * coach account while missing any of the required fields.
         * Required fields are: firstName, secondName, thirdName, email, and phoneNumber.
         * It checks that the response status code it 400 (BAD_REQUEST).
         * coach1: is the coach to be updated, I have chosen it as it is saved on the database in setup method.
         * */

        Coach.Builder coachBuilder = new Coach.Builder();
        Coach sampleCoach = coachBuilder.copyFrom(coach1).build();

        // can't update coach account without first name .
        sampleCoach.getAccount().setFirstName(null);
        testUtil.shouldNotUpdateCoachAccountWithMissingRequiredFields(sampleCoach, coachToken);

        // can't update coach account without second name.
        sampleCoach = coachBuilder.copyFrom(coach1).build();
        sampleCoach.getAccount().setSecondName(null);
        testUtil.shouldNotUpdateCoachAccountWithMissingRequiredFields(sampleCoach, coachToken);

        // can't update coach account without third name.
        sampleCoach = coachBuilder.copyFrom(coach1).build();
        sampleCoach.getAccount().setThirdName(null);
        testUtil.shouldNotUpdateCoachAccountWithMissingRequiredFields(sampleCoach, coachToken);

        // can't update coach account without email.
        sampleCoach = coachBuilder.copyFrom(coach1).build();
        sampleCoach.getAccount().setEmail(null);
        testUtil.shouldNotUpdateCoachAccountWithMissingRequiredFields(sampleCoach, coachToken);

        // can't update coach account without phone number.
        sampleCoach = coachBuilder.copyFrom(coach1).build();
        sampleCoach.getAccount().setPhoneNumber(null);
        testUtil.shouldNotUpdateCoachAccountWithMissingRequiredFields(sampleCoach, coachToken);

    }

    @Test
    void shouldNotUpdateCoachAccountWithoutAccessToken() {
        /*
         * It checks that you shouldn't be able to update coach account without access token.
         * It checks that the response status code is 401 (UNAUTHORIZED).
         * I have used coach1 is it's saved to the database in the setup method.
         * */

        // perform updates on coach1
        coach1 = testUtil.performUpdatesOnCoachAccount(coach1);

        ResponseEntity<AccountProfileDto> response = testUtil.attemptUpdateCoachAccount(coach1, null);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    /*
     * Change coach account password tests
     * */
    @Test
    void coachShouldChangeHisOwnAccountPassword() {
        /*
         * Given access token with scope coach you should be able to change the accounts password for that coach.
         * It checks that the response status code is 200 (OK).
         * */

        ResponseEntity<String> response = testUtil.attemptChangeCoachAccountPassword("1234", coachToken);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    }

    @Test
    void coachShouldChangeHisOwnAccountPasswordWithEmptyPassword() {
        /*
         * Given access token with scope coach you should not be able to change his own account password with empty password.
         * It checks that the response status code is 400 (BAD_REQUEST).
         * */

        ResponseEntity<String> response = testUtil.attemptChangeCoachAccountPassword(null, coachToken);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void ownerAndClientShouldNotChangeCoachAccountPassword() {
        /*
         * Given access token with scope owner or client you should not be able to change the accounts password of a coach.
         * It checks that the response status code is 403 (FORBIDDEN).
         * */


        // checks that owner can't change coach account password
        ResponseEntity<String> response = testUtil.attemptChangeCoachAccountPassword("1234", ownerToken);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);

        // checks that client can't change coach account password
        response = testUtil.attemptChangeCoachAccountPassword("1234", clientToken);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void shouldNotChangeCoachAccountPasswordWithoutAccessToken() {
        /*
         * You should not be able to change the accounts password of a coach without access token.
         * It checks that the response status code is 403 (UNAUTHORIZED).
         * */
        ResponseEntity<String> response = testUtil.attemptChangeCoachAccountPassword("1234", null);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    /*
     * Delete coach account tests
     * */
    @Test
    void ownerShouldDeleteCoachAccount() {
        /*
         * Given access token with scope owner , and a deleteAccountDto you should be able to delete coach account.
         * deleteAccountDto: contains the email or phoneNumber or both for the coach account to be deleted.
         * It checks that the response status code is 200 (OK).
         * In addition to checking that you can delete coach account with his email and phone number or his email only or
         * his phone number only.
         * */

        // check that owner can delete coach account by providing his email and phone number
        testUtil.shouldDeleteCoachAccount(
                new DeleteAccountDto(
                        coach1.getAccount().getEmail(), coach1.getAccount().getPhoneNumber()
                ),
                ownerToken);

        // check that owner can delete coach account by providing his email only.
        testUtil.shouldDeleteCoachAccount(
                new DeleteAccountDto(
                        coach1.getAccount().getEmail(), null
                ),
                ownerToken);

        // check that owner can delete coach account by providing his phone number only.
        testUtil.shouldDeleteCoachAccount(
                new DeleteAccountDto(
                        null, coach1.getAccount().getPhoneNumber()
                ),
                ownerToken);
    }

    @Test
    void coachAndClientShouldNotDeleteCoachAccount() {
        /*
         * Given access token with scope coach or client , and a deleteAccountDto you should not be able to delete coach account.
         * deleteAccountDto: contains the email or phoneNumber or both for the coach account to be deleted.
         * It checks that the response status code is 403 (FORBIDDEN).
         */

        // checks that coach can't delete coach account.
        ResponseEntity<String> response = testUtil.attemptDeleteCoachAccountPassword(
                new DeleteAccountDto(
                        coach1.getAccount().getEmail(), coach1.getAccount().getPhoneNumber()
                ),
                coachToken);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);

        // checks that client can't delete coach account.
        response = testUtil.attemptDeleteCoachAccountPassword(
                new DeleteAccountDto(
                        coach1.getAccount().getEmail(), coach1.getAccount().getPhoneNumber()
                ),
                clientToken);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);

    }

    @Test
    void shouldNotDeleteCoachAccountWithoutAccessToken() {
        /*
         * should not be able to delete coach account without access token
         * deleteAccountDto: contains the email or phoneNumber or both for the coach account to be deleted.
         * It checks that the response status code is 403 (UNAUTHORIZED).
         */

        ResponseEntity<String> response = testUtil.attemptDeleteCoachAccountPassword(
                new DeleteAccountDto(
                        coach1.getAccount().getEmail(), coach1.getAccount().getPhoneNumber()
                ),
                null);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);

    }


}