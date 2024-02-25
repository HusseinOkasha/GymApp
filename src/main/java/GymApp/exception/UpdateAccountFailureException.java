package GymApp.exception;


import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class UpdateAccountFailureException  extends Exception{
    public UpdateAccountFailureException(String message){
        super(message);
    }
}
