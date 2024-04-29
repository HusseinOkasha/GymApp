package GymApp.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.SecondaryRow;

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

        public Id(Long workoutId, Long accountId) {
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

    @ManyToOne()
    @JoinColumn(name = "workout_id", insertable = false, updatable = false)
    private Workout workout;

    @ManyToOne()
    @JoinColumn(name = "account_id", insertable = false, updatable = false)
    private Account account;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public AccountWorkout() {
    }

    public AccountWorkout(Id id, Account account, Workout workout){
        this.id = id;
        this.account = account;
        this.workout = workout;
        this.id.accountId = account.getId();
        this.id.workoutId = workout.getId();
        account.getAccountWorkouts().add(this);
        workout.getAccountWorkouts().add(this);
    }

    public Id getId() {
        return id;
    }

    public void setId(Id id) {
        this.id = id;
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
