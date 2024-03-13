package GymApp.service;

import GymApp.entity.Coach;

import java.util.List;
import java.util.Optional;

public interface CoachService {
    List<Coach> findAll();
    Optional<Coach> findById(long id);
    Optional<Coach> findByAccountId(long accountId);
    Optional<Coach> save(Coach coach);
    void deleteById(long Id);
    void deleteByAccountId(long accountId);
    void deleteByAccount_Email(String email);
    void deleteByAccount_PhoneNumber(String phoneNumber);
    void deleteAll();

}
