package GymApp.entity;

import GymApp.enums.MembershipType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "memberships")
public class Membership {
    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "is_active", nullable = false)
    private boolean isActive;

    @Enumerated(EnumType.STRING)
    @Column(name = "membership_type", nullable = false)
    private MembershipType type;

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private Account client;

    @ManyToOne
    @JoinColumn(name="branch_id", nullable = false)
    private Branch branch;

    @JoinColumn(name="created_by", nullable = false)
    @ManyToOne
    private Account createdBy;

    @CreationTimestamp
    @Column(name="created_at", nullable = false)
    private LocalDateTime createdAt;

    public Membership() {
    }

    public Membership(
            Long id,
            LocalDate startDate,
            LocalDate endDate,
            boolean isActive,
            MembershipType type,
            Account client,
            Branch branch,
            Account createdBy
    ) {
        this.id = id;
        this.startDate = startDate;
        this.endDate = endDate;
        this.isActive = isActive;
        this.type = type;
        this.client = client;
        this.branch = branch;
        this.createdBy = createdBy;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public MembershipType getType() {
        return type;
    }

    public void setType(MembershipType type) {
        this.type = type;
    }

    public Account getClient() {
        return client;
    }

    public void setClient(Account client) {
        this.client = client;
    }

    public Branch getBranch() {
        return branch;
    }

    public void setBranch(Branch branch) {
        this.branch = branch;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Account getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Account createdBy) {
        this.createdBy = createdBy;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Membership that = (Membership) o;
        return isActive() == that.isActive() &&
               Objects.equals(getId(), that.getId()) &&
               Objects.equals(getStartDate(), that.getStartDate()) &&
               Objects.equals(getEndDate(), that.getEndDate()) &&
               getType() == that.getType() &&
               Objects.equals(getClient(), that.getClient()) &&
               Objects.equals(getBranch(), that.getBranch()) &&
               Objects.equals(getCreatedBy(), that.getCreatedBy()) &&
               Objects.equals(getCreatedAt(), that.getCreatedAt());
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                getId(),
                getStartDate(),
                getEndDate(),
                isActive(),
                getType(),
                getClient(),
                getBranch(),
                getCreatedBy(),
                getCreatedAt()
        );
    }

    @Override
    public String toString() {
        return "Membership{" +
               "id=" +
               id +
               ", startDate=" +
               startDate +
               ", endDate=" +
               endDate +
               ", isActive=" +
               isActive +
               ", type=" +
               type +
               ", client=" +
               client +
               ", branch=" +
               branch +
               ", created_by=" +
               createdBy +
               ", createdAt=" +
               createdAt +
               '}';
    }
}
