package GymApp.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "coach")
public class Coach {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", referencedColumnName = "id", nullable = false)
    private Account account;

    public Coach() {
    }
    public Coach(Account account) {
        this.account = account;
    }

    public Coach(long id, Account account) {
        this.id = id;
        this.account = account;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    @Override
    public String toString() {
        return "Coach{" +
                "id=" + id +
                ", account=" + account +
                '}';
    }
}
