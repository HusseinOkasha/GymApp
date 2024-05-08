package GymApp.entity;


import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

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

    private Client(){}

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

    public static class Builder{

        private long id;
        private Account account;
        private LocalDate birthDate;

        public Builder(){}

        public Builder id(long id){
            this.id = id;
            return this;
        }

        public Builder account(Account account){
            Account.Builder accountBuilder = new Account.Builder();
            this.account = accountBuilder.copyFrom(account).build();
            return this;
        }
        public Builder birthDate(LocalDate birthDate){
            this.birthDate = birthDate;
            return this;
        }
        public Client build(){
            Client client =  new Client();
            client.id = this.id;
            client.account = this.account;
            client.birthDate = this.birthDate;
            return client;
        }







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
