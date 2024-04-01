package GymApp.dao;


import GymApp.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client, Long> {
    Optional<Client> findByAccountId(long accountId);
    Optional<Client> findByAccount_Email(String Email);
    Optional<Client> findByAccount_PhoneNumber(String phoneNumber);
    @Transactional
    void deleteByAccountId(long AccountId);


}
