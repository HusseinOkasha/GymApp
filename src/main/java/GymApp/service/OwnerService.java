package GymApp.service;

import GymApp.entity.Owner;

import java.util.List;
import java.util.Optional;

public interface OwnerService {
    List<Owner> findAll();
    Optional<Owner> findById(long id);
    Optional<Owner> findByAccountId(long accountId);
    Owner save(Owner owner);
    void deleteById(long Id);
    void deleteByAccountId(long accountId);
    void deleteAll();

}
