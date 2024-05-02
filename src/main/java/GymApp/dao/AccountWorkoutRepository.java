package GymApp.dao;

import GymApp.entity.AccountWorkout;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface AccountWorkoutRepository extends JpaRepository<AccountWorkout, AccountWorkout.Id> {
    @Query("SELECT aw FROM AccountWorkout aw WHERE aw.id.accountId = :accountId AND aw.id.workoutId = :workoutId")
    Optional<AccountWorkout> findByAccountIdAndWorkoutId(long accountId, long workoutId);
}