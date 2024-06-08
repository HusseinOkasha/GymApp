# GYM APP
It's the backend of GYM management app.

## How to run
* `git clone https://github.com/HusseinOkasha/GymAppDockerization.git`
  * This repo contains the necessary docker files to build an image from the project.
* `cd GymAppDockerization`
* `docker compose up`
  * Running this command will clone the GymApp repo from gitHub.
  * It will create 2 containers one for the GymApp and the other for postgreSQL.
  * The Gym app will be running on port 8080.
* Send post request to the init controller, which will create owner account, so you can try different features.
  * `curl -X POST http://localhost:8080/api/init`

### Tests
* `git clone https://github.com/HusseinOkasha/GymApp.git`
* Open the project with intellij
* You can run the tests
* Tests include integration tests for all controllers (built with testcontainers).


## Functionalities
### Login
* **Owner** login with email or phone number, and  password.
  * By sending post request to `http://localhost:8080/api/login/owner`
* **Coach** login with email or phone number, and password
  * By sending post request to `http://localhost:8080/api/login/coach`
* **Client** login with email or phone number, and password.
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

### Workout Controller
Owner, coach, and client can CRUD their own workouts.
* Create workout through
  * `POST http://localhost:8080/api/workouts`
    
* Get all his workouts through
  * `GET http://localhost:8080/api/workouts`
   
* Get any of his workouts by id through
  * `GET http://localhost:8080/api/workouts/{id}`
    
* Update any of his workouts by id through
  * `PUT http://localhost:8080/api/workouts/{id}`
    
* Delete any of his workouts by id
  * `DELETE http://localhost:8080/api/workouts/{id}`

I have provided integrations tests for these functionalities using test containers.
***
