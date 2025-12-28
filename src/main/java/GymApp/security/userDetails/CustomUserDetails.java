package GymApp.security.userDetails;

import GymApp.enums.UserRoles;

import org.springframework.security.core.userdetails.UserDetails;


public interface CustomUserDetails extends UserDetails {
   UserRoles getAccountType(String accountType);

}
