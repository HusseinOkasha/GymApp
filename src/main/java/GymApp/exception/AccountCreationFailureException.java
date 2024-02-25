package GymApp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class AccountCreationFailureException  extends Exception{
    public AccountCreationFailureException(String message){
        super(message);
    }
}



