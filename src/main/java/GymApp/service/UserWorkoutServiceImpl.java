package GymApp.service;


import GymApp.dao.UserWorkoutRepository;
import GymApp.entity.UserWorkout;
import GymApp.service.Intrface.UserWorkoutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserWorkoutServiceImpl implements UserWorkoutService {

    @Autowired
    private UserWorkoutRepository userWorkoutRepository;

    @Override
    public List<UserWorkout> findAll() {
        return userWorkoutRepository.findAll();
    }

    @Override
    public Optional<UserWorkout> findById(UserWorkout.Id id) {
        return userWorkoutRepository.findById(id);
    }

    @Override
    public Optional<UserWorkout> findByAccountIdAndWorkoutId(long accountId, long workoutId) {
        return userWorkoutRepository.findByAccountIdAndWorkoutId(accountId, workoutId);
    }

    @Override
    public UserWorkout save(UserWorkout userWorkout) {
        return userWorkoutRepository.save(userWorkout);
    }

    @Override
    public void deleteById(UserWorkout.Id id) {
        userWorkoutRepository.deleteById(id);
    }

    @Override
    public void deleteAll() {
        userWorkoutRepository.deleteAll();
    }
}
