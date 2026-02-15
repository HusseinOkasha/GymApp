package GymApp.dto;

import GymApp.enums.Roles;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RegisterDto(
        @NotBlank(message = "FirstName shouldn't be empty")
        String firstName,
        @NotBlank(message = "SecondName shouldn't be empty")
        String secondName,
        @NotBlank(message = "ThirdName shouldn't be empty")
        String thirdName,
        @NotBlank(message = "FirstName shouldn't be empty")
        @Email
        String email,
        @NotBlank(message = "Phone Number shouldn't be empty")
        String phoneNumber,
        @NotNull(message = "Role shouldn't be empty")
        Roles role,
        @NotNull(message= "Branch Id can't be null")
        Long branchId
) {
}
