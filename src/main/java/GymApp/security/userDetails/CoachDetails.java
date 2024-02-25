package GymApp.security.userDetails;

import GymApp.entity.Coach;
import GymApp.enums.AccountType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class CoachDetails implements CustomUserDetails {
    private final Coach coach;

    @Autowired
    public CoachDetails(Coach coach) {
        this.coach = coach;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return coach.getAccount().getPassword();
    }

    @Override
    public String getUsername() {
        return coach.getAccount().getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    public AccountType getAccountType(String accountType) {
        return AccountType.COACH;
    }
    public long getAccountId(){
        return coach.getAccount().getId();
    }
}
