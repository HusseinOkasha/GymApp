package GymApp.util.entityAndDtoMappers;

import GymApp.dto.ExerciseDto;
import GymApp.entity.Exercise;

public class ExerciseMapper {

    public static Exercise exerciseDtoToExerciseEntity(ExerciseDto exerciseDto){
        return new Exercise(exerciseDto.name(), exerciseDto.sets(), exerciseDto.reps(), exerciseDto.notes());
    }

    public static ExerciseDto exerciseEntityToExerciseDto(Exercise exercise){
        return new ExerciseDto(exercise.getName(),exercise.getSets(), exercise.getReps(), exercise.getNotes());
    }
}
