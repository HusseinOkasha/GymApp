# GYM APP
It's the backend of GYM management app.  

## Working Functionalities
* Owner, Coach, and Client can log in to their accounts through
  * POST "http://localhost:8080/api/login/owner"
  * POST "http://localhost:8080/api/login/owner"
  * POST "http://localhost:8080/api/login/owner"
* Owner can CREATE owner account through
  * POST "http://localhost:8080/api/owner-account-manager"
* Owner can get his account details through
  * GET "http://localhost:8080/api/owner-account-manager"
* Owner can list all owner accounts through
  * GET "http://localhost:8080/api/owner-account-manager/all"
* Owner can update his account through
  * PUT "http://localhost:8080/api/owner-account-manager"
* Owner can change his account password through
  * PUT "http://localhost:8080/api/owner-account-manager/password"
* Owner can Delete his own account through
  * DELETE "http://localhost:8080/api/owner-account-manager"

I have provided tests for these functionalities using test containers.

## How to run

* `git clone https://github.com/HusseinOkasha/GymApp.git`
* Open the project with intellij
* You can run the tests 
* I hava a plan to dockerize the whole app, however now you can run tests to verify that everything is working .   
    
