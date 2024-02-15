package GymApp.dao;


import GymApp.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client, Long> {
    Optional<Client> findByAccountId(long accountId);
    @Transactional
    void deleteByAccountId(long AccountId);

}
