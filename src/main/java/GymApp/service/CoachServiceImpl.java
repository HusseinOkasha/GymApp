package GymApp.service;

import GymApp.dao.CoachRepository;
import GymApp.entity.Coach;
import GymApp.exception.AccountNotFoundException;
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
    public Coach save(Coach coach) {
        return coachRepository.save(coach);
    }

    @Override
    public void deleteById(long id) {
        coachRepository.deleteById(id);
    }

    @Override
    public void deleteByAccountId(long accountId) {
        coachRepository.deleteByAccountId(accountId);
    }

    @Override
    public void deleteByAccount_Email(String email){
        Optional<Coach> coach = coachRepository.findByAccount_Email(email);
        coach.ifPresent(value -> coachRepository.deleteById(value.getId()));
    }

    @Override
    public void deleteByAccount_PhoneNumber(String phoneNumber){
        Optional<Coach> coach = coachRepository.findByAccount_PhoneNumber(phoneNumber);
        coach.ifPresent(value -> coachRepository.deleteById(value.getId()));
    }
    @Override
    public void deleteAll(){
        coachRepository.deleteAll();
    }
}
