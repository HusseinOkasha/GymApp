package GymApp.controller;

import GymApp.dto.WorkoutDto;
import GymApp.entity.AccountWorkout;
import GymApp.entity.Exercise;
import GymApp.entity.Workout;
import GymApp.exception.WorkoutNotFoundException;
import GymApp.service.AccountService;
import GymApp.service.AccountWorkoutService;
import GymApp.service.WorkoutService;
import GymApp.util.entityAndDtoMappers.ExerciseMapper;
import GymApp.util.entityAndDtoMappers.WorkoutMapper;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class WorkoutController {

    @Autowired
    private WorkoutService workoutService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private AccountWorkoutService accountWorkoutService;

    @PostMapping("/workouts")
    @Validated
    public WorkoutDto createWorkout(@RequestBody @Valid WorkoutDto workoutDto, Authentication authentication) throws Exception {
        // 1) convert createWorkoutDto to "workout" entity
        Workout workout = WorkoutMapper.workoutDtoToWorkoutEntity(workoutDto);

        // 2) save the workout to the database.
        Workout dbworkout = workoutService.save(workout);

        // 3) get the account id from the authentication object.
        long accountId = Long.parseLong(authentication.getName());

        // 4) create account_workout entity(link).
        AccountWorkout accountWorkout = new AccountWorkout(new AccountWorkout.Id(workout.getId(), accountId));
        accountWorkoutService.save(accountWorkout);

        // 5) convert the workout entity to workoutDto.
        return WorkoutMapper.workoutEntityToWorkoutDto(dbworkout);
    }

    @GetMapping("/workouts")
    @Validated
    public List<WorkoutDto> getAllWorkouts(Authentication authentication) throws Exception {
        // 1) get the account_id from the authentication object.
        //      note: Look at the authentication provider "check password" method  for clarification.
        long account_id = Long.parseLong(authentication.getName());

        // 2) fetch all workouts which that account has using the account id
        List<Workout> workouts = workoutService.findByAccountId(account_id);

        // 3) convert workout entity to workoutDto.
        return workouts.stream().map(WorkoutMapper::workoutEntityToWorkoutDto).toList();
    }

    @Validated
    @GetMapping("/workouts/{workoutId}")
    public WorkoutDto getWorkoutById(@PathVariable long workoutId, Authentication authentication) throws Exception {
        // 1) get the account_id from the authentication object.
        //      note: Look at the authentication provider "check password" method  for clarification.
        long accountId = Long.parseLong(authentication.getName());

        // 2) Check if this workout belongs to that user.
        Optional<AccountWorkout> result = accountWorkoutService.findByAccountIdAndWorkoutId(accountId, workoutId);
        AccountWorkout dbAccountWorkout = result.orElseThrow(
                () -> new AccessDeniedException("You can't access this workout")
        );

        // 3) Get the required workout.
        Workout dbWorkout = dbAccountWorkout.getWorkout();

        // 4) convert workout entity to workoutDto.
        return WorkoutMapper.workoutEntityToWorkoutDto(dbWorkout);
    }

    @Validated
    @PutMapping("/workouts/{workoutId}")
    public WorkoutDto updateWorkoutById(@PathVariable long workoutId, @Valid @RequestBody WorkoutDto workoutDto,
                                        Authentication authentication) throws Exception {
        // get the account_id from the authentication object.
        long accountId = Long.parseLong(authentication.getName());

        // check if this workout belongs to that user
        Optional<AccountWorkout> accountWorkoutFetchResult =
                accountWorkoutService.findByAccountIdAndWorkoutId(accountId, workoutId);

        // in case it doesn't belong to that user throw access denied exception.
        accountWorkoutFetchResult.orElseThrow(()-> new AccessDeniedException("you can't access this workout"));

        // in case it does belong to that user.
        // fetch the workout from the database.
        Optional<Workout> workoutFetchResult = workoutService.findById(workoutId);

        // in case the workout doesn't exist throw workout not found exception with NOT_FOUND status code.
        Workout dbWorkout = workoutFetchResult
                .orElseThrow(()-> new WorkoutNotFoundException("no workout found with id: " + workoutId));

        // reflect the updates to the fetched entity.
        dbWorkout.setName(workoutDto.name());

        // convert exercises dto to exercise entity
        List<Exercise> exercises = workoutDto.exercises().stream()
                .map(ExerciseMapper::exerciseDtoToExerciseEntity).toList();
        dbWorkout.getExercises().clear();
        dbWorkout.getExercises().addAll(exercises);

        // save the updated entity.
        dbWorkout = workoutService.save(dbWorkout);

        // convert the save workout entity to workoutDto.
        return WorkoutMapper.workoutEntityToWorkoutDto(dbWorkout);
    }

    @Validated
    @DeleteMapping("/workouts/{workoutId}")
    public void deleteWorkoutById(@PathVariable long workoutId, Authentication authentication){
        // get the account id from the authentication object.
        long accountId = Long.parseLong(authentication.getName());

        // check if that workout belongs to that user
        Optional<AccountWorkout>accountWorkoutFetchResult =
                accountWorkoutService.findByAccountIdAndWorkoutId(accountId, workoutId);

        // throw Access denied exception in case the workout doesn't belong to the user
        AccountWorkout dbAccountWorkout = accountWorkoutFetchResult.orElseThrow(()-> new AccessDeniedException(""));

        // in case the workout does belong to the user, delete it.
        accountWorkoutService.deleteById(new AccountWorkout.Id(workoutId, accountId));

        workoutService.deleteById(workoutId);
    }


}
