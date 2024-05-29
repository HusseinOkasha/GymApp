package GymApp.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "coach")
public class Coach {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToOne(cascade = CascadeType.ALL, optional = false)
    @JoinColumn(name = "account_id", referencedColumnName = "id", nullable = false)
    private Account account;

    public Coach() {
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

    public static class Builder {
        private long id;
        private Account account;

        public Builder id(long id){
            this.id = id;
            return this;
        }

        public Builder account(Account account){
            Account.Builder accountBuilder = new Account.Builder();
            this.account = accountBuilder.copyFrom(account).build();
            return this;
        }
        public Builder copyFrom(Coach coach){
            Account.Builder accountBuilder = new Account.Builder();
            this.account = accountBuilder.copyFrom(coach.getAccount()).build();
            this.id = coach.id;
            return this;
        }
        public Coach build(){
            Coach coach = new Coach();
            coach.id = this.id;
            coach.account = this.account;
            return coach;
        }

    }

    @Override
    public String toString() {
        return "Coach{" +
                "id=" + id +
                ", account=" + account +
                '}';
    }
}
