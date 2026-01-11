package GymApp.entity;

import GymApp.enums.Roles;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;

@Entity
@Table(name="role")
public class Role implements GrantedAuthority {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;
    @Enumerated(EnumType.STRING)
    @Column(name="name", nullable = false)
    private Roles name;
    public Role(){

    }
    public Role(Roles name){
        this.name = name;
    }
    public Long getId(){
        return this.id;
    }
    @Override
    public String getAuthority() {
        return name.toString();
    }
}
