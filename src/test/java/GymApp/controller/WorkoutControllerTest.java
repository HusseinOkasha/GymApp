//package GymApp.controller;
//
//import GymApp.entity.*;
//import GymApp.enums.WorkoutAccessType;
//import GymApp.service.*;
//import GymApp.util.GeneralUtil;
//import GymApp.util.WorkoutControllerTestUtil;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.web.server.LocalServerPort;
//import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
//import org.springframework.web.client.RestTemplate;
//import org.testcontainers.containers.PostgreSQLContainer;
//import org.testcontainers.junit.jupiter.Container;
//import org.testcontainers.junit.jupiter.Testcontainers;
//
//import java.time.LocalDate;
//import java.util.List;
//
//import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
//
//@Testcontainers
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
//        properties = { "spring.datasource.url=jdbc:tc:postgres:latest:///database", "spring.sql.init.mode=always" })
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
//class WorkoutControllerTest {
//    @Autowired
//    private OwnerService ownerService;
//
//    @Autowired
//    private CoachService coachService;
//
//    @Autowired
//    private ClientService clientService;
//
//    @Autowired
//    private AccountWorkoutService accountWorkoutService;
//
//    @Autowired
//    private WorkoutService workoutService;
//
//    @Autowired
//    private RestTemplate restTemplate;
//
//    @LocalServerPort
//    private int port;
//
//    @Container
//    @ServiceConnection
//    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest")
//            .withDatabaseName("db").withUsername("user");
//
//    static private Owner unAuthenticatedOwner;
//    static private Coach unAuthenticatedCoach;
//    static private Client unAuthenticatedClient;
//
//    static private Owner authenticatedOwner;
//    static private Coach authenticatedCoach;
//    static private Client authenticatedClient;
//
//    static private Workout ownerWorkout; // workout created by owner
//    static private Workout coachWorkout; // workout created by coach
//    static private Workout clientWorkout; // workout created by client
//    static private Workout commonWorkout; // workout that is common between (owner, coach, client)
//    static private Workout workout; //  sample workout.
//
//    static String bCryptPassword;
//    static String rawPassword;
//    static String ownerToken;
//    static String coachToken;
//    static String clientToken;
//
//    @BeforeAll
//    static void generalSetUp(){
//        rawPassword = "123";
//        // bCrypt password for raw password 123.
//        bCryptPassword = "$2a$12$fdQCjXHktjZczz5hlHg77u8bIXUQdzGQf5k7ulN.cxzhW2vidHzSu";
//
//        // create sample owner accounts one will be authenticated and the other won't.
//        Owner.Builder ownerBuilder = new Owner.Builder();
//        Account.Builder accountBuilder = new Account.Builder();
//        authenticatedOwner =  ownerBuilder
//                .account(
//                        accountBuilder.firstName("f1").secondName("s1").thirdName("t1").email("e1@email.com")
//                                .phoneNumber("1").password(bCryptPassword).build()
//                ).build();
//        unAuthenticatedOwner = ownerBuilder
//                .account(
//                        accountBuilder.firstName("f2").secondName("s2").thirdName("t2").email("e2@email.com")
//                                .phoneNumber("2").password(bCryptPassword).build()
//                ).build();
//
//        // create sample coach accounts one will be authenticated and the other won't
//        Coach.Builder coachBuilder = new Coach.Builder();
//        authenticatedCoach =  coachBuilder
//                .account(
//                        accountBuilder.firstName("f3").secondName("s3").thirdName("t3").email("e3@email.com")
//                                .phoneNumber("3").password(bCryptPassword).build()
//                ).build();
//
//        unAuthenticatedCoach =  coachBuilder
//                .account(
//                        accountBuilder.firstName("f4").secondName("s4").thirdName("t4").email("e4@email.com")
//                                .phoneNumber("4").password(bCryptPassword).build()
//                ).build();
//
//        // create sample client accounts one will be authenticated and the other won't.
//        Client.Builder clientBuilder = new Client.Builder();
//        authenticatedClient = clientBuilder
//                .account(
//                        accountBuilder.firstName("f5").secondName("s5").thirdName("t5").email("e5@email.com")
//                                .phoneNumber("5").password(bCryptPassword).build()
//                ).birthDate(LocalDate.of(2024,3,1)).build();
//        unAuthenticatedClient =  clientBuilder
//                .account(
//                        accountBuilder.firstName("f6").secondName("s6").thirdName("t6").email("e6@email.com")
//                                .phoneNumber("6").password(bCryptPassword).build()
//                ).birthDate(LocalDate.of(2024,3,1)).build();
//
//        // create sample exercises
//        Exercise.Builder exerciseBuilder = new Exercise.Builder();
//        Exercise exercise1 = exerciseBuilder.sets(3).reps(10).notes("").name("ex1").build();
//        Exercise exercise2 = exerciseBuilder.sets(3).reps(10).notes("").name("ex2").build();
//
//        // create sample workouts
//        Workout.Builder workoutBuilder = new Workout.Builder();
//        ownerWorkout = workoutBuilder.name("workout1").exercises(List.of(exercise1, exercise2)).build();
//        coachWorkout = workoutBuilder.name("workout2").exercises(List.of(exercise1, exercise2)).build();
//        clientWorkout = workoutBuilder.name("workout3").exercises(List.of(exercise1, exercise2)).build();
//        commonWorkout = workoutBuilder.name("workout4").exercises(List.of(exercise1, exercise2)).build();
//        workout = workoutBuilder.name("workout5").exercises(List.of(exercise1, exercise2)).build();
//
//
//    }
//
//    @BeforeEach
//    void setUp() {
//        // persist sample Owners
//        ownerService.save(authenticatedOwner);
//        ownerService.save(unAuthenticatedOwner);
//
//        // persist sample coaches
//        coachService.save(authenticatedCoach);
//        coachService.save(unAuthenticatedCoach);
//
//        // persist sample clients
//        clientService.save(authenticatedClient);
//        clientService.save(unAuthenticatedClient);
//
//        // persist sample workout
//        workoutService.save(ownerWorkout);
//        workoutService.save(coachWorkout);
//        workoutService.save(clientWorkout);
//        workoutService.save(commonWorkout);
//
//        AccountWorkout.Builder accountWorkoutBuilder = new AccountWorkout.Builder();
//
//        // link sample workouts to sample owners / coaches / clients.
//        accountWorkoutService.save(accountWorkoutBuilder
//                .id(new AccountWorkout.Id(authenticatedOwner.getAccount().getId(), ownerWorkout.getId()))
//                .accessType(WorkoutAccessType.WRITE)
//                .build());
//        accountWorkoutService.save(accountWorkoutBuilder
//                .id(new AccountWorkout.Id(authenticatedCoach.getAccount().getId(), coachWorkout.getId()))
//                .accessType(WorkoutAccessType.WRITE)
//                .build());
//        accountWorkoutService.save(accountWorkoutBuilder
//                .id(new AccountWorkout.Id(authenticatedClient.getAccount().getId(), clientWorkout.getId()))
//                .accessType(WorkoutAccessType.WRITE)
//                .build());
//
//        // link authenticated(owner / coach / client) to the common workout.
//        accountWorkoutService.save(accountWorkoutBuilder
//                .id(new AccountWorkout.Id(authenticatedOwner.getAccount().getId(), commonWorkout.getId()))
//                .accessType(WorkoutAccessType.READ)
//                .build());
//        accountWorkoutService.save(accountWorkoutBuilder
//                .id(new AccountWorkout.Id(authenticatedCoach.getAccount().getId(), commonWorkout.getId()))
//                .accessType(WorkoutAccessType.READ)
//                .build());
//        accountWorkoutService.save(accountWorkoutBuilder
//                .id(new AccountWorkout.Id(authenticatedClient.getAccount().getId(), commonWorkout.getId()))
//                .accessType(WorkoutAccessType.READ)
//                .build());
//
//
//
//        // get owner token.
//        ownerToken = GeneralUtil.login(authenticatedOwner.getAccount().getEmail(), rawPassword,
//                GeneralUtil.getBaseUrl(port)+"/login/owner", restTemplate);
//        // get coach token.
//        coachToken = GeneralUtil.login(authenticatedCoach.getAccount().getEmail(), rawPassword,
//                GeneralUtil.getBaseUrl(port)+"/login/coach", restTemplate);
//
//        // get client token.
//        clientToken = GeneralUtil.login(authenticatedClient.getAccount().getEmail(), rawPassword,
//                GeneralUtil.getBaseUrl(port)+"/login/client", restTemplate);
//
//    }
//
//    @AfterEach
//    void tearDown() {
//        // clear the database
//        ownerService.deleteAll();
//        clientService.deleteAll();
//        coachService.deleteAll();
//        workoutService.deleteAll();
//        accountWorkoutService.deleteAll();
//    }
//
//
//    @Test
//    public void ownerShouldCreateWorkout(){
//        // this method tests the ability for authenticated coach to create workout.
//
//        // shouldCreateWorkout is helper method that encapsulates the logic of testing.
//        WorkoutControllerTestUtil.shouldCreateWorkout(workout, ownerToken, port ,restTemplate);
//    }
//
//    @Test
//    public void coachShouldCreateWorkout(){
//        // this method tests the ability for authenticated coach to create workout.
//
//        // shouldCreateWorkout is helper method that encapsulates the logic of testing.
//        WorkoutControllerTestUtil.shouldCreateWorkout(workout, coachToken, port ,restTemplate);
//    }
//
//    @Test
//    public void clientShouldCreateWorkout(){
//        // this method tests the ability for authenticated client to create workout.
//
//        // shouldCreateWorkout is helper method that encapsulates the logic testing.
//        WorkoutControllerTestUtil.shouldCreateWorkout(workout, clientToken, port ,restTemplate);
//    }
//
//    @Test
//    public void ShouldNotCreateWorkoutWithoutToken(){
//        // this method tests "that creation of workout without authentication token isn't valid".
//        WorkoutControllerTestUtil.shouldNotCreateWorkout(workout, port, restTemplate);
//    }
//    @Test
//    public void OwnerShouldGetWorkoutById(){
//        // this method tests the ability of authenticated owner to get any of his workouts by id.
//
//        // his workouts mean workouts that he has read / write access to it.
//        WorkoutControllerTestUtil.shouldGetWorkoutById(ownerWorkout, ownerToken, port, restTemplate);
//    }
//    @Test
//    public void coachShouldGetWorkoutById(){
//        // this method tests the ability of authenticated owner to get any of his workouts by id.
//
//        // his workouts mean workouts that he has read / write access to it.
//        WorkoutControllerTestUtil.shouldGetWorkoutById(coachWorkout, coachToken, port, restTemplate);
//    }
//
//    @Test
//    public void clientShouldGetWorkoutById(){
//        // this method tests the ability of authenticated owner to get any of his workouts by id.
//
//        // his workouts mean workouts that he has read / write access to it.
//        WorkoutControllerTestUtil.shouldGetWorkoutById(clientWorkout, clientToken, port, restTemplate);
//    }
//
//    @Test
//    public void shouldNotGetWorkoutByIdWithoutToken(){
//        // this method tests "that getting workout by id without authentication token isn't valid".
//
//        // here I try to get each of sample workouts by id.
//        WorkoutControllerTestUtil.shouldNotGetWorkoutById(ownerWorkout, null, port, restTemplate);
//        WorkoutControllerTestUtil.shouldNotGetWorkoutById(coachWorkout, null, port, restTemplate);
//        WorkoutControllerTestUtil.shouldNotGetWorkoutById(clientWorkout, null, port, restTemplate);
//    }
//
//    @Test
//    public void ownerShouldNotGetWorkoutById(){
//        // this method tests "that owner can't get workout by id which he has no access to".
//
//        // in this test we try to get "coachWorkout" by using "ownerToken".
//        WorkoutControllerTestUtil.shouldNotGetWorkoutById(coachWorkout, ownerToken, port, restTemplate);
//
//        // in this test we try to get "clientWorkout" by using "ownerToken".
//        WorkoutControllerTestUtil.shouldNotGetWorkoutById(clientWorkout, ownerToken, port, restTemplate);
//    }
//    @Test
//    public void coachShouldNotGetWorkoutById(){
//        // this method tests "that coach can't get workout by id which he has no access to".
//
//        // in this test we try to get "ownerWorkout" by using "coachToken".
//        WorkoutControllerTestUtil.shouldNotGetWorkoutById(ownerWorkout, coachToken, port, restTemplate);
//
//        // in this test we try to get "clientWorkout" by using "coachToken".
//        WorkoutControllerTestUtil.shouldNotGetWorkoutById(clientWorkout, coachToken, port, restTemplate);
//    }
//    @Test
//    public void clientShouldNotGetWorkoutById(){
//        // this method tests "that client can't get workout by id which he has no access to".
//
//        // in this test we try to get "ownerWorkout" by using "clientToken".
//        WorkoutControllerTestUtil.shouldNotGetWorkoutById(ownerWorkout, clientToken, port, restTemplate);
//
//        // in this test we try to get "coachWorkout" by using "clientToken".
//        WorkoutControllerTestUtil.shouldNotGetWorkoutById(coachWorkout, clientToken, port, restTemplate);
//    }
//
//    @Test
//    public void ownerShouldGetAllWorkout(){
//        // this method tests "that owner can get all his workouts".
//        WorkoutControllerTestUtil.shouldGetAllWorkouts(List.of(ownerWorkout, commonWorkout), ownerToken, port,
//                restTemplate);
//    }
//
//    @Test
//    public void coachShouldGetAllWorkout(){
//        // this method tests "that coach can get all his workouts".
//        WorkoutControllerTestUtil.shouldGetAllWorkouts(List.of(coachWorkout, commonWorkout), coachToken, port,
//                restTemplate);
//    }
//    @Test
//    public void clientShouldGetAllWorkout(){
//        // this method tests "that client can get all his workouts".
//        WorkoutControllerTestUtil.shouldGetAllWorkouts(List.of(clientWorkout, commonWorkout), clientToken, port,
//                restTemplate);
//    }
//
//    @Test
//    public void shouldNotGetAllWorkoutsWithoutToken(){
//        // this method tests "that client can get all his workouts".
//        WorkoutControllerTestUtil.shouldNotGetAllWorkouts(null, port, restTemplate);
//    }
//    @Test
//    public void ownerShouldUpdateWorkoutById(){
//        // this method tests the ability of "Owner to update his own workouts by id"
//        // workouts that he has write access to it.
//        ownerWorkout.setName("name update");
//        Exercise.Builder exerciseBuilder = new Exercise.Builder();
//        ownerWorkout.setExercises(
//                List.of(
//                        exerciseBuilder.name("ex1 updated").sets(3).reps(10).build(),
//                        exerciseBuilder.name("ex2 updated").sets(3).reps(10).build()
//                )
//        );
//        WorkoutControllerTestUtil.shouldUpdateWorkoutById(ownerToken, ownerWorkout, port,restTemplate);
//    }
//    @Test
//    public void coachShouldUpdateWorkoutById(){
//        // this method tests the ability of "coach to update his own workouts by id"
//        // workouts that he has write access to it.
//        coachWorkout.setName("name update");
//        Exercise.Builder exerciseBuilder = new Exercise.Builder();
//        coachWorkout.setExercises(
//                List.of(
//                        exerciseBuilder.name("ex1 updated").sets(3).reps(10).build(),
//                        exerciseBuilder.name("ex2 updated").sets(3).reps(10).build()
//                )
//        );
//        WorkoutControllerTestUtil.shouldUpdateWorkoutById(coachToken, coachWorkout, port,restTemplate);
//    }
//    @Test
//    public void clientShouldUpdateWorkoutById(){
//        // this method tests the ability of "client to update his own workouts by id"
//        // workouts that he has write access to it.
//        clientWorkout.setName("name update");
//        Exercise.Builder exerciseBuilder = new Exercise.Builder();
//        clientWorkout.setExercises(
//                List.of(
//                        exerciseBuilder.name("ex1 updated").sets(3).reps(10).build(),
//                        exerciseBuilder.name("ex2 updated").sets(3).reps(10).build()
//                )
//        );
//        WorkoutControllerTestUtil.shouldUpdateWorkoutById(clientToken, clientWorkout, port, restTemplate);
//    }
//    @Test
//    public void ownerShouldNotUpdateWorkoutById(){
//        // this method tests that "An owner can't update workouts by id that he has no write access to it."
//
//        // owner has no access to this workout.
//        WorkoutControllerTestUtil.shouldNotUpdateWorkoutById(ownerToken, coachWorkout, port, restTemplate);
//
//        // owner has no access to this workout.
//        WorkoutControllerTestUtil.shouldNotUpdateWorkoutById(ownerToken, clientWorkout, port, restTemplate);
//
//        // owner has only read access to this workout.
//        WorkoutControllerTestUtil.shouldNotUpdateWorkoutById(ownerToken, commonWorkout, port, restTemplate);
//    }
//    @Test
//    public void coachShouldNotUpdateWorkoutById(){
//        // this method tests that "An client can't update workouts by id that he has no write access to it."
//
//        // coach has no access to this workout.
//        WorkoutControllerTestUtil.shouldNotUpdateWorkoutById(coachToken, ownerWorkout, port, restTemplate);
//
//        // coach has no access to this workout.
//        WorkoutControllerTestUtil.shouldNotUpdateWorkoutById(coachToken, clientWorkout, port, restTemplate);
//
//        // coach has only read access to this workout.
//        WorkoutControllerTestUtil.shouldNotUpdateWorkoutById(coachToken, commonWorkout, port, restTemplate);
//    }
//    @Test
//    public void clientShouldNotUpdateWorkoutById(){
//        // this method tests that "An client can't update workouts by id that he has no write access to it."
//
//        // client has no access to this workout.
//        WorkoutControllerTestUtil.shouldNotUpdateWorkoutById(clientToken, ownerWorkout, port, restTemplate);
//
//        // client has no access to this workout.
//        WorkoutControllerTestUtil.shouldNotUpdateWorkoutById(clientToken, coachWorkout, port, restTemplate);
//
//        // client has only read access to this workout.
//        WorkoutControllerTestUtil.shouldNotUpdateWorkoutById(clientToken, commonWorkout, port, restTemplate);
//    }
//    @Test
//    public void ownerShouldDeleteWorkoutById(){
//        // this method tests the ability of owner to delete any of his workouts by id
//        // if he has write access to the workout, then he can delete the workout itself.
//        // if he has read access to the workout, then he can delete the workout from his list of workouts (delete the link only).
//        WorkoutControllerTestUtil.shouldDeleteWorkoutById(ownerToken,  ownerWorkout,  port, restTemplate);
//    }
//
//    @Test
//    public void coachShouldDeleteWorkoutById(){
//        // this method tests the ability of coach to delete any of his workouts by id
//        // if he has write access to the workout, then he can delete the workout itself.
//        // if he has read access to the workout, then he can delete the workout from his list of workouts (delete the link only).
//        WorkoutControllerTestUtil.shouldDeleteWorkoutById(coachToken, coachWorkout, port, restTemplate);
//    }
//    @Test
//    public void clientShouldDeleteWorkoutById(){
//        // this method tests the ability of client to delete any of his workouts by id
//        // if he has write access to the workout, then he can delete the workout itself.
//        // if he has read access to the workout, then he can delete the workout from his list of workouts (delete the link only).
//        WorkoutControllerTestUtil.shouldDeleteWorkoutById(clientToken, clientWorkout,  port, restTemplate);
//    }
//
//    @Test
//    public void ownerShouldNotDeleteWorkoutById(){
//        // this method tests that "owner can't delete workout that he has no access to it"
//
//        // owner has no access to this workout.
//        WorkoutControllerTestUtil.shouldNotDeleteWorkoutById(ownerToken, coachWorkout, port, restTemplate);
//
//        // owner has no access to this workout.
//        WorkoutControllerTestUtil.shouldNotDeleteWorkoutById(ownerToken, clientWorkout, port, restTemplate);
//    }
//
//    @Test
//    public void coachShouldNotDeleteWorkoutById(){
//        // this method tests that "coach can't delete workout that he has no access to it"
//
//        // coach has no access to this workout.
//        WorkoutControllerTestUtil.shouldNotDeleteWorkoutById(coachToken, ownerWorkout, port, restTemplate);
//
//        // coach has no access to this workout.
//        WorkoutControllerTestUtil.shouldNotDeleteWorkoutById(coachToken, clientWorkout, port, restTemplate);
//    }
//
//    @Test
//    public void clientShouldNotDeleteWorkoutById(){
//        // this method tests that "coach can't delete workout that he has no access to it"
//
//        // coach has no access to this workout.
//        WorkoutControllerTestUtil.shouldNotDeleteWorkoutById(clientToken, ownerWorkout, port, restTemplate);
//
//        // coach has no access to this workout.
//        WorkoutControllerTestUtil.shouldNotDeleteWorkoutById(clientToken, coachWorkout, port, restTemplate);
//    }
//
//
//
//
//}