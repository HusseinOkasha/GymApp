package GymApp.service;

import GymApp.dao.AccountWorkoutRepository;
import GymApp.entity.AccountWorkout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AccountWorkoutServiceImpl implements AccountWorkoutService {

    @Autowired
    private AccountWorkoutRepository accountWorkoutRepository;

    @Override
    public List<AccountWorkout> findAll() {
        return accountWorkoutRepository.findAll();
    }

    @Override
    public Optional<AccountWorkout> findById(AccountWorkout.Id id) {
        return accountWorkoutRepository.findById(id);
    }

    @Override
    public Optional<AccountWorkout> findByAccountIdAndWorkoutId(long accountId, long workoutId) {
        return accountWorkoutRepository.findByAccountIdAndWorkoutId(accountId, workoutId);
    }

    @Override
    public AccountWorkout save(AccountWorkout accountWorkout) {
        return accountWorkoutRepository.save(accountWorkout);
    }

    @Override
    public void deleteById(AccountWorkout.Id id) {
        accountWorkoutRepository.deleteById(id);
    }

    @Override
    public void deleteAll() {
        accountWorkoutRepository.deleteAll();
    }
}
