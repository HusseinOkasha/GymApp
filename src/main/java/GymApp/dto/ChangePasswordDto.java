package GymApp.dto;

import jakarta.validation.constraints.NotBlank;

public record ChangePasswordDto(@NotBlank(message = "password can't be blank") String password) {
}
