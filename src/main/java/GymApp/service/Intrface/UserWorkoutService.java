package GymApp.service.Intrface;

import GymApp.entity.UserWorkout;

import java.util.List;
import java.util.Optional;

public interface UserWorkoutService {
    List<UserWorkout> findAll();
    Optional<UserWorkout> findById(UserWorkout.Id id);
    Optional<UserWorkout> findByAccountIdAndWorkoutId(long accountId, long workoutId);
    UserWorkout save(UserWorkout userWorkout);
    void deleteById(UserWorkout.Id id);
    void deleteAll();
}
