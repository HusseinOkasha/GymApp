package GymApp.util;

import GymApp.dto.AccountProfileDto;
import GymApp.dto.ExerciseDto;
import GymApp.dto.WorkoutDto;
import GymApp.entity.Workout;
import GymApp.util.entityAndDtoMappers.ExerciseMapper;
import GymApp.util.entityAndDtoMappers.WorkoutMapper;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class WorkoutControllerTestUtil {
    static public void shouldCreateWorkout(Workout workout, String token, int port, RestTemplate restTemplate){
        // create workoutDto
        WorkoutDto workoutDto = WorkoutMapper.workoutEntityToWorkoutDto(workout);

        // send the request
        ResponseEntity<WorkoutDto> response = attemptWorkoutCreation(workoutDto, token, port, restTemplate);

        WorkoutDto createdWorkout = response.getBody();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(createdWorkout).isNotNull();
        assertThat(createdWorkout.name()).isEqualTo(workoutDto.name());
        assertThat(createdWorkout.exercises()).isEqualTo(workoutDto.exercises());
    }
    static public void shouldNotCreateWorkout(Workout workout, int port, RestTemplate restTemplate){
        // create workoutDto
        WorkoutDto workoutDto = WorkoutMapper.workoutEntityToWorkoutDto(workout);

        // send the request
        ResponseEntity<WorkoutDto> response = attemptWorkoutCreation(workoutDto, null ,port, restTemplate);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }
    static public ResponseEntity<WorkoutDto> attemptWorkoutCreation(WorkoutDto workoutDto, String token, int port,
                                                                    RestTemplate restTemplate){

        // set up the request
        // As port number as it's generated randomly.
        String baseUrl = AuthUtil.getBaseUrl(port);

        // set up the authentication header
        HttpHeaders headers = new HttpHeaders();

        // token value is assigned during setUp method
        headers.add("Authorization", "Bearer " + token);
        HttpEntity<WorkoutDto> request = new HttpEntity<>(workoutDto, headers);

        // send the request
        try{
            return restTemplate.exchange(baseUrl + "/workouts",
                    HttpMethod.POST, request, WorkoutDto.class);
        }
        catch (HttpClientErrorException e){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

    }

    static public ResponseEntity<WorkoutDto> attemptGetWorkoutById(long id, String token, int port,
                                                                    RestTemplate restTemplate){

        // set up the request
        // As port number as it's generated randomly.
        String baseUrl = AuthUtil.getBaseUrl(port);

        // set up the authentication header
        HttpHeaders headers = new HttpHeaders();

        // token value is assigned during setUp method
        headers.add("Authorization", "Bearer " + token);
        HttpEntity<WorkoutDto> request = new HttpEntity<>(headers);

        // send the request
        try{
            return restTemplate.exchange(baseUrl + "/workouts/"+id,
                    HttpMethod.GET, request, WorkoutDto.class);
        }
        catch (HttpClientErrorException e){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }
    static public void shouldGetWorkoutById(Workout workout, String token, int port, RestTemplate restTemplate){

        // convert workout entity to workoutDto.
        // why ? as we will compare it against workoutDto returned as response body.
        WorkoutDto expectedWorkoutDto = WorkoutMapper.workoutEntityToWorkoutDto(workout);

        // send the request
        ResponseEntity<WorkoutDto> response = attemptGetWorkoutById(workout.getId(), token, port, restTemplate);

        // extract the response body.
        WorkoutDto underTestWorkoutDto = response.getBody();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(underTestWorkoutDto).isNotNull();
        assertThat(underTestWorkoutDto.name()).isEqualTo(expectedWorkoutDto.name());
        assertThat(underTestWorkoutDto.exercises()).isEqualTo(expectedWorkoutDto.exercises());
    }

    static public void shouldNotGetWorkoutById(Workout workout, String token,int port, RestTemplate restTemplate){
        // send the request
        ResponseEntity<WorkoutDto> response = attemptGetWorkoutById(workout.getId(),null, port, restTemplate);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }
    static public void shouldGetAllWorkouts(List<Workout> expectedWorkouts, String token, int port, RestTemplate restTemplate){

        // convert workout entity to workoutDto.
        // why ? as we will compare it against workoutDto returned as response body.
        List<WorkoutDto> expectedWorkoutDto = expectedWorkouts
                .stream()
                .map(WorkoutMapper::workoutEntityToWorkoutDto)
                .toList();

        // send the request
        ResponseEntity<List<WorkoutDto>> response = attemptGetAllWorkouts(token, port, restTemplate);

        // extract the response body.
        List<WorkoutDto> underTestWorkoutDtos = response.getBody();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(underTestWorkoutDtos).isNotNull();
        assertThat(underTestWorkoutDtos.size()).isEqualTo(expectedWorkoutDto.size());

        assertThat(underTestWorkoutDtos).isIn(expectedWorkoutDto);

    }
    static public ResponseEntity<List<WorkoutDto>> attemptGetAllWorkouts(String token, int port, RestTemplate restTemplate){

        // set up the request
        // As port number as it's generated randomly.
        String baseUrl = AuthUtil.getBaseUrl(port);

        // set up the authentication header
        HttpHeaders headers = new HttpHeaders();

        // token value is assigned during setUp method
        headers.add("Authorization", "Bearer " + token);
        HttpEntity request = new HttpEntity<>(headers);

        ParameterizedTypeReference<List<WorkoutDto>>
                responseType = new ParameterizedTypeReference<List<WorkoutDto>>() {};
        // send the request
        try{
            return restTemplate.exchange(baseUrl + "/workouts",
                    HttpMethod.GET, request, responseType);
        }
        catch (HttpClientErrorException e){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }
    static public void shouldNotGetAllWorkouts(String token,int port, RestTemplate restTemplate){
        // send the request
        ResponseEntity<List<WorkoutDto>> response = attemptGetAllWorkouts(null,port, restTemplate);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }
    static public ResponseEntity<WorkoutDto> attemptUpdateWorkoutById(String token, int port, WorkoutDto workoutDto, RestTemplate restTemplate){
        // set up the request
        // As port number as it's generated randomly.
        String baseUrl = AuthUtil.getBaseUrl(port);

        // set up the authentication header
        HttpHeaders headers = new HttpHeaders();

        // token value is assigned during setUp method
        headers.add("Authorization", "Bearer " + token);
        HttpEntity<WorkoutDto> request = new HttpEntity<>(workoutDto, headers);

        // send the request
        try{
            return restTemplate.exchange(baseUrl + "/workouts/" + workoutDto.id(),
                    HttpMethod.PUT, request, WorkoutDto.class);
        }
        catch (HttpClientErrorException e){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

    }

    public static void shouldUpdateWorkoutById(String token, Workout workout, int port,RestTemplate restTemplate ){

        WorkoutDto expectedWorkoutDto = WorkoutMapper.workoutEntityToWorkoutDto(workout);

        attemptUpdateWorkoutById(token, port, expectedWorkoutDto, restTemplate);
        ResponseEntity<WorkoutDto>  response = WorkoutControllerTestUtil.attemptUpdateWorkoutById(token, port,
                expectedWorkoutDto, restTemplate);

        WorkoutDto underTestWorkoutDto = response.getBody();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(underTestWorkoutDto).isEqualTo(expectedWorkoutDto);

    }
    public static void shouldNotUpdateWorkoutById(String token, Workout workout, int port,RestTemplate restTemplate ){

        WorkoutDto expectedWorkoutDto = WorkoutMapper.workoutEntityToWorkoutDto(workout);

        attemptUpdateWorkoutById(token, port, expectedWorkoutDto, restTemplate);
        ResponseEntity<WorkoutDto>  response = WorkoutControllerTestUtil.attemptUpdateWorkoutById(token, port,
                expectedWorkoutDto, restTemplate);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);


    }

}
