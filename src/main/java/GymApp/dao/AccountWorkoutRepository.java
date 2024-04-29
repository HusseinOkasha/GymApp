package GymApp.dao;

import GymApp.entity.AccountWorkout;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountWorkoutRepository extends JpaRepository<AccountWorkout, AccountWorkout.Id> {
}
