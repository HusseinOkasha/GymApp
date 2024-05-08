package GymApp.service;


import GymApp.entity.Account;

import java.util.List;
import java.util.Optional;

public interface AccountService {
      List<Account> findAll();
      Optional<Account>findById(long id);
      Optional<Account>findByEmail(String email);
      Optional<Account>findByPhoneNumber(String phoneNumber);
      Optional<Account>findByEmailOrPhoneNumber(String email, String phoneNumber);
      Account save(Account account);
      void deleteById(long Id);
      void deleteAll();
      void deleteByEmail(String email);
      void deleteByPhoneNumber(String phoneNumber);

}
