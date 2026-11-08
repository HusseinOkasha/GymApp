package GymApp.entity;


import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = "email"), @UniqueConstraint(columnNames = "phone_number")
})
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq_generator")
    @SequenceGenerator(
            name = "user_seq_generator",       // The logical name used inside Hibernate
            sequenceName = "users_seq",        // The physical sequence name in your database
            allocationSize = 1                 // Adjust based on your DB sequence increment
            )
    @Column(name = "id")
    private long id;
    @Column(name = "first_name", nullable = false)
    private String firstName;
    @Column(name = "second_name" , nullable = false)
    private String secondName;
    @Column(name = "third_name" , nullable = false)
    private String thirdName;
    @Column(name = "email", nullable = false)
    private String email;
    @Column(name="phone_number", nullable = false)
    String phoneNumber;
    @Column(name = "password", nullable = false)
    private String password;
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private Set<UserWorkout> userWorkouts = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserRole> roles = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserBranch> branches = new HashSet<>();

    public User() {
    }

    public Set<UserRole> getRoles() {
        return roles;
    }
    public void setRoles(Set<UserRole> roles){
        this.roles = roles;
    }
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public String getThirdName() {
        return thirdName;
    }

    public void setThirdName(String thirdName) {
        this.thirdName = thirdName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public Set<UserWorkout> getUserWorkouts() {
        return userWorkouts;
    }

    public void setUserWorkouts(Set<UserWorkout> userWorkouts) {
        this.userWorkouts = userWorkouts;
    }

    public Set<UserBranch> getBranches() {
        return this.branches;
    }

    public void setBranch(Set<UserBranch> branches) {
        this.branches = branches;
    }

    public static class Builder{

        private long id;
        private String firstName;
        private String secondName;
        private String thirdName;
        private String email;
        private String phoneNumber;
        private String password;
        private Set<UserRole> roles;
        private Set<UserWorkout> userWorkouts;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public Builder(){}

        public Builder id(long id ){
            this.id = id;
            return this;
        }
        public Builder firstName(String firstName ){
            this.firstName = firstName;
            return this;
        }
        public Builder secondName(String secondName ){
            this.secondName = secondName;
            return this;
        }

        public Builder thirdName(String  thirdName ){
            this.thirdName = thirdName;
            return this;
        }
        public Builder email(String email){
            this.email = email;
            return this;
        }
        public Builder phoneNumber(String phoneNumber){
            this.phoneNumber = phoneNumber;
            return this;
        }
        public Builder password(String  password ){
            this.password = password;
            return this;
        }
        public Builder role(Set<UserRole> roles){
            this.roles = roles;
            return this;
        }
        public Builder userWorkouts(Set<UserWorkout> userWorkouts){
            this.userWorkouts = new HashSet<>(userWorkouts);
            return this;
        }
        public Builder createdAt(LocalDateTime  createdAt ){
            this.createdAt = createdAt;
            return this;
        }
        public Builder updateAt(LocalDateTime  updatedAt ){
            this.updatedAt = updatedAt;
            return this;
        }
        public Builder copyFrom(User user){
            this.id = user.id;
            this.firstName = user.firstName;
            this.secondName = user.secondName;
            this.thirdName = user.thirdName;
            this.email = user.email;
            this.phoneNumber = user.phoneNumber;
            this.password = user.password;
            this.updatedAt = user.updatedAt;
            this.createdAt = user.createdAt;
            return this;
        }
        public User build(){
            User user = new User();
            user.id = this.id;
            user.firstName = this.firstName;
            user.secondName = this.secondName;
            user.thirdName = this.thirdName;
            user.email = this.email;
            user.phoneNumber = this.phoneNumber;
            user.password = this.password;
            user.roles = this.roles;
            user.userWorkouts = this.userWorkouts;
            user.createdAt = this.createdAt;
            user.updatedAt = this.updatedAt;
            return user;
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
        return "User{" +
                "firstName='" + firstName + '\'' +
                ", secondName='" + secondName + '\'' +
                ", thirdName='" + thirdName + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
