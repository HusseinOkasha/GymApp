package GymApp.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

// this Dto represents the Account entity while receiving it from the outside world
// It includes the password.
public record CreateAccountDto(long id, @NotBlank(message = "first name can't be blank") String firstName,
                               @NotBlank(message = "second name can't be blank")String SecondName,
                               @NotBlank(message = "third name can't be blank")String thirdName,
                               @Email(message = "invalid email format") String email,
                               @NotBlank(message = "phone number can't be blank") String phoneNumber,
                               @NotBlank(message = "password can't be blank") String password,
                               LocalDateTime createdAt,
                               LocalDateTime updatedAt) {
}
