package GymApp.entity;


import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name="user_role")
public class UserRole {

    @Embeddable
    public static class Id implements Serializable {

        @Column(name = "account_id")
        private Long accountId;

        @Column(name = "role_id")
        private Long  roleId;

        public Id(){}

        public Id(Long accountId, Long roleId) {
            this.roleId = roleId;
            this.accountId = accountId;
        }

        public void setAccountId(Long accountId){
            this.accountId = accountId;
        }

        public void setRoleId(Long roleId){
            this.roleId = roleId;
        }
        public Long getAccountId(){
            return this.accountId;
        }
        public Long getRoleId(){
            return this.roleId;
        }
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof UserRole.Id id)) return false;
            return Objects.equals(roleId, id.roleId) && Objects.equals(accountId, id.accountId);
        }

        @Override
        public int hashCode() {
            return roleId.hashCode() + accountId.hashCode();
        }

    }
    @EmbeddedId
    private UserRole.Id id = new UserRole.Id();

    @ManyToOne(fetch = FetchType.LAZY )
    @JoinColumn(name = "role_id", insertable = false, updatable = false)
    @MapsId("roleId")
    private Role role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", insertable = false, updatable = false)
    @MapsId("accountId")
    private Account account;


    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public UserRole() {}
    public UserRole(Account account, Role role) {
        this.account = account;
        this.role = role;
        this.id = new UserRole.Id(account.getId(), role.getId());
    }

    public UserRole.Id getId() {
        return id;
    }

    public void setId(UserRole.Id id) {
        this.id = id;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
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
        private UserRole.Id id;
        private Role role;
        private Account account;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public Builder() {
        }
        public UserRole.Builder id(UserRole.Id id){
            this.id = id;
            return this;
        }
        public UserRole.Builder role(Role role) {
            this.role = role;
            return this;
        }

        public UserRole.Builder account(Account account) {
            this.account = account;
            return this;
        }

        public UserRole.Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public UserRole.Builder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public UserRole build() {
            UserRole userRole = new UserRole();
            userRole.id = this.id;
            userRole.role = this.role;
            userRole.account = this.account;
            userRole.createdAt = this.createdAt;
            userRole.updatedAt = this.updatedAt;
            return userRole;
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
