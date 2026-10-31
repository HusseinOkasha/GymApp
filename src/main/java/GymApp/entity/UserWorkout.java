package GymApp.entity;

import GymApp.enums.WorkoutAccessType;
import jakarta.persistence.*;


import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "user_workout")
public class UserWorkout {

    @Embeddable
    public static class Id implements Serializable {

        @Column(name = "workout_id")
        private Long workoutId;

        @Column(name = "user_id")
        private Long  userId;

        public Id(){}

        public Id(Long userId, Long workoutId) {
            this.workoutId = workoutId;
            this.userId = userId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Id id)) return false;
            return Objects.equals(workoutId, id.workoutId) && Objects.equals(userId, id.userId);
        }

        @Override
        public int hashCode() {
            return workoutId.hashCode() + userId.hashCode();
        }

    }
    @EmbeddedId
    private Id id = new Id();

    @ManyToOne(fetch = FetchType.LAZY )
    @JoinColumn(name = "workout_id", insertable = false, updatable = false)
    private Workout workout;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @Column(name="access_type", nullable = false)
    private WorkoutAccessType accessType;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public UserWorkout() {}

    public Id getId() {
        return id;
    }

    public void setId(Id id) {
        this.id = id;
    }

    public Workout getWorkout() {
        return workout;
    }

    public void setWorkout(Workout workout) {
        this.workout = workout;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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

    public WorkoutAccessType getAccessType() {
        return accessType;
    }

    public void setAccessType(WorkoutAccessType accessType) {
        this.accessType = accessType;
    }

    public static class Builder {
        private Id id;
        private Workout workout;
        private User user;
        private WorkoutAccessType accessType;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public Builder() {
        }
        public Builder id(Id id){
            this.id = id;
            return this;
        }
        public Builder workout(Workout workout) {
            this.workout = workout;
            return this;
        }

        public Builder user(User user) {
            this.user = user;
            return this;
        }

        public Builder accessType(WorkoutAccessType accessType) {
            this.accessType = accessType;
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public UserWorkout build() {
            UserWorkout userWorkout = new UserWorkout();
            userWorkout.id = this.id;
            userWorkout.workout = this.workout;
            userWorkout.user = this.user;
            userWorkout.accessType = this.accessType;
            userWorkout.createdAt = this.createdAt;
            userWorkout.updatedAt = this.updatedAt;
            return userWorkout;
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


}
