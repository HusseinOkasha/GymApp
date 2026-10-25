package GymApp.service.Intrface;

import GymApp.entity.Role;

import java.util.Optional;

public interface RoleService {
    Role findByName(String roleName);
}
