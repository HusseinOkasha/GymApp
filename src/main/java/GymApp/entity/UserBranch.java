package GymApp.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name="user_branch")
public class UserBranch {
    @EmbeddedId
    private Id id = new Id();

    @ManyToOne(fetch = FetchType.LAZY )
    @JoinColumn(name = "branch_id", insertable = false, updatable = false)
    @MapsId("branchId")
    private Branch branch;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", insertable = false, updatable = false)
    @MapsId("accountId")
    private Account account;


    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public UserBranch(){

    }

    public Id getId() {
        return id;
    }

    public void setId(Id id) {
        this.id = id;
    }

    public Branch getBranch() {
        return branch;
    }

    public void setBranch(Branch branch) {
        this.branch = branch;
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

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
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

    @Embeddable
    public static class Id implements Serializable {

        @Column(name = "account_id")
        private Long accountId;

        @Column(name = "branch_id")
        private Long  branchId;

        public Id(){}

        public Id(Long accountId, Long branchId) {
            this.branchId = branchId;
            this.accountId = accountId;
        }

        public void setAccountId(Long accountId){
            this.accountId = accountId;
        }

        public void setBranchId(Long branchId){
            this.branchId = branchId;
        }
        public Long getAccountId(){
            return this.accountId;
        }
        public Long getBranchId(){
            return this.branchId;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Id id = (Id) o;
            return Objects.equals(getAccountId(), id.getAccountId()) &&
                   Objects.equals(getBranchId(), id.getBranchId());
        }

        @Override
        public int hashCode() {
            return Objects.hash(getAccountId(), getBranchId());
        }
    }

    public static class Builder {
        private Id id;
        private Branch branch;
        private Account account;

        public Builder() {
        }
        public Builder id(Id id){
            this.id = id;
            return this;
        }
        public Builder branch(Branch branch) {
            this.branch = branch;
            return this;
        }

        public Builder account(Account account) {
            this.account = account;
            return this;
        }

        public UserBranch build() {
            UserBranch userBranch = new UserBranch();
            userBranch.id = this.id;
            userBranch.branch = this.branch;
            userBranch.account = this.account;
            return userBranch;
        }
    }

}
