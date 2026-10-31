package GymApp.service.Intrface;


import GymApp.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    List<User> findAll();

    User findById(long id);

    User findByEmail(String email);

    Optional<User> findByPhoneNumber(String phoneNumber);

    Optional<User> findByEmailOrPhoneNumber(String email, String phoneNumber);

    User save(User user);

    void deleteById(long Id);

    void deleteAll();

    void deleteByEmail(String email);

    void deleteByPhoneNumber(String phoneNumber);

    boolean hasAccessOnBranch(Long accountId, Long branchId);

}
