package GymApp.service;

import GymApp.dao.RoleRepository;
import GymApp.entity.Role;
import GymApp.exception.NotFoundException;
import GymApp.service.Intrface.RoleService;

import java.util.Optional;

public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;

    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public Role findByName(String roleName) {
        return roleRepository.findByName(roleName)
                             .orElseThrow(() -> new NotFoundException("Can't find Role with name: " +
                                                                      roleName));
    }
}
