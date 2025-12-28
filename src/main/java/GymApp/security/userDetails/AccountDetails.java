package GymApp.security.userDetails;

import GymApp.entity.Account;
import GymApp.enums.UserRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;

public class AccountDetails implements CustomUserDetails {

    private final Account account;

    @Autowired
    public AccountDetails(Account account) {
        this.account = account;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return account.getRoles().stream().map(role-> new SimpleGrantedAuthority(role.getAuthority())).toList();
    }

    @Override
    public String getPassword() {
        return account.getPassword();
    }

    @Override
    public String getUsername() {
        return account.getEmail();
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

    public long getAccountId(){
        return account.getId();
    }

    @Override
    public UserRoles getAccountType(String accountType) {
        return null;
    }
}
