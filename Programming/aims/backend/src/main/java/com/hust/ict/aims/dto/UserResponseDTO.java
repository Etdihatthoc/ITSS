package com.hust.ict.aims.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
public class UserResponseDTO {
    private int id;
    private String name;
    private String email;
    private List<String> roles;
    // Password is deliberately omitted for security
}