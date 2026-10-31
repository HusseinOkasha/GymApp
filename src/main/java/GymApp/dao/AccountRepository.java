package GymApp.dao;

import GymApp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


public interface AccountRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User>findByPhoneNumber(String phoneNumber);
    Optional<User>findByEmailOrPhoneNumber(String email, String phoneNumber);
    @Transactional
    void deleteByEmail(String email);
    @Transactional
    void deleteByPhoneNumber(String phoneNumber);

}
