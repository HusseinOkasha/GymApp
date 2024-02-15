package GymApp.security.userDetails;

import GymApp.entity.Client;
import GymApp.enums.AccountType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class ClientDetails implements CustomUserDetails {

    private final Client client;

    @Autowired
    public ClientDetails(Client client) {
        this.client = client;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return client.getAccount().getPassword();
    }

    @Override
    public String getUsername() {
        return client.getAccount().getEmail();
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
        return AccountType.CLIENT;
    }
}
