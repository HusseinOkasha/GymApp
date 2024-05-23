package GymApp.entity;

import jakarta.persistence.*;

@Entity
@Table(name="owner")
public class Owner {
    @Id
    @Column(name="id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "account_id", referencedColumnName = "id", nullable = false)
    private Account account;

    public Owner (){}

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

    public static class Builder{

        private  long id;
        private Account account;

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
        public Builder copyFrom(Owner owner){
            Account.Builder accountBuilder =  new Account.Builder();
            this.account = accountBuilder.copyFrom(owner.getAccount()).build();
            this.id = owner.id;
            return this;
        }
        public Owner build(){
            Owner owner =  new Owner();
            owner.id = this.id;
            owner.account = this.account;
            return owner;
        }

    }


    @Override
    public String toString() {
        return "Owner{" +
                "id=" + id +
                ", account=" + account +
                '}';
    }
}
