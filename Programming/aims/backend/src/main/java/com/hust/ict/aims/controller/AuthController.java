package com.hust.ict.aims.controller;

import com.hust.ict.aims.dto.LoginRequestDTO;
import com.hust.ict.aims.dto.RegisterRequestDTO;
import com.hust.ict.aims.dto.ResponseTokenDTO;
import com.hust.ict.aims.dto.UserResponseDTO;
import com.hust.ict.aims.model.Role;
import com.hust.ict.aims.model.User;
import com.hust.ict.aims.security.jwt.JwtService;
import com.hust.ict.aims.service.RoleService;
import com.hust.ict.aims.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtService jwtService;
    private final RoleService roleService;

    public AuthController(AuthenticationManager authenticationManager, UserService userService, JwtService jwtService, RoleService roleService) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.jwtService = jwtService;
        this.roleService = roleService;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequestDTO loginRequestDTO) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginRequestDTO.getUsername(), loginRequestDTO.getPassword()
        ));

        User user = userService.findByEmail(loginRequestDTO.getUsername());
        if (user != null) {
            String token = jwtService.generateToken(user);

            // If your frontend expects user info along with token:
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("user", new UserResponseDTO(user.getId(), user.getName(), user.getEmail(), user.getRoles().stream()
                    .map(Role::getName)
                    .collect(Collectors.toList())));

            return ResponseEntity.ok(response);
        } else {
            throw new UsernameNotFoundException("Username or password is incorrect");
        }
    }

    @PostMapping("/create-role")
    public Role registerRole(@RequestBody String roleName) {
        return roleService.createRole(roleName);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequestDTO registerRequest) {
        try {
            // Create the new user
            User newUser = userService.createUser(registerRequest);

            // Generate JWT token for auto-login after registration
            String token = jwtService.generateToken(newUser);

            // Create user response without password
            UserResponseDTO userResponse = new UserResponseDTO(
                    newUser.getId(),
                    newUser.getName(),
                    newUser.getEmail(),
                    newUser.getRoles().stream()
                            .map(Role::getName)
                            .collect(Collectors.toList())
            );

            // Return token and user info
            return ResponseEntity.ok(new ResponseTokenDTO(token));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
