package GymApp.entity;


import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "Client")
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "account_id", referencedColumnName = "id", nullable = false)
    private Account account;
    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    public Client(){}

    public Client(Account account, LocalDate birthDate) {
        this.account = account;
        this.birthDate = birthDate;
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

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    @Override
    public String toString() {
        return "Client{" +
                "id=" + id +
                ", account=" + account +
                ", birthDate=" + birthDate +
                '}';
    }

}
