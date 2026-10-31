package GymApp.service;

import GymApp.dao.RoleRepository;
import GymApp.entity.Role;
import GymApp.enums.Roles;
import GymApp.exception.NotFoundException;
import GymApp.service.Intrface.RoleService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;

    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public Role findByName(Roles role) {
        return roleRepository.findByName(role)
                             .orElseThrow(() -> new NotFoundException("Can't find Role with name: " +
                                                                      role.name()));
    }
}
