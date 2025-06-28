package com.hust.ict.aims.service.impl;

import com.hust.ict.aims.model.Role;
import com.hust.ict.aims.repository.RoleRepository;
import com.hust.ict.aims.service.RoleService;
import org.springframework.stereotype.Service;

@Service
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;

    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public Role createRole(String roleName) {
        Role role = roleRepository.findByName(roleName).orElse(null);
        if (role == null) {
            role = new Role();
            role.setName(roleName);
        }
        return roleRepository.save(role);
    }
}
