package GymApp.controller;


import GymApp.dao.AccountRepository;
import GymApp.dto.AccountDto;
import GymApp.entity.Account;
import GymApp.security.EncryptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class AccountController {

    private AccountRepository accountRepository;
    private EncryptionService encryptionService;
    @Autowired
    AccountController(AccountRepository accountRepository ,EncryptionService encryptionService){
        this.accountRepository = accountRepository;
        this.encryptionService = encryptionService;
    }

    @PostMapping("/accounts")
    @Validated
    public Account createAccount(@RequestBody Account account){
        System.out.println("!!!!! Request Arrived !!!!!");

        String password = account.getPassword();
        String encryptedPassword = encryptionService.encryptString(password);
        account.setPassword(encryptedPassword);
        return accountRepository.save(account);
    }


}
