package GymApp.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record CreateClientAccountDto (@Valid  CreateAccountDto createAccountDto,
                                      @NotNull(message = "birth_date can't be empty") LocalDate birthDate
                                      ){
}
