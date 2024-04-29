package GymApp.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;


import java.time.LocalDateTime;

public record ExerciseDto(@NotBlank(message = "name shouldn't be blank") String name,
                          @NotNull(message = "number of sets can't be blank")
                          @Positive(message = "number of sets should be positive") int sets,
                          @NotNull(message = "number of reps can't be blank")
                          @Positive(message = "number of reps should be positive")int reps,
                          String notes) {
}
