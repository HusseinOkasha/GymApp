package GymApp.service;

import GymApp.dao.AccountRepository;
import GymApp.entity.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AccountServiceImpl implements AccountService {
    @Autowired
    private final AccountRepository accountRepository;

    public AccountServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public List<Account> findAll() {
        return accountRepository.findAll();
    }

    @Override
    public Optional<Account> findById(long id) {
        return accountRepository.findById(id);
    }

    @Override
    public Optional<Account> findByEmail(String email) {
        return accountRepository.findByEmail(email);
    }

    @Override
    public Optional<Account> findByPhoneNumber(String phoneNumber) {
        return accountRepository.findByPhoneNumber(phoneNumber);
    }

    @Override
    public Optional<Account> findByEmailOrPhoneNumber(String email, String phoneNumber) {
        return accountRepository.findByEmailOrPhoneNumber(email, phoneNumber);
    }

    @Override
    public Account save(Account account) {
        return accountRepository.save(account);
    }

    @Override
    public void deleteById(long id) {
        accountRepository.deleteById(id);
    }

    @Override
    public void deleteAll() {
        accountRepository.deleteAll();
    }


    @Override
    public void deleteByEmail(String email) {
        accountRepository.deleteByEmail(email);
    }

    @Override
    public void deleteByPhoneNumber(String phoneNumber) {
        accountRepository.deleteByPhoneNumber(phoneNumber);
    }
}
