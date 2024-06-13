package com.example.pdfmanagement.service;

import com.example.pdfmanagement.model.Role;
import com.example.pdfmanagement.model.AppUser;
import com.example.pdfmanagement.repository.RoleRepository;
import com.example.pdfmanagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public AppUser registerUser(AppUser user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setActive(true);

        // Ensure roles are persisted before assigning them to the user
        Set<Role> roles = new HashSet<>();
        for (Role role : user.getRoles()) {
            Optional<Role> existingRole = roleRepository.findByName(role.getName());
            if (existingRole.isEmpty()) {
                roles.add(roleRepository.save(role));
            } else {
                roles.add(existingRole.get());
            }
        }
        user.setRoles(roles);

        return userRepository.save(user);
    }

    public void deactivateUser(Long userId) {
        AppUser user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        user.setActive(false);
        userRepository.save(user);
    }

    public void activateUser(Long userId) {
        AppUser user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        user.setActive(true);
        userRepository.save(user);
    }

    public List<AppUser> getAllUsers() {
        return userRepository.findAll();
    }
}
