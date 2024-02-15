package GymApp.service;

import GymApp.dao.CoachRepository;
import GymApp.entity.Coach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CoachServiceImpl  implements  CoachService{
    @Autowired
    private final CoachRepository coachRepository;


    public CoachServiceImpl(CoachRepository coachRepository){
        this.coachRepository = coachRepository;
    }
    @Override
    public List<Coach> findAll() {
        return coachRepository.findAll();
    }

    @Override
    public Optional<Coach> findById(long id) {
        return coachRepository.findById(id);
    }

    @Override
    public Optional<Coach> findByAccountId(long accountId) {
        return coachRepository.findByAccountId(accountId);
    }

    @Override
    public Optional<Coach> save(Coach coach) {
        return Optional.of(coachRepository.save(coach));
    }

    @Override
    public void deleteById(long id) {
        coachRepository.deleteById(id);
    }

    @Override
    public void deleteByAccountId(long accountId) {
        coachRepository.deleteByAccountId(accountId);
    }
}
