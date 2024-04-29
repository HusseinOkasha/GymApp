package GymApp.service;

import GymApp.dao.WorkoutRepository;
import GymApp.entity.Workout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class WorkoutServiceImpl implements  WorkoutService{
    @Autowired
    private WorkoutRepository workoutRepository;


    @Override
    public List<Workout> findAll() {
        return workoutRepository.findAll();
    }

    @Override
    public Optional<Workout> findById(long id) {
        return workoutRepository.findById(id);
    }

    @Override
    public Workout save(Workout workout) {
        return workoutRepository.save(workout);
    }

    @Override
    public void deleteAll() {
        workoutRepository.deleteAll();
    }

    @Override
    public void deleteById(long id) {
        workoutRepository.deleteById(id);
    }
}
