package GymApp.entity;

import GymApp.enums.WorkoutAccessType;
import jakarta.persistence.*;


import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "account_workout")
public class AccountWorkout {

    @Embeddable
    public static class Id implements Serializable {

        @Column(name = "workout_id")
        private Long workoutId;

        @Column(name = "account_id")
        private Long  accountId;

        public Id(){}

        public Id(Long accountId, Long workoutId) {
            this.workoutId = workoutId;
            this.accountId = accountId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Id id)) return false;
            return Objects.equals(workoutId, id.workoutId) && Objects.equals(accountId, id.accountId);
        }

        @Override
        public int hashCode() {
            return workoutId.hashCode() + accountId.hashCode();
        }

    }
    @EmbeddedId
    private Id id = new Id();

    @ManyToOne(fetch = FetchType.LAZY )
    @JoinColumn(name = "workout_id", insertable = false, updatable = false)
    private Workout workout;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", insertable = false, updatable = false)
    private Account account;

    @Column(name="access_type", nullable = false)
    private WorkoutAccessType accessType;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    private AccountWorkout() {}

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

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
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

    public static class Builder {
        private Id id;
        private Workout workout;
        private Account account;
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

        public Builder account(Account account) {
            this.account = account;
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

        public AccountWorkout build() {
            AccountWorkout accountWorkout = new AccountWorkout();
            accountWorkout.id = this.id;
            accountWorkout.workout = this.workout;
            accountWorkout.account = this.account;
            accountWorkout.accessType = this.accessType;
            accountWorkout.createdAt = this.createdAt;
            accountWorkout.updatedAt = this.updatedAt;
            return accountWorkout;
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
