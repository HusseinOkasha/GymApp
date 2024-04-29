package GymApp.dto;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

import java.util.List;


public record WorkoutDto(long id, @NotBlank(message = "Name shouldn't be blank") String name,
                               @Valid List<ExerciseDto> exercises) {
}
