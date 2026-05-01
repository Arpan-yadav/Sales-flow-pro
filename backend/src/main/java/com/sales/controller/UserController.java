package com.sales.controller;

import com.sales.dto.ApiResponse;
import com.sales.model.User;
import com.sales.service.UserService;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/users")
@PreAuthorize("hasRole('ADMIN')")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success(userService.getAll().stream().map(this::toMap).toList()));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> create(@RequestBody UserService.UserRequest req) {
        User u = userService.create(req);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("User created", toMap(u)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> update(
            @PathVariable Long id, @RequestBody UserService.UserRequest req) {
        return ResponseEntity.ok(ApiResponse.success(toMap(userService.update(id, req))));
    }

    @PatchMapping("/{id}/toggle")
    public ResponseEntity<ApiResponse<Map<String, Object>>> toggle(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(toMap(userService.toggleActive(id))));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("User deleted", null));
    }

    private Map<String, Object> toMap(User u) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id",        u.getId());
        m.put("name",      u.getName());
        m.put("email",     u.getEmail());
        m.put("role",      u.getRole());
        m.put("phone",     u.getPhone() != null ? u.getPhone() : "");
        m.put("active",    u.isActive());
        m.put("createdAt", u.getCreatedAt());
        return m;
    }
}
