package GymApp.dao;

import GymApp.entity.Workout;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkoutRepository extends JpaRepository<Workout, Long> {

    List<Workout> findByUserWorkouts_Id_UserId(long id);
}
