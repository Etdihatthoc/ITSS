package com.hust.ict.aims.service.impl;

import com.hust.ict.aims.dto.RegisterRequestDTO;
import com.hust.ict.aims.model.Role;
import com.hust.ict.aims.model.User;
import com.hust.ict.aims.repository.RoleRepository;
import com.hust.ict.aims.repository.UserRepository;
import com.hust.ict.aims.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }

    @Override
    public User findByEmail(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        return user.orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Override
    public User createUser(RegisterRequestDTO registerRequest) {

        if (userRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
            throw new RuntimeException("User with this email already exists");
        }

        User newUser = new User();
        newUser.setName(registerRequest.getName());
        newUser.setEmail(registerRequest.getEmail());
        newUser.setPassword(passwordEncoder.encode(registerRequest.getPassword()));

        List<Role> roles = new ArrayList<>();
        if (registerRequest.getRoles() == null || registerRequest.getRoles().isEmpty()) {
            Role defaultRole = roleRepository.findByName("ROLE_USER")
                    .orElseThrow(() -> new RuntimeException("Default role not found"));
            roles.add(defaultRole);
        } else {
            for (String roleName : registerRequest.getRoles()) {
                Role role = roleRepository.findByName(roleName)
                        .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));
                roles.add(role);
            }
        }

        newUser.setRoles(roles);
        return userRepository.save(newUser);
    }
}