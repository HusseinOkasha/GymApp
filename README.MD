# GYM APP
It's the backend of GYM management app.

## How to run
* `git clone https://github.com/HusseinOkasha/GymApp.git`
* `cd GymApp`
* `docker-compose -f docker-compose-prod.yml up`
  * Running this command will clone the GymApp repo from gitHub.
  * It will create 2 containers one for the GymApp and the other for postgreSQL.
  * The Gym app will be running on port 8080.
* Send post request to the init controller, which will create owner user, so you can try different features.
  * `curl -X POST http://localhost:8080/api/init`
## User Guide
It contains curl commands trying different features in the project [User Guide](https://thin-move-11a.notion.site/User-Guide-6338255eefbc4362b5c952132c801622#c58b835e7c9d457db8a09a47e562e404)

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
* Owner can CREATE owner user through
  * `POST http://localhost:8080/api/owner-user-manager`

* Owner can get his user details through
  * `GET http://localhost:8080/api/owner-user-manager`

* Owner can list all owner accounts through
  * `GET http://localhost:8080/api/owner-user-manager/all`

* Owner can update his user through
  * `PUT http://localhost:8080/api/owner-user-manager`

* Owner can change his user password through
  * `PUT "http://localhost:8080/api/owner-user-manager/password`

* Owner can Delete his own user through
  * `DELETE http://localhost:8080/api/owner-user-manager`


### Coach Account Manager
* **Owner** can create coach user through
  * `POST http://localhost:8080/api/coach-user-manager`

* **Coach** can get his user details through
  * `GET  http://localhost:8080/api/coach-user-manager`

* **Owner** can get list of all coaches through
  * `GET http://localhost:8080/api/coach-user-manager/coaches`

* **Coach** can update his own profile through
  * `PUT http://localhost:8080/api/coach-user-manager`

* **Coach** can change his user password through
  * `PUT http://localhost:8080/api/coach-user-manager/password`

* **Owner** can delete coach user through
  * `DELETE http://localhost:8080/api/coach-user-manager`


### Client Account Manager
* **Owner** can create new client user through
  * `POST http://localhost:8080/api/client-user-manager`

* **Client** can get his user details through
  * `GET http://localhost:8080/api/client-user-manager`
    
* **Owner / Coach** can get list of all clients through
  * `GET http://localhost:8080/api/client-user-manager/clients`
    
* **Client** can update his user details through
  * `PUT http://localhost:8080/api/client-user-manager`
    
* **Client** can change his password through
  * `PUT http://localhost:8080/api/client-user-manager/password`
   
* **Owner** can delete client user through
  * `Delete http://localhost:8080/api/client-user-manager`

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
