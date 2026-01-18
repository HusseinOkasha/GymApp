package GymApp.dto;

import jakarta.validation.constraints.NotBlank;

public record SetPasswordDto (@NotBlank String password, @NotBlank String confirmPassword){
}
