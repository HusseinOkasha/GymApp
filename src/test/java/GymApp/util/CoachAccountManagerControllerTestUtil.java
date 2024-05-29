package GymApp.util;

import GymApp.dto.AccountProfileDto;
import GymApp.dto.ChangePasswordDto;
import GymApp.dto.CreateAccountDto;
import GymApp.dto.DeleteAccountDto;
import GymApp.entity.Account;
import GymApp.entity.Coach;
import GymApp.util.entityAndDtoMappers.AccountMapper;
import org.assertj.core.api.Assertions;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;


import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


public class CoachAccountManagerControllerTestUtil {

    // fields
    private int port; // the port number the server is running on as it is generated randomly
    private RestTemplate restTemplate; // used to send requests


    // constructors
    public CoachAccountManagerControllerTestUtil(int port, RestTemplate restTemplate) {
        this.port = port;
        this.restTemplate = restTemplate;
    }

    /*
     * Create new coach account test utils
     * */
    public ResponseEntity<AccountProfileDto> attemptCreateCoachAccount(Coach coach, String token) {
        /*
         * this method encapsulates the process of sending a POST request to "/api/coach-account-manager"
         * coach: is the coach to be created.
         * token: is the access token to be added as a bearer token to the authorization header
         * */

        // Get the create account Dto of the coach to be created.
        CreateAccountDto createAccountDto = AccountMapper.accountEntityToCreateAccountDto(coach.getAccount());

        Request.Builder<CreateAccountDto, AccountProfileDto> requestBuilder = new Request.Builder<>();

        Request<CreateAccountDto, AccountProfileDto> request = requestBuilder
                .endPointUrl("/coach-account-manager")
                .httpMethod(HttpMethod.POST)
                .portNumber(port)
                .httpEntity(token, createAccountDto)
                .responseDataType(new ParameterizedTypeReference<>() {
                })
                .restTemplate(restTemplate)
                .build();

        return request.sendRequest();
    }

    public void coachAndClientShouldNotCreateCoachAccount(Coach coach, String token) {
        /*
         * It's a util method that encapsulates the logic of the test.
         * It checks that Coaches & clients can't create coach accounts.
         * User is known to be (owner / coach / client) from the scope of the access token.
         * coach: is the coach to be created.
         * it checks that the response status code is 403 (FORBIDDEN)
         * */
        ResponseEntity<AccountProfileDto> response = attemptCreateCoachAccount(coach, token);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    public void shouldNotCreateNewCoachAccountWithMissingRequiredFields(Coach coach, String token) {
        /*
         * It's a util method that encapsulates the logic of a test
         * Given that you have an access token of an authenticated owner, you can't create new
         * coach account while missing any of the required fields.
         * Required fields are: firstName, secondName, thirdName, email, phoneNumber, and  password.
         * coach: is the coach to be created.
         * token: is an access token with scope owner.
         * */

        ResponseEntity<AccountProfileDto> response = attemptCreateCoachAccount(coach, token);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    /*
     * Get coach account details test utils
     * */
    public ResponseEntity<AccountProfileDto> attemptGetCoachAccountDetails(Coach coach, String token) {
        /*
         * this method encapsulates the process of sending a GET request to "/api/coach-account-manager"
         * coach: to be returned (which the token belongs to).
         * token: is the access token to be added as a bearer token to the authorization header.
         * */

        // Get the create account Dto of the coach to be created.
        AccountProfileDto accountProfileDto = AccountMapper.accountEntityToAccountProfileDto(coach.getAccount());

        Request.Builder<AccountProfileDto, AccountProfileDto> requestBuilder = new Request.Builder<>();

        Request<AccountProfileDto, AccountProfileDto> request = requestBuilder
                .endPointUrl("/coach-account-manager")
                .httpMethod(HttpMethod.GET)
                .portNumber(port)
                .httpEntity(token, accountProfileDto)
                .responseDataType(new ParameterizedTypeReference<>() {
                })
                .restTemplate(restTemplate)
                .build();

        return request.sendRequest();
    }

    /*
     * Get All Coaches test utils
     * */
    public ResponseEntity<List<AccountProfileDto>> attemptGetAllCoaches(String token) {
        /*
         * This method encapsulates sending a GET request to "/api/coach-account-manager/coaches"
         * to get a list of all coaches.
         * token: is the access token to be sent in the authorization header.
         * */

        Request.Builder<String, List<AccountProfileDto>> requestBuilder = new Request.Builder<>();

        Request<String, List<AccountProfileDto>> request = requestBuilder
                .endPointUrl("/coach-account-manager/coaches")
                .httpMethod(HttpMethod.GET)
                .portNumber(port)
                .httpEntity(token, null)
                .responseDataType(new ParameterizedTypeReference<>() {
                })
                .restTemplate(restTemplate)
                .build();
        return request.sendRequest();

    }

    public void coachAndClientShouldNotGetAllCoaches(String token) {
        /*
         * It is a util method.
         * Given access token with scope coach or client, you shouldn't be able to get a list of all coaches.
         * It checks that the response status code is 403 (FORBIDDEN).
         * */
        ResponseEntity<List<AccountProfileDto>> response = attemptGetAllCoaches(token);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    /*
     * Update coach account test utils
     * */
    public ResponseEntity<AccountProfileDto> attemptUpdateCoachAccount(Coach coach, String token) {
        /*
         * this method encapsulates the process of sending a PUT request to "/api/coach-account-manager"
         * coach: the updated coach.
         * token: is the access token to be added as a bearer token to the authorization header.
         * */
        AccountProfileDto accountProfileDto = AccountMapper.accountEntityToAccountProfileDto(coach.getAccount());

        Request.Builder<AccountProfileDto, AccountProfileDto> requestBuilder = new Request.Builder<>();

        Request<AccountProfileDto, AccountProfileDto> request = requestBuilder
                .endPointUrl("/coach-account-manager")
                .httpMethod(HttpMethod.PUT)
                .portNumber(port)
                .httpEntity(token, accountProfileDto)
                .responseDataType(new ParameterizedTypeReference<>() {
                })
                .restTemplate(restTemplate)
                .build();

        return request.sendRequest();

    }

    public void ownerAndClientShouldNotUpdateCoachAccount(Coach coach, String token) {
        /*
         * It's a util method that encapsulates the logic of the test.
         * It checks that owners & clients can't update coach accounts.
         * User is known to be (owner / coach / client) from the scope of the access token.
         * coach: is the updated coach.
         * it checks that the response status code is 403 (FORBIDDEN)
         * */
        ResponseEntity<AccountProfileDto> response = attemptUpdateCoachAccount(coach, token);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    public void shouldNotUpdateCoachAccountWithMissingRequiredFields(Coach coach, String token) {
        /*
         * It's a util method that encapsulates the logic of the test.
         * It checks that updating coach without providing all required fields is impossible.
         * coach: is the updated coach.
         * token: is the access token to be used in the authorization header.
         * it checks that the response status code is 400 (BAD_REQUEST)
         * */

        ResponseEntity<AccountProfileDto> response = attemptUpdateCoachAccount(coach, token);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

    }

    public Coach performUpdatesOnCoachAccount(Coach coach) {
        /*
         * util method encapsulates updating coach account.
         * */

        //perform some updates.
        Account coachAccount = coach.getAccount();
        coachAccount.setFirstName("updated firstName");
        coachAccount.setSecondName("updated secondName");
        coachAccount.setThirdName("updated thirdName");
        coachAccount.setEmail("updated@email.com");
        coachAccount.setPhoneNumber("updated Phone number");
        return coach;

    }

    /*
     * Change coach account password test utils
     * */
    public ResponseEntity<String> attemptChangeCoachAccountPassword(String password, String token) {
        /*
         * this method encapsulates the process of sending a PUT request to "/api/coach-account-manager/password"
         * password: is the new password.
         * token: is the access token to be added as a bearer token to the authorization header.
         * */
        ChangePasswordDto changePasswordDto = new ChangePasswordDto(password);

        Request.Builder<ChangePasswordDto, String> requestBuilder = new Request.Builder<>();

        Request<ChangePasswordDto, String> request = requestBuilder
                .endPointUrl("/coach-account-manager/password")
                .httpMethod(HttpMethod.PUT)
                .portNumber(port)
                .httpEntity(token, changePasswordDto)
                .responseDataType(new ParameterizedTypeReference<>() {
                })
                .restTemplate(restTemplate)
                .build();

        return request.sendRequest();

    }

    /*
     * Delete coach account test utils
     * */
    public ResponseEntity<String> attemptDeleteCoachAccountPassword(DeleteAccountDto deleteAccountDto, String token) {
        /*
         * this method encapsulates the process of sending a DELETE request to "/api/coach-account-manager"
         * deleteAccountDto: contains identifier for the coach to be deleted.
         * token: is the access token to be added as a bearer token to the authorization header.
         * */

        Request.Builder<DeleteAccountDto, String> requestBuilder = new Request.Builder<>();

        Request<DeleteAccountDto, String> request = requestBuilder
                .endPointUrl("/coach-account-manager")
                .httpMethod(HttpMethod.DELETE)
                .portNumber(port)
                .httpEntity(token, deleteAccountDto)
                .responseDataType(new ParameterizedTypeReference<>() {
                })
                .restTemplate(restTemplate)
                .build();

        return request.sendRequest();

    }

    public void shouldDeleteCoachAccount(DeleteAccountDto deleteAccountDto, String token) {
        /*
         * utility method for delete coach account test.
         * encapsulates the check for successful deletion of coach account.
         * */
        ResponseEntity<String> response = attemptDeleteCoachAccountPassword(deleteAccountDto, token);
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    }
}
