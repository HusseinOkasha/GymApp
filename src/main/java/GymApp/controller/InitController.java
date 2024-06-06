package GymApp.controller;


import GymApp.dto.AccountProfileDto;
import GymApp.entity.Account;
import GymApp.entity.Owner;
import GymApp.service.OwnerService;
import GymApp.util.entityAndDtoMappers.AccountMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class InitController {
    /*
    * It is intended to be used to initialize the DB, by adding an owner account.
    * I have added this controller to be easy for others to run and test the project.
    * Instead of writing SQL queries directly to the database, you just send a request to this controller,
      and it will handle the creation of owner account, so you can use the application.
    * */
    @Autowired
    private final OwnerService ownerService;


    public InitController(OwnerService ownerService) {
        this.ownerService = ownerService;
    }

    @PostMapping("/init")
    public AccountProfileDto init(){
        /*
        * It creates an owner account for demo purposes.
        * */

        Account.Builder accountBuilder = new Account.Builder();

        // BCrypt password for raw password "123"
        String bCryptPassword = "$2a$12$fdQCjXHktjZczz5hlHg77u8bIXUQdzGQf5k7ulN.cxzhW2vidHzSu";

        // save the account_id in the owner table
        Owner.Builder ownerBuilder = new Owner.Builder();
        Owner owner = ownerBuilder.account(
                accountBuilder
                        .firstName("f1")
                        .secondName("s1")
                        .thirdName("t1")
                        .email("e1@gmail.com")
                        .phoneNumber("1")
                        .password(bCryptPassword)
                        .build()
        ).build();

        owner = ownerService.save(owner);

        // return the account profile dto (without password).
        return AccountMapper.accountEntityToAccountProfileDto(owner.getAccount());
    }

}
