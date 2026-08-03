package com.arham.demo_project.Services;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class PasswordManager {

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public String hash(String rawPassword){
        return encoder.encode(rawPassword);
    }

    public boolean matches(String rawPassword, String hashedPassword){
        return encoder.matches(rawPassword, hashedPassword);
    }
}