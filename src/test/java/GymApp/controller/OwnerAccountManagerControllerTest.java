package GymApp.controller;


import GymApp.dto.AccountProfileDto;
import GymApp.entity.Account;
import GymApp.entity.Client;
import GymApp.entity.Coach;
import GymApp.entity.Owner;
import GymApp.service.ClientService;
import GymApp.service.CoachService;
import GymApp.service.OwnerService;

import GymApp.util.GeneralUtil;
import GymApp.util.OwnerAccountManagerControllerTestUtil;
import GymApp.util.Request;
import GymApp.util.entityAndDtoMappers.AccountMapper;
import org.junit.jupiter.api.AfterEach;
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
import org.testcontainers.shaded.org.bouncycastle.cert.ocsp.Req;


import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


import static org.assertj.core.api.Assertions.assertThat;


@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {"spring.datasource.url=jdbc:tc:postgres:latest:///database", "spring.sql.init.mode=always"})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class OwnerAccountManagerControllerTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest").withDatabaseName("database").withUsername("myuser");

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
    private static String bCryptPassword = "$2a$12$fdQCjXHktjZczz5hlHg77u8bIXUQdzGQf5k7ulN.cxzhW2vidHzSu";


    private static final Owner.Builder ownerBuilder = new Owner.Builder();
    private static final Coach.Builder coachBuilder = new Coach.Builder();
    private static final Client.Builder clientBuilder = new Client.Builder();
    private static final Account.Builder accountBuilder = new Account.Builder();

    // coach, client, owner2 all are declared final as they shouldn't change
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

    private static final Owner owner2 = ownerBuilder
            .account(
                    accountBuilder
                            .firstName("f2")
                            .secondName("s2")
                            .thirdName("t2")
                            .email("e2@gmail.com")
                            .phoneNumber("2")
                            .password(bCryptPassword)
                            .build()
            ).build();
    private static final Owner owner3 = ownerBuilder
            .account(
                    accountBuilder
                            .firstName("f3")
                            .secondName("s3")
                            .thirdName("t3")
                            .email("e3@gmail.com")
                            .phoneNumber("3")
                            .password(bCryptPassword)
                            .build()
            ).build();
    // owner1 isn't declared final as it will change for example in updateOwnerAccount
    // that is why it will be initialized in the setUp method.
    private static Owner owner1;


    @BeforeEach
    void setUp() {
        // initialize owner1
        // the owner1 will be used to perform login.
        owner1 = ownerBuilder.account(
                accountBuilder
                        .firstName("f1")
                        .secondName("s1")
                        .thirdName("t1")
                        .email("e1@gmail.com")
                        .phoneNumber("1")
                        .password(bCryptPassword)
                        .build()
        ).build();

        // initialize the database.
        ownerService.save(owner1);
        ownerService.save(owner2);
        coachService.save(coach);
        clientService.save(client);

        // perform login and a token with scope owner.
        ownerToken = GeneralUtil.login(owner1.getAccount().getEmail(), rawPassword,
                GeneralUtil.getBaseUrl(port) + "/login/owner", restTemplate);
        coachToken = GeneralUtil.login(coach.getAccount().getEmail(), rawPassword,
                GeneralUtil.getBaseUrl(port) + "/login/coach", restTemplate);
        clientToken = GeneralUtil.login(client.getAccount().getEmail(), rawPassword,
                GeneralUtil.getBaseUrl(port) + "/login/client", restTemplate);

    }

    @AfterEach
    void tearDown() {
        // delete all owners to start fresh.
        ownerService.deleteAll();
        coachService.deleteAll();
        clientService.deleteAll();
    }

    @Test
    void postgresContainerShouldBeRunning() {
        assertThat(postgres.isRunning()).isTrue();
    }

    /*
     * Create Owner Account tests.
     * */
    @Test
    void shouldCreateOwnerAccount() {
        /*
         * This method tests the ability of authenticated owner to create new owner account.
         * It checks that the response status code is 200 (OK).
         * In addition to checking that the returned account profile Dto has the same values
         * as the provided createAccountDto when sending the request.
         * I have chosen Owner3 as it's not saved to the database in the setup method.
         * */
        Owner toBeCreatedOwner = ownerBuilder.copyFrom(owner3).build();
        // This method encapsulates the logic of the test.
        OwnerAccountManagerControllerTestUtil
                .shouldCreateOwnerAccount(toBeCreatedOwner, ownerToken, port, restTemplate);
    }

    @Test
    void shouldNotCreateOwnerAccountWithMissingRequiredFields() {
        /*
         * This method tests that authenticated owner can't create owner account without any of the required fields.
         * It checks that the response status code is 400 (Bad Request)
         * Required fields are: firstName, secondName, thirdName, email, phoneNumber, password.
         * I have chosen Owner3 as it's not saved to the database in the setup method.
         * "OwnerAccountManagerControllerTestUtil.shouldNotCreateOwnerAccountWithMissingRequiredFields" encapsulates
         * the logic of the test.
         *
         * */

        // get sample owner.
        Owner toBeCreatedOwner = ownerBuilder.copyFrom(owner3).build();

        // should not create owner account with missing firstName.
        toBeCreatedOwner.getAccount().setFirstName(null);
        OwnerAccountManagerControllerTestUtil.shouldNotCreateOwnerAccountWithMissingRequiredFields(toBeCreatedOwner,
                ownerToken, port, restTemplate);

        // should not create owner account with missing secondName.
        toBeCreatedOwner = ownerBuilder.copyFrom(owner3).build();
        toBeCreatedOwner.getAccount().setSecondName(null);
        OwnerAccountManagerControllerTestUtil.shouldNotCreateOwnerAccountWithMissingRequiredFields(toBeCreatedOwner,
                ownerToken, port, restTemplate);

        // should not create owner account with missing thirdName.
        toBeCreatedOwner = ownerBuilder.copyFrom(owner3).build();
        toBeCreatedOwner.getAccount().setThirdName(null);
        OwnerAccountManagerControllerTestUtil.shouldNotCreateOwnerAccountWithMissingRequiredFields(toBeCreatedOwner,
                ownerToken, port, restTemplate);

        // should not create owner account with missing email.
        toBeCreatedOwner = ownerBuilder.copyFrom(owner3).build();
        toBeCreatedOwner.getAccount().setEmail(null);
        OwnerAccountManagerControllerTestUtil.shouldNotCreateOwnerAccountWithMissingRequiredFields(toBeCreatedOwner,
                ownerToken, port, restTemplate);

        // should not create owner account with missing phone number.
        toBeCreatedOwner = ownerBuilder.copyFrom(owner3).build();
        toBeCreatedOwner.getAccount().setPhoneNumber(null);
        OwnerAccountManagerControllerTestUtil.shouldNotCreateOwnerAccountWithMissingRequiredFields(toBeCreatedOwner,
                ownerToken, port, restTemplate);

        // should not create owner account with missing password.
        toBeCreatedOwner = ownerBuilder.copyFrom(owner3).build();
        toBeCreatedOwner.getAccount().setPassword(null);
        OwnerAccountManagerControllerTestUtil.shouldNotCreateOwnerAccountWithMissingRequiredFields(toBeCreatedOwner,
                ownerToken, port, restTemplate);
    }

    @Test
    void unAuthenticatedOwnerShouldNotCreateOwnerAccount() {
        /*
         * This method tests that you can't create owner account without access token.
         * It checks that the response status code is 401 (UNAUTHORIZED)
         * */

        /*
         * Create sample owner.
         * Why owner3 ?
         * As it isn't saved to the database in the setup method.
         * "OwnerAccountManagerControllerTestUtil.unAuthenticatedOwnerShouldNotCreateOwnerAccount" encapsulates the
         * logic of the test.
         * */
        Owner toBeCreatedOwner = ownerBuilder.copyFrom(owner3).build();
        OwnerAccountManagerControllerTestUtil
                .unAuthenticatedOwnerShouldNotCreateOwnerAccount(toBeCreatedOwner, null, port, restTemplate);
    }

    @Test
    void clientAndCoachShouldNotCreateOwnerAccount() {
        /*
         * This method tests that coaches and clients are unable to create owner accounts
         * It checks that the response status code is 403 (FORBIDDEN)
         * Note: we know if a user is (owner / coach / client) from the scope of the access token.
         * "OwnerAccountManagerControllerTestUtil.clientAndCoachShouldNotCreateOwnerAccount"
         * encapsulates the logic of the test
         * */

        // create sample owner
        Owner toBeCreatedOwner = ownerBuilder.copyFrom(owner3).build();
        OwnerAccountManagerControllerTestUtil.clientAndCoachShouldNotCreateOwnerAccount(toBeCreatedOwner, coachToken,
                port, restTemplate);
        OwnerAccountManagerControllerTestUtil.clientAndCoachShouldNotCreateOwnerAccount(toBeCreatedOwner, clientToken,
                port, restTemplate);
    }

    /*
     * Get Owner profile tests.
     * */
    @Test
    void shouldGetOwnerProfile() {
        /*
         * This method tests that authenticated owner can get his profile details.
         * It checks that the response status code is 200 (OK),
         * in addition to checking that values of owner1 are the same as the returned in the response.
         * here I have used owner1 as the ownerToken belongs to it.
         * "OwnerAccountManagerControllerTestUtil.shouldGetOwnerProfile" encapsulates the logic of the test
         * */
        OwnerAccountManagerControllerTestUtil.shouldGetOwnerProfile(owner1, ownerToken, port, restTemplate);
    }

    @Test
    void shouldNotGetOwnerProfileWithoutAccessToken() {
        /*
         * this method tests that you can't get an owner profile without access token.
         * It checks that the response status code is 401 (UNAUTHORIZED).
         * */
        OwnerAccountManagerControllerTestUtil.shouldNotGetOwnerProfile(null, port, restTemplate);
    }

    @Test
    void coachAndClientShouldNotGetOwnerProfile() {
        /*
         * This method tests that you can't get an owner profile with a coach / client access token.
         * It checks that the response status code is 403 (FORBIDDEN).
         * Note: coach / client is known from the scope of the access token.
         * "OwnerAccountManagerControllerTestUtil.coachAndClientShouldNotGetOwnerProfile" encapsulates the test logic.
         * */

        // this method tests that you can't get an owner profile with a coach access token.
        OwnerAccountManagerControllerTestUtil.coachAndClientShouldNotGetOwnerProfile(coachToken, port, restTemplate);

        // this method tests that you can't get an owner profile with a client access token.
        OwnerAccountManagerControllerTestUtil.coachAndClientShouldNotGetOwnerProfile(clientToken, port, restTemplate);
    }

    /*
     * Get All owners tests.
     * */
    @Test
    void ownerShouldGetAllOwners() {
        /*
         * This method check that an owner can list all owners in the system.
         * It checks that the response status code is 200 (OK),
         * in addition to checking that the returned list is same as the already saved owners in the database.
         * "The already saved owners in the database" are owner1 and owner2.
         * "OwnerAccountManagerControllerTestUtil.shouldGetAllOwners" encapsulates the logic of the test.
         * */
        // this method tests the ability of an authenticated owner to get all owners in the system
        OwnerAccountManagerControllerTestUtil.shouldGetAllOwners(List.of(owner1, owner2), ownerToken, port, restTemplate);
    }

    @Test
    void coachAndClientShouldNotGetAllOwners() {
        /*
         * This method tests that coach and client can't get all owner accounts
         * It checks that the response status code is 403 (FORBIDDEN)
         * user is known to be client or coach from the scope of the access token.
         * */
        OwnerAccountManagerControllerTestUtil.shouldNotGetAllOwners(coachToken, port, restTemplate);
        OwnerAccountManagerControllerTestUtil.shouldNotGetAllOwners(clientToken, port, restTemplate);
    }

    @Test
    void shouldNotGetAllOwnersWithoutAccessToken() {
        /*
         * this method tests that getting all owners isn't achievable without access token.
         * It checks that the response status code is 401 (UNAUTHORIZED)
         * */
        OwnerAccountManagerControllerTestUtil.shouldNotGetAllOwnersWithoutAccessToken(port, restTemplate);
    }

    /*
     * Update owner account tests.
     * */
    @Test
    void shouldUpdateOwnerProfile() {

        /*
         * This method tests the ability of owner to update his profile.
         * It checks that the updates made are reflected in the response body.
         * We send the updated accountProfileDto in the request, then check if the updates are reflected in the
         * accountProfileDto returned in the response.
         * In addition, it checks that the response status code is 200 (OK).
         * "OwnerAccountManagerControllerTestUtil.shouldUpdateOwnerAccount" it encapsulates the test logic.
         * I have chosen owner1 as it's already saved on the database.
         * the updates to values of owner1 are done on "OwnerAccountManagerControllerTestUtil.shouldUpdateOwnerAccount"
         * */
        OwnerAccountManagerControllerTestUtil.shouldUpdateOwnerAccount(ownerToken, owner1, port, restTemplate);
    }

    @Test
    void coachAndClientShouldNotUpdateOwnerProfile() {
        /*
         * This method tests that coach / client  can't update owner account.
         * It checks that the response status code is 403 (FORBIDDEN).
         * user is known to be client or coach from the scope of the access token.
         * */

        // tests that coach can't update owner account.
        OwnerAccountManagerControllerTestUtil
                .coachAndClientShouldNotUpdateOwnerAccount(coachToken, owner1, port, restTemplate);

        // tests that client can't update owner account.
        OwnerAccountManagerControllerTestUtil
                .coachAndClientShouldNotUpdateOwnerAccount(clientToken, owner1, port, restTemplate);
    }

    @Test
    void shouldNotUpdateOwnerProfileWithoutAccessToken() {
        /*
         * This method tests that changing owner profile without access token isn't achievable.
         * It checks that the response status code is 401 (UNAUTHORIZED)
         * */
        //
        OwnerAccountManagerControllerTestUtil
                .shouldNotUpdateOwnerAccountProfileWithoutAccessToken(owner1, port, restTemplate);
    }

    @Test
    void shouldNotUpdateOwnerProfileWithMissingRequiredFields() {
        /*
         * This method tests that you can't update owner profile without any of the required fields.
         * It checks that the response status code is 400 (BAD_REQUEST).
         * You may wonder what if I want to update certain field only not all of them, then provide them without update.
         * "OwnerAccountManagerControllerTestUtil.shouldNotUpdateOwnerAccountProfileWithMissingRequiredFields"
         * Encapsulates the test logic.
         * */
        Owner owner = ownerBuilder.copyFrom(owner1).build(); // sample owner

        // try update without firstName.
        owner.getAccount().setFirstName(null);
        OwnerAccountManagerControllerTestUtil
                .shouldNotUpdateOwnerAccountProfileWithMissingRequiredFields
                        (owner, ownerToken, port, restTemplate);

        // try update without secondName.
        owner = ownerBuilder.copyFrom(owner1).build();
        owner.getAccount().setSecondName(null);
        OwnerAccountManagerControllerTestUtil
                .shouldNotUpdateOwnerAccountProfileWithMissingRequiredFields
                        (owner, ownerToken, port, restTemplate);

        // try update without thirdName.
        owner = ownerBuilder.copyFrom(owner1).build();
        owner.getAccount().setThirdName(null);
        OwnerAccountManagerControllerTestUtil
                .shouldNotUpdateOwnerAccountProfileWithMissingRequiredFields
                        (owner, ownerToken, port, restTemplate);

        // try update without email.
        owner = ownerBuilder.copyFrom(owner1).build();
        owner.getAccount().setEmail(null);
        OwnerAccountManagerControllerTestUtil
                .shouldNotUpdateOwnerAccountProfileWithMissingRequiredFields
                        (owner, ownerToken, port, restTemplate);

        // try update without phoneNumber.
        owner = ownerBuilder.copyFrom(owner1).build();
        owner.getAccount().setPhoneNumber(null);
        OwnerAccountManagerControllerTestUtil
                .shouldNotUpdateOwnerAccountProfileWithMissingRequiredFields
                        (owner, ownerToken, port, restTemplate);
    }

    /*
     * Change owner account password tests.
     * */
    @Test
    void shouldChangeOwnerPassword() {
        /*
         * this method tests that authenticated owner can change his password.
         * It checks that the response status code is 200 (OK)
         * "OwnerAccountManagerControllerTestUtil.shouldChangeOwnerPassword" encapsulates the test logic.
         * */
        OwnerAccountManagerControllerTestUtil
                .shouldChangeOwnerPassword(ownerToken, "1234", port, restTemplate);
    }

    @Test
    void coachAndClientShouldNotChangeOwnerPassword() {
        /*
         * tests that both coach and client can't change password for owner account.
         * It checks that the response status code is 403 (FORBIDDEN)
         * "OwnerAccountManagerControllerTestUtil.coachAndClientShouldNotChangeOwnerPassword" encapsulates the test logic.
         * */
        OwnerAccountManagerControllerTestUtil
                .coachAndClientShouldNotChangeOwnerPassword(coachToken, "1234", port, restTemplate);

        OwnerAccountManagerControllerTestUtil
                .coachAndClientShouldNotChangeOwnerPassword(clientToken, "1234", port, restTemplate);
    }

    @Test
    void shouldNotUpdatePasswordWithEmptyPassword() {
        /*
         * This method tests that you can't change the password with empty password.
         * It checks that the response status code is 400 bad request.
         * "OwnerAccountManagerControllerTestUtil.shouldNotChangePasswordWithEmptyPassword" encapsulates the test logic.
         * */
        OwnerAccountManagerControllerTestUtil
                .shouldNotChangePasswordWithEmptyPassword(ownerToken, "", port, restTemplate);
    }

    @Test
    void shouldNotChangeOwnerPasswordWithoutAccessToken() {
        /*
         * This method tests that changing owner Account password is not achievable without access token.
         * It checks that the response status code is 401 (UNAUTHORIZED)
         * "OwnerAccountManagerControllerTestUtil.shouldNotChangeOwnerPasswordWithoutAccessToken" encapsulates
         * the test logic
         * */
        OwnerAccountManagerControllerTestUtil
                .shouldNotChangeOwnerPasswordWithoutAccessToken("1234", port, restTemplate);
    }

    /*
     * Delete owner account tests.
     * */
    @Test
    void shouldDeleteOwnerAccount() {
        /*
         * This method tests that owner can delete his own account.
         * it checks that the response status code is 200 (OK).
         * "OwnerAccountManagerControllerTestUtil.shouldDeleteOwnerAccount" encapsulates the test logic.
         * */
        OwnerAccountManagerControllerTestUtil
                .shouldDeleteOwnerAccount(ownerToken, port, restTemplate);
    }

    @Test
    void coachAndClientShouldNotDeleteOwnerAccount() {
        /*
         * This method tests that coach and client can't delete owner account.
         * it checks that the response status code is 403 (FORBIDDEN)
         * OwnerAccountManagerControllerTestUtil.coachAndClientShouldNotDeleteOwnerAccount encapsulates the test logic.
         * user is known to be (owner / coach / client ) from the token scope.
         * */
        OwnerAccountManagerControllerTestUtil.coachAndClientShouldNotDeleteOwnerAccount(coachToken, port, restTemplate);

        OwnerAccountManagerControllerTestUtil.coachAndClientShouldNotDeleteOwnerAccount(clientToken, port, restTemplate);
    }

    @Test
    void shouldNotDeleteOwnerAccountWithoutAccessToken() {
        /*
         * This method tests that deleting owner account isn't achievable without access token.
         * It checks response status code is 401 (UNAUTHORIZED)
         * "OwnerAccountManagerControllerTestUtil.shouldNotDeleteOwnerAccountWithoutAccessToken"
         * encapsulates the test logic
         * */
        OwnerAccountManagerControllerTestUtil.shouldNotDeleteOwnerAccountWithoutAccessToken(port, restTemplate);
    }
}