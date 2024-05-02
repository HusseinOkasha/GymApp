package GymApp.controller;

import GymApp.dto.WorkoutDto;
import GymApp.entity.AccountWorkout;
import GymApp.entity.Workout;
import GymApp.service.AccountService;
import GymApp.service.AccountWorkoutService;
import GymApp.service.WorkoutService;
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

}
