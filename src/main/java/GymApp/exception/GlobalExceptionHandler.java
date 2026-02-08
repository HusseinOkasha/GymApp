package GymApp.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AccountAlreadyExistsException.class)
    public ResponseEntity<ApiError> handleBadRequest(
            AccountAlreadyExistsException ex,
            HttpServletRequest request
    ) {
        ApiError error = new ApiError(
                ex.getMessage(),
                "ACCOUNT_ALREADY_EXISTS",
                HttpStatus.BAD_REQUEST.value(),
                request.getRequestURI(),
                Instant.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<ApiError> handleAccountNotFound(
            AccountNotFoundException ex,
            HttpServletRequest request
    ) {
        ApiError error = new ApiError(
                ex.getMessage(),
                "ACCOUNT_NOT_FOUND",
                HttpStatus.BAD_REQUEST.value(),
                request.getRequestURI(),
                Instant.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(BranchNotFoundException.class)
    public ResponseEntity<ApiError> handleAccountNotFound(
            BranchNotFoundException ex,
            HttpServletRequest request
    ) {
        ApiError error = new ApiError(
                ex.getMessage(),
                "BRANCH_NOT_FOUND",
                HttpStatus.BAD_REQUEST.value(),
                request.getRequestURI(),
                Instant.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
}
