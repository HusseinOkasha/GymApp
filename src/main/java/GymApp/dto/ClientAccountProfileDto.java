package GymApp.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ClientAccountProfileDto(@Valid AccountProfileDto accountProfileDto,
                                @NotNull LocalDate birthDate) {
}
