# GYM APP
It's the backend of GYM management app.  



## Working Functionalities
### Login
  * **Owner** login with email / phone number, and  password.
    * By sending post request to `http://localhost:8080/api/login/owner`
  * **Coach** login with email / phone number, and password
    * By sending post request to `http://localhost:8080/api/login/coach`
  * **Client** login with email / phone number, and password.
    * By sending post request to `http://localhost:8080/api/login/client`
### Owner Account Manager
* Owner can CREATE owner account through
  * `POST http://localhost:8080/api/owner-account-manager`
* Owner can get his account details through
  * `GET http://localhost:8080/api/owner-account-manager`
* Owner can list all owner accounts through
  * `GET http://localhost:8080/api/owner-account-manager/all`
* Owner can update his account through
  * `PUT http://localhost:8080/api/owner-account-manager`
* Owner can change his account password through
  * `PUT "http://localhost:8080/api/owner-account-manager/password`
* Owner can Delete his own account through
  * `DELETE http://localhost:8080/api/owner-account-manager`

### Coach Account Manager
* **Owner** can create coach account through
  * `POST http://localhost:8080/api/coach-account-manager`
* **Coach** can get his account details through
  * `GET  http://localhost:8080/api/coach-account-manager`
* **Owner** can get list of all coaches through
  * `GET http://localhost:8080/api/coach-account-manager/coaches`
* **Coach** can update his own profile through
  * `PUT http://localhost:8080/api/coach-account-manager`
* **Coach** can change his account password through
  * `PUT http://localhost:8080/api/coach-account-manager/password`
* **Owner** can delete coach account through
  * `DELETE http://localhost:8080/api/coach-account-manager`

### Client Account Manager
* **Owner** can create new client account through
  * `POST http://localhost:8080/api/client-account-manager`
* **Client** can get his account details through
  * `GET http://localhost:8080/api/client-account-manager`
* **Owner / Coach** can get list of all clients through
  * `GET http://localhost:8080/api/client-account-manager/clients`
* **Client** can update his account details through
  * `PUT http://localhost:8080/api/client-account-manager`
* **Client** can change his password through
  * `PUT http://localhost:8080/api/client-account-manager/password`
* **Owner** can delete client account through
  * `Delete http://localhost:8080/api/client-account-manager`

I have provided tests for these functionalities using test containers.

## How to run
### Tests
* `git clone https://github.com/HusseinOkasha/GymApp.git`
* Open the project with intellij
* You can run the tests 
* I hava a plan to dockerize the whole app, however now you can run tests to verify that everything is working .   

### Project Itself
#### setup
* `git clone https://github.com/HusseinOkasha/GymApp.git`
* Make sure that you have docker on your machine.
* Open the project with intellij.
* Run GymAppApplication class under src/main/java.
  * running the project will cause the creation of docker container called gymapp-postgres-1.
* We will start by creating an account & tag that account as owner, coach and client.
  * Open the bash of the docker container. 
    * `docker exec -it gymapp-postgres-1 bash`
  * Connect to the database.
    * `psql -U myuser -d mydatabase`
  * Insert account in the account table.
    * `insert into account (first_name, second_name, third_name, email, password, phone_number ,updated_at, created_at) values('f1',
  's1', 't1', 'e1@gmail.com', '$2a$12$fdQCjXHktjZczz5hlHg77u8bIXUQdzGQf5k7ulN.cxzhW2vidHzSu', '1234' ,' 2024-04-02 14:57:23.123', ' 2024-04-02 14:57:23.123');`
  * Make the previously created account Owner account.
    * To Know the id of the created account.
      * `select * from account;`
    * Make the Previously created account Owner account.
      * `insert into owner (account_id) values (1);`
    * Make the Previously created account Coach account.
      * `insert into coach (account_id) values (1);`
    * Make the Previously created account client account.
      * `insert into client (account_id, birth_date) values (1, '2024-04-02');`
