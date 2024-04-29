package GymApp.service;

import GymApp.entity.Workout;

import java.util.List;
import java.util.Optional;

public interface WorkoutService {
    List<Workout> findAll();
    Optional<Workout> findById(long id);
    Workout save(Workout workout);
    void deleteAll();
    void deleteById(long id);

}
