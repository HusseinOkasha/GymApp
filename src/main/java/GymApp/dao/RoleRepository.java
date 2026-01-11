package GymApp.dao;

import GymApp.entity.Role;
import GymApp.enums.Roles;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Role findRoleByName(Roles role);
}
