package com.sales.service;

import com.sales.model.User;
import com.sales.model.enums.Role;
import com.sales.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> getAll() {
        return userRepository.findAll();
    }

    public User getById(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found: " + id));
    }

    public User create(UserRequest req) {
        if (userRepository.existsByEmail(req.getEmail()))
            throw new RuntimeException("Email already registered: " + req.getEmail());

        User user = new User();
        user.setName(req.getName());
        user.setEmail(req.getEmail());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setRole(req.getRole() != null ? req.getRole() : Role.SALESPERSON);
        user.setPhone(req.getPhone());
        user.setActive(true);
        return userRepository.save(user);
    }

    public User update(Long id, UserRequest req) {
        User user = getById(id);
        user.setName(req.getName());
        user.setPhone(req.getPhone());
        if (req.getRole() != null) user.setRole(req.getRole());
        if (req.getPassword() != null && !req.getPassword().isBlank())
            user.setPassword(passwordEncoder.encode(req.getPassword()));
        return userRepository.save(user);
    }

    public User toggleActive(Long id) {
        User user = getById(id);
        user.setActive(!user.isActive());
        return userRepository.save(user);
    }

    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    public static class UserRequest {
        private String name;
        private String email;
        private String password;
        private Role role;
        private String phone;
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public Role getRole() { return role; }
        public void setRole(Role role) { this.role = role; }
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
    }
}
