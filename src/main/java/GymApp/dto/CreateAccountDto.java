package GymApp.dto;

import GymApp.enums.AccountType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.time.LocalDateTime;

// this Dto represents the Account entity while receiving it from the outside world
// It includes the password.
public record CreateAccountDto(@NotBlank(message = "first name can't be blank") String firstName,
                               @NotBlank(message = "second name can't be blank")String SecondName,
                               @NotBlank(message = "third name can't be blank")String thirdName,
                               @Email(message = "invalid email format") String email,
                               @NotBlank(message = "phone number can't be blank") String phoneNumber,
                               @NotBlank(message = "password can't be blank") String password) {
}
