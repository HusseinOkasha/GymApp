package GymApp.security.userDetails;

import GymApp.entity.Owner;
import GymApp.enums.AccountType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class OwnerDetails implements CustomUserDetails {

    private final Owner owner;

    @Autowired
    public OwnerDetails(Owner owner) {
        this.owner = owner;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return owner.getAccount().getPassword();
    }

    @Override
    public String getUsername() {
        return owner.getAccount().getEmail();
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
        return AccountType.OWNER;
    }
    public long getAccountId(){
        return owner.getAccount().getId();
    }
}
