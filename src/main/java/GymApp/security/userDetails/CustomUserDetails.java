package GymApp.security.userDetails;

import GymApp.enums.AccountType;

import org.springframework.security.core.userdetails.UserDetails;


public interface CustomUserDetails extends UserDetails {
   AccountType getAccountType(String accountType);

}
