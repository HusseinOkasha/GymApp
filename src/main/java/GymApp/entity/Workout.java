package GymApp.entity;


import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;

import java.util.List;
import java.util.Set;

@Entity
@Table(name="workout")
public class Workout {

    @Id
    @Column(name="id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "name")
    private String name;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "workout", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private Set<AccountWorkout> accountWorkouts = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "exercise", joinColumns = @JoinColumn(name = "workout_id"))
    private List<Exercise> exercises =  new ArrayList<>();

    private Workout(){}

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<Exercise> getExercises() {
        return exercises;
    }

    public void setExercises(List<Exercise> exercises) {
        this.exercises = exercises;
    }

    public Set<AccountWorkout> getAccountWorkouts() {
        return accountWorkouts;
    }

    public void setAccountWorkouts(Set<AccountWorkout> accountWorkouts) {
        this.accountWorkouts = accountWorkouts;
    }

    public static class Builder{
        private long id;
        private String name;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private Set<AccountWorkout>accountWorkouts;
        private List<Exercise>exercises;

        public Builder(){}

        public Builder id(long id){
            this.id = id;
            return this;
        }
        public Builder name(String name){
            this.name = name;
            return this;
        }
        public Builder createdAt(LocalDateTime createdAt){
            this.createdAt = createdAt;
            return this;
        }
        public Builder updatedAt(LocalDateTime updatedAt){
            this.updatedAt = updatedAt;
            return this;
        }
        public Builder accountWorkouts(Set<AccountWorkout> accountWorkouts){
            this.accountWorkouts = accountWorkouts;
            return this;
        }
        public Builder exercises(List<Exercise> exercises){
            this.exercises = exercises;
            return this;
        }
        public Workout build(){
            Workout workout = new Workout();
            workout.id = this.id;
            workout.name = this.name;
            workout.createdAt = this.createdAt;
            workout.updatedAt = this.updatedAt;
            workout.accountWorkouts =this.accountWorkouts;
            workout.exercises = this.exercises;

            return workout;
        }


    }

    @PrePersist
    public void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "Workout{" +
                ", name='" + name + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
