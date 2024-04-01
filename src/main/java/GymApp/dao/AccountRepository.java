package GymApp.dao;

import GymApp.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByEmail(String email);
    Optional<Account>findByPhoneNumber(String phoneNumber);
    Optional<Account>findByEmailOrPhoneNumber(String email, String phoneNumber);
    @Transactional
    void deleteByEmail(String email);
    @Transactional
    void deleteByPhoneNumber(String phoneNumber);

}
