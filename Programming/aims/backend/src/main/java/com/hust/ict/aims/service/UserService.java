package com.hust.ict.aims.service;

import com.hust.ict.aims.dto.RegisterRequestDTO;
import com.hust.ict.aims.model.User;

public interface UserService {
    User findByEmail(String email);
    User createUser(RegisterRequestDTO registerRequest);
}
