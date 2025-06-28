package com.hust.ict.aims.dto;

import com.hust.ict.aims.model.Role;
import lombok.Data;

import java.util.Set;

@Data
public class RegisterRequestDTO {
    private String name;
    private String email;
    private String password;
    private Set<String> roles;
}