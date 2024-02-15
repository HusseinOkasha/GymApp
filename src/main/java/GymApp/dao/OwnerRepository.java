package GymApp.dao;



import GymApp.entity.Owner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface OwnerRepository extends JpaRepository<Owner, Long> {
    Optional<Owner> findByAccountId(long accountId);
    @Transactional
    void deleteByAccountId(long AccountId);

}