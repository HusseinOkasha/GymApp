package GymApp.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;

@Embeddable
public class Exercise {

    @Column(name="name", nullable = false)
    private String name;

    @Column(name="sets", nullable = false)
    private int sets;

    @Column(name = "reps", nullable = false)
    private int reps;

    @Column(name = "notes")
    private String notes;

    @ManyToOne()
    @JoinColumn(name="workout_id", updatable = false, insertable = false, nullable = false)
    private Workout workout;

    public Exercise() {
    }

    public Exercise(String name, int sets, int reps, String notes) {
        this.name = name;
        this.sets = sets;
        this.reps = reps;
        this.notes = notes;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSets() {
        return sets;
    }

    public void setSets(int sets) {
        this.sets = sets;
    }

    public int getReps() {
        return reps;
    }

    public void setReps(int reps) {
        this.reps = reps;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Workout getWorkout() {
        return workout;
    }

    public void setWorkout(Workout workout) {
        this.workout = workout;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Exercise exercise)) return false;
        return workout.getId() == exercise.getWorkout().getId() && Objects.equals(name, exercise.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(workout.getId(), name);
    }

    @Override
    public String toString() {
        return "Exercise{" +
                ", name='" + name + '\'' +
                ", sets=" + sets +
                ", reps=" + reps +
                '}';
    }
}
