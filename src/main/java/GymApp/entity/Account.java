package GymApp.entity;


import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "account", uniqueConstraints = {
        @UniqueConstraint(columnNames = "email"), @UniqueConstraint(columnNames = "phone_number")
})
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @OneToMany(mappedBy = "account", cascade = CascadeType.REMOVE)
    private Set<AccountWorkout> accountWorkouts = new HashSet<>();

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserRole> roles = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "branch_Id", nullable = false)
    private Branch branch;

    public Account() {
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

    public Set<AccountWorkout> getAccountWorkouts() {
        return accountWorkouts;
    }

    public void setAccountWorkouts(Set<AccountWorkout> accountWorkouts) {
        this.accountWorkouts = accountWorkouts;
    }

    public Branch getBranch() {
        return branch;
    }

    public void setBranch(Branch branch) {
        this.branch = branch;
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
        private Set<AccountWorkout> accountWorkouts;
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
        public Builder accountWorkouts(Set<AccountWorkout> accountWorkouts ){
            this.accountWorkouts = new HashSet<>(accountWorkouts);
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
        public Builder copyFrom(Account account){
            this.id = account.id;
            this.firstName = account.firstName;
            this.secondName = account.secondName;
            this.thirdName = account.thirdName;
            this.email = account.email;
            this.phoneNumber = account.phoneNumber;
            this.password = account.password;
            this.updatedAt = account.updatedAt;
            this.createdAt = account.createdAt;
            return this;
        }
        public Account build(){
            Account account = new Account();
            account.id = this.id;
            account.firstName = this.firstName;
            account.secondName = this.secondName;
            account.thirdName = this.thirdName;
            account.email = this.email;
            account.phoneNumber = this.phoneNumber;
            account.password = this.password;
            account.roles = this.roles;
            account.accountWorkouts = this.accountWorkouts;
            account.createdAt = this.createdAt;
            account.updatedAt = this.updatedAt;
            return account;
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
        return "Account{" +
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
