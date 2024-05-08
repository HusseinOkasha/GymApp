package GymApp.util.entityAndDtoMappers;

import GymApp.dto.WorkoutDto;
import GymApp.entity.Exercise;
import GymApp.entity.Workout;

import java.util.List;

public class WorkoutMapper {

    public static Workout workoutDtoToWorkoutEntity(WorkoutDto workoutDto){
        List<Exercise> exercises =
        workoutDto
                .exercises()
                .stream()
                .map(ExerciseMapper::exerciseDtoToExerciseEntity)
                .toList();

        Workout.Builder workoutBuilder = new Workout.Builder();
        Workout workout = workoutBuilder.name(workoutDto.name())
                        .exercises(exercises).build();
        workout.setExercises(exercises);
        return workout;
    }

    public static WorkoutDto workoutEntityToWorkoutDto(Workout workout){
        return new WorkoutDto(workout.getId(), workout.getName(),
                workout.getExercises().stream().map(ExerciseMapper::exerciseEntityToExerciseDto).toList());
    }

}
