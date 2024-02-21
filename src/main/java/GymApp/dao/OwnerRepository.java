package GymApp.dao;



import GymApp.entity.Owner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface OwnerRepository extends JpaRepository<Owner, Long> {
    Optional<Owner> findByAccountId(long accountId);
    Optional<Owner> findByAccount_EmailOrAccount_PhoneNumber(String email, String phoneNumber);
    @Transactional
    void deleteByAccountId(long AccountId);
    @Transactional
    void deleteByAccount_EmailOrAccount_phoneNumber(String email, String phoneNumber);

}