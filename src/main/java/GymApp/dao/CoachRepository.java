package GymApp.dao;


import GymApp.entity.Coach;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface CoachRepository extends JpaRepository<Coach, Long> {
    Optional<Coach> findByAccountId(long accountId);
    Optional<Coach> findByAccount_Email(String email);
    Optional<Coach> findByAccount_PhoneNumber(String phoneNumber);
    @Transactional
    void deleteByAccountId(long AccountId);

}