package GymApp.controller;

import GymApp.dto.AccountProfileDto;


import GymApp.entity.Account;
import GymApp.entity.Owner;
import GymApp.service.OwnerService;
import com.github.dockerjava.zerodep.shaded.org.apache.commons.codec.binary.Base64;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;


import org.springframework.web.client.RestTemplate;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;




import java.util.List;


import static org.assertj.core.api.Assertions.assertThat;

import static org.junit.jupiter.api.Assertions.*;


@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = { "spring.datasource.url=jdbc:tc:postgres:latest:///database", "spring.sql.init.mode=always" })
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class OwnerAccountManagerControllerTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("database").withUsername("myuser");

    @Autowired
    RestTemplate restTemplate;
    @Autowired
    OwnerService ownerService;

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
       ownerService.deleteAll();
    }

    @Test
    void postgresContainerShouldBeRunning (){
        assertThat(postgres.isRunning()).isTrue();
    }


    @Test
    void getAllOwners() {
        // "123" encoded with bCrypt
        String bCryptPassword = "$2a$12$fdQCjXHktjZczz5hlHg77u8bIXUQdzGQf5k7ulN.cxzhW2vidHzSu";

        // Create 2 accounts
        Account acc1 = new Account("f1", "s1", "t1","e1@gmail.com", "1",
                bCryptPassword, null, null);
        Account acc2 = new Account("f2", "s2", "t2","e2@gmail.com", "2",
                bCryptPassword, null, null);

        // Make them owner accounts.
        Owner owner1 = new Owner(acc1);
        Owner owner2 = new Owner(acc2);
        ownerService.save(owner1);
        ownerService.save(owner2);

        // Create the basic auth request to the api/login/owner endpoint.
        String plainCredentials  = "1:123";
        byte[] plainCredentialsBytes = plainCredentials.getBytes();

        // Encode the basic authentication request.
        byte[] base64CredentialsBytes = Base64.encodeBase64(plainCredentialsBytes);
        String base64Credentials= new String(base64CredentialsBytes);

        // Create the header object
        HttpHeaders basicAuthHeaders = new HttpHeaders();
        basicAuthHeaders.add("Authorization", "Basic " + base64Credentials);

        // Get the port number as it was created randomly.
        String baseUrl = "http://localhost:" + port;

        // Perform login to get the token.
        HttpEntity<String> basicAuthRequest = new HttpEntity<String>(basicAuthHeaders);
        String token = restTemplate.postForObject(baseUrl + "/api/login/owner",basicAuthRequest
                ,String.class);

        // Sending request associated with the token to get a list of all owners.
        HttpHeaders tokenAuthHeaders = new HttpHeaders();
        tokenAuthHeaders.add("Authorization", "Bearer " + token);
        HttpEntity<String> TokenAuthRequest = new HttpEntity<String>(tokenAuthHeaders);
        List<AccountProfileDto> allOwnerAccounts = restTemplate.exchange(baseUrl + "/api/owner-account-manager/all",
                HttpMethod.GET, TokenAuthRequest,List.class).getBody();

        // Check that there is 2 owner accounts returned
        assertThat(allOwnerAccounts.size()).isEqualTo(2);
    }

    @Test
    void getOwner() {

    }

    @Test
    void createOwnerAccount() {
    }

    @Test
    void updateOwnerAccount() {
    }

    @Test
    void changeOwnerAccountPassword() {
    }

    @Test
    void deleteOwnerAccount() {
    }


}