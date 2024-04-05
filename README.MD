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
* Tests include only the happy path. In future ISA I will add tests for the unhappy path.
* I hava a plan to dockerize the whole app, however now you can run tests to verify that everything is working .   


  
