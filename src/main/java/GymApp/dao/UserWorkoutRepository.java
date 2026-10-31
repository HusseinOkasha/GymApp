package GymApp.dao;

import GymApp.entity.UserWorkout;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserWorkoutRepository extends JpaRepository<UserWorkout, UserWorkout.Id> {
    @Query("SELECT aw FROM UserWorkout aw WHERE aw.id.userId = :accountId AND aw.id.workoutId = :workoutId")
    Optional<UserWorkout> findByAccountIdAndWorkoutId(long accountId, long workoutId);
}