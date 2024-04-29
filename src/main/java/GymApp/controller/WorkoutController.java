package GymApp.controller;

import GymApp.dto.WorkoutDto;
import GymApp.entity.Account;
import GymApp.entity.AccountWorkout;
import GymApp.entity.Workout;
import GymApp.service.AccountService;
import GymApp.service.AccountWorkoutService;
import GymApp.service.WorkoutService;
import GymApp.util.entityAndDtoMappers.WorkoutMapper;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    private WorkoutDto createWorkout(@RequestBody @Valid WorkoutDto workoutDto, Authentication authentication) throws Exception {
        // 1) convert createWorkoutDto to "workout" entity
        Workout workout = WorkoutMapper.workoutDtoToWorkoutEntity(workoutDto);

        // 2) fetch the account from the database.
        Optional<Account> result = accountService.findByEmailOrPhoneNumber(authentication.getName(),authentication.getName());
        Account account  = result.get();
        // 2) save the workout to the database.
        Workout dbworkout = workoutService.save(workout);

        // 4) create account_workout entity(link).
        AccountWorkout accountWorkout = new AccountWorkout(new AccountWorkout.Id(workout.getId(), account.getId()),result.get(), workout);
        accountWorkoutService.save(accountWorkout);

        // 5) convert the workout entity to workoutDto.
        return WorkoutMapper.workoutEntityToWorkoutDto(dbworkout);
    }

}
