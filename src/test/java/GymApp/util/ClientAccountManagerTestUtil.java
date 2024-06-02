package GymApp.util;

import GymApp.dto.*;
import GymApp.entity.Account;
import GymApp.entity.Client;
import GymApp.util.entityAndDtoMappers.AccountMapper;
import GymApp.util.entityAndDtoMappers.ClientMapper;
import org.assertj.core.api.Assertions;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class ClientAccountManagerTestUtil {

    // fields
    private int port; // the port number the server is running on as it is generated randomly
    private RestTemplate restTemplate; // used to send requests

    // Constructors
    public ClientAccountManagerTestUtil(int port, RestTemplate restTemplate) {
        this.port = port;
        this.restTemplate = restTemplate;
    }

    /*
    * Create new client account test utils
    * */
    public ResponseEntity<ClientAccountProfileDto> attemptCreateNewClientAccount(Client client, String token ){
        /*
        * It encapsulates the logic of sending a POST request to "api/client-account-manager".
        * client: is the client to be created.
        * token: is the access token to be sent in the authorization header as bearer token.
        * */

        CreateClientAccountDto createClientAccountDto = ClientMapper.clientEntityToCreateClientAccountDto(client);

        Request.Builder<CreateClientAccountDto, ClientAccountProfileDto> requestBuilder = new Request.Builder<>();

        Request<CreateClientAccountDto, ClientAccountProfileDto> request = requestBuilder
                .endPointUrl("/client-account-manager")
                .httpMethod(HttpMethod.POST)
                .portNumber(port)
                .httpEntity(token, createClientAccountDto)
                .responseDataType(new ParameterizedTypeReference<>() {
                })
                .restTemplate(restTemplate)
                .build();

        return request.sendRequest();
    }
    public void shouldNotCreateNewClientAccountWithoutRequiredFields(Client client, String token){
        /*
        * It's a utility method that encapsulates the logic of the test.
        * It checks that the response status code is 400 (BAD_REQUEST).
        * client: is the client to be created.
        * token: is the access token used as bearer token in the authorization header.
        * */
        ResponseEntity<ClientAccountProfileDto> response = attemptCreateNewClientAccount(client, token);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    /*
    * Get my account test utils
    * */
    public ResponseEntity<ClientAccountProfileDto> attemptGetMyAccountDetails(Client client, String token){
        /*
         * It encapsulates the logic of sending a GET request to "api/client-account-manager".
         * client: is the client we want to get his account.
         * token: is the access token to be sent in the authorization header as bearer token.
         * */
        Request.Builder<String, ClientAccountProfileDto> requestBuilder = new Request.Builder<>();

        Request<String, ClientAccountProfileDto> request = requestBuilder
                .endPointUrl("/client-account-manager")
                .httpMethod(HttpMethod.GET)
                .portNumber(port)
                .httpEntity(token, null)
                .responseDataType(new ParameterizedTypeReference<>() {
                })
                .restTemplate(restTemplate)
                .build();

        return request.sendRequest();


    }

    /*
    * Get all client accounts test utils
    * */
    public ResponseEntity<List<ClientAccountProfileDto>> attemptGetAllClients(String token) {
        /*
         * It encapsulates the logic of sending a GET request to "api/client-account-manager".
         * token: is the access token to be sent in the authorization header as bearer token.
         * */
        Request.Builder<String, List<ClientAccountProfileDto>> requestBuilder = new Request.Builder<>();

        Request<String, List<ClientAccountProfileDto>> request = requestBuilder
                .endPointUrl("/client-account-manager/clients")
                .httpMethod(HttpMethod.GET)
                .portNumber(port)
                .httpEntity(token, null)
                .responseDataType(new ParameterizedTypeReference<>() {
                })
                .restTemplate(restTemplate)
                .build();

        return request.sendRequest();


    }
    public void shouldGetAllClients(String token, List<Client> clients){

        List<ClientAccountProfileDto> expected = clients.stream()
                .map(ClientMapper::clientEntityToClientAccountProfileDto)
                .toList();

        ResponseEntity<List<ClientAccountProfileDto>> response = attemptGetAllClients(token);

        List<ClientAccountProfileDto> actual  = response.getBody();

        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        // 1) I have Ignored the id from the comparison as id represents the database id which I couldn't predict
        // before saving the client to the database.
        // 2) I have Ignored fields of time "LocalDateTime" to avoid precision errors.
        assert actual != null;
        actual.forEach(
                (e)-> Assertions.assertThat(e)
                        .usingRecursiveComparison()
                        .ignoringFieldsOfTypes(LocalDateTime.class)
                        .ignoringFields("accountProfileDto.id")
                        .isIn(expected)
        );


    }

    /*
    * update client account test utils
    * */
    public ResponseEntity<ClientAccountProfileDto> attemptUpdateClientAccount(Client client, String token){
        /*
        * It encapsulates the logic of sending a PUT request to "api/client-account-manager".
        * Client: is the updated version of the client we want to update.
        * token: is the access token to be sent in the authorization header as bearer token.
        * */
        ClientAccountProfileDto clientAccountProfileDto = ClientMapper.clientEntityToClientAccountProfileDto(client);

        Request.Builder<ClientAccountProfileDto, ClientAccountProfileDto> requestBuilder = new Request.Builder<>();
        Request<ClientAccountProfileDto, ClientAccountProfileDto> request = requestBuilder
                .endPointUrl("/client-account-manager")
                .httpMethod(HttpMethod.PUT)
                .portNumber(port)
                .httpEntity(token, clientAccountProfileDto)
                .responseDataType(new ParameterizedTypeReference<>() {
                })
                .restTemplate(restTemplate)
                .build();

        return request.sendRequest();
    }
    public Client performUpdatesOnClientAccount(Client client){
        Account clientAccount = client.getAccount();
        clientAccount.setFirstName("updated");
        clientAccount.setSecondName("updated");
        clientAccount.setThirdName("updated");
        clientAccount.setEmail("updated@email.com");
        clientAccount.setPhoneNumber("updated number");

        client.setBirthDate(LocalDate.of(2024,5,31));

        return client;
    }

    /*
    * change client password test utils.
    * */
    public ResponseEntity<String> attemptChangeClientAccountPassword(String password, String token){
        /*
         * It encapsulates the logic of sending a PUT request to "api/client-account-manager/password".
         * password: is the new password we want to set.
         * token: is the access token to be sent in the authorization header as bearer token.
         * */
        ChangePasswordDto changePasswordDto = new ChangePasswordDto(password);

        Request.Builder<ChangePasswordDto, String> requestBuilder = new Request.Builder<>();
        Request<ChangePasswordDto, String> request = requestBuilder
                .endPointUrl("/client-account-manager/password")
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
    * Delete client account test utils
    * */
    public ResponseEntity<String>attemptDeleteClientAccount(DeleteAccountDto deleteAccountDto,String token){
        /*
        * It's a util method that encapsulates the logic of sending DELETE request to "api/client-account-manager"
        * DeleteAccountDto: contains the email (and / or) phone number of the client account you want to delete.
        * token: is the access token to be sent as bearer token in the authorization header of the request.
        * */

        Request.Builder<DeleteAccountDto, String> requestBuilder = new Request.Builder<>();

        Request<DeleteAccountDto, String> request = requestBuilder
                .endPointUrl("/client-account-manager")
                .httpMethod(HttpMethod.DELETE)
                .portNumber(port)
                .httpEntity(token, deleteAccountDto)
                .responseDataType(new ParameterizedTypeReference<>() {
                })
                .restTemplate(restTemplate)
                .build();

        return request.sendRequest();
    }
    public void shouldDeleteClientAccount(DeleteAccountDto deleteAccountDto, String token){
        /*
         * utility method for delete client account test.
         * encapsulates the check for successful deletion of client account.
         * DeleteAccountDto: contains the email (and / or) phone number of the client account you want to delete.
         * token: is the access token to be sent as bearer token in the authorization header of the request.
         * It checks that the response status code is 200 (Ok).
         * */
        ResponseEntity<String> response = attemptDeleteClientAccount(deleteAccountDto, token);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
    public void coachAndClientShouldNotDeleteClientAccount(DeleteAccountDto deleteAccountDto, String token){
        /*
         * utility method for delete client account test.
         * encapsulates the check for successful deletion of client account.
         * DeleteAccountDto: contains the email (and / or) phone number of the client account you want to delete.
         * token: is the access token to be sent as bearer token in the authorization header of the request.
         * Given access token with scope coach / client you should Not be able to delete client account.
         * It checks that the response status code is 403 (FORBIDDEN).
         * */
        ResponseEntity<String> response = attemptDeleteClientAccount(deleteAccountDto, token);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }
}
