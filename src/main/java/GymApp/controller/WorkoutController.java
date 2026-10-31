package GymApp.controller;

import GymApp.dto.WorkoutDto;
import GymApp.entity.UserWorkout;
import GymApp.entity.Exercise;
import GymApp.entity.Workout;
import GymApp.enums.WorkoutAccessType;
import GymApp.exception.WorkoutNotFoundException;
import GymApp.service.Intrface.UserService;
import GymApp.service.Intrface.UserWorkoutService;
import GymApp.service.Intrface.WorkoutService;
import GymApp.util.entityAndDtoMappers.ExerciseMapper;
import GymApp.util.entityAndDtoMappers.WorkoutMapper;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
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
    private UserService userService;

    @Autowired
    private UserWorkoutService userWorkoutService;

    @PostMapping("/workouts")
    @Validated
    public WorkoutDto createWorkout(@RequestBody @Valid WorkoutDto workoutDto,
                                    Authentication authentication) throws Exception {

        // convert WorkoutDto to "workout" entity
        Workout workout = WorkoutMapper.workoutDtoToWorkoutEntity(workoutDto);

        // save the workout to the database.
        Workout dbworkout = workoutService.save(workout);

        // get the account id from the authentication object.
        long accountId = Long.parseLong(authentication.getName());

        // create account_workout entity(link).
        UserWorkout.Builder accountWorkoutBuilder = new UserWorkout.Builder();
        UserWorkout userWorkout = accountWorkoutBuilder.id(new UserWorkout.Id(accountId, dbworkout.getId()))
                                                       .accessType(WorkoutAccessType.WRITE).build();

        // save the account workout to the database.
        userWorkoutService.save(userWorkout);

        // convert the workout entity to workoutDto.
        return WorkoutMapper.workoutEntityToWorkoutDto(dbworkout);
    }

    @GetMapping("/workouts")
    @Validated
    public List<WorkoutDto> getAllWorkouts(Authentication authentication) throws Exception {
        // 1) get the account_id from the authentication object.
        //      note: Look at the authentication provider "check password" method  for clarification.
        long account_id = Long.parseLong(authentication.getName());

        // 2) fetch all workouts which that account has using the account id
        List<Workout> workouts = workoutService.findByUserId(account_id);

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
        Optional<UserWorkout> result = userWorkoutService.findByAccountIdAndWorkoutId(accountId, workoutId);
        UserWorkout dbUserWorkout = result.orElseThrow(
                () -> new AccessDeniedException("You can't access this workout")
        );

        // 3) Get the required workout.
        Workout dbWorkout = dbUserWorkout.getWorkout();

        // 4) convert workout entity to workoutDto.
        return WorkoutMapper.workoutEntityToWorkoutDto(dbWorkout);
    }

    @Validated
    @PutMapping("/workouts/{workoutId}")
    public WorkoutDto updateWorkoutById(@PathVariable long workoutId, @Valid @RequestBody WorkoutDto workoutDto,
                                        Authentication authentication) throws Exception {
        // get the account_id from the authentication object.
        long accountId = Long.parseLong(authentication.getName());

        // check if this user can access this workout.
        Optional<UserWorkout> accountWorkoutFetchResult =
                userWorkoutService.findByAccountIdAndWorkoutId(accountId, workoutId);

        // in case he can't access this workout throw access denied exception.
        UserWorkout userWorkout = accountWorkoutFetchResult.orElseThrow(
                ()-> new AccessDeniedException("you can't access this workout")
        );

        // check if he can update the workout.
        if(userWorkout.getAccessType() == WorkoutAccessType.READ){
            throw new AccessDeniedException("you can't update this workout.");
        }

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

        // check if the user can access this workout.
        Optional<UserWorkout>accountWorkoutFetchResult =
                userWorkoutService.findByAccountIdAndWorkoutId(accountId, workoutId);

        // in case the user has no access to the workout throw Access denied exception
        UserWorkout dbUserWorkout = accountWorkoutFetchResult.orElseThrow(()-> new AccessDeniedException(""));

        // in case the user has READ access to the workout, just delete the link.
        if(dbUserWorkout.getAccessType() == WorkoutAccessType.READ){
            userWorkoutService.deleteById(new UserWorkout.Id(accountId, workoutId));
        }
        else if(dbUserWorkout.getAccessType() == WorkoutAccessType.WRITE){
            // in case the user has WRITE  access to the workout, delete the workout itself
            // note: by deleting the workout any associated link(account_workout) will be deleted as well.
            workoutService.deleteById(workoutId);
        }
    }


}
