package GymApp.service;

import GymApp.entity.AccountWorkout;

import java.util.List;
import java.util.Optional;

public interface AccountWorkoutService {
    List<AccountWorkout> findAll();
    Optional<AccountWorkout> findById(AccountWorkout.Id id);
    AccountWorkout save(AccountWorkout accountWorkout);
    void deleteById(AccountWorkout.Id id);
    void deleteAll();
}
