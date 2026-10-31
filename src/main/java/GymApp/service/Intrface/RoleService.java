package GymApp.service.Intrface;

import GymApp.entity.Role;
import GymApp.enums.Roles;

import java.util.Optional;

public interface RoleService {
    Role findByName(Roles role);
}
