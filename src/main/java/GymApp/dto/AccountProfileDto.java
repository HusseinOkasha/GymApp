package GymApp.dto;




import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;
import java.time.LocalDateTime;

// This Dto is used to represent the Account entity while retrieving it (return it to the outside world).
// It doesn't include the password.
public record AccountProfileDto(long id, @NotBlank(message = "first name can't be blank") String firstName,
                                @NotBlank(message = "second name can't be blank")String SecondName,
                                @NotBlank(message = "third name can't be blank")String thirdName,
                                @NotBlank @Email(message = "invalid email format") String email,
                                @NotBlank(message = "phone number can't be blank") String phoneNumber,
                                LocalDateTime createdAt,
                                LocalDateTime updatedAt) {
}
