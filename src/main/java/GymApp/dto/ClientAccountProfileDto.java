package GymApp.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ClientAccountProfileDto(long id, @NotBlank(message = "first name can't be blank") String firstName,
                                @NotBlank(message = "second name can't be blank")String SecondName,
                                @NotBlank(message = "third name can't be blank")String thirdName,
                                @Email(message = "invalid email format") String email,
                                @NotBlank(message = "phone number can't be blank") String phoneNumber,
                                @NotNull LocalDate birthDate,
                                LocalDateTime createdAt,
                                LocalDateTime updatedAt) {
}
