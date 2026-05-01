package com.sales.dto;

public class AuthResponse {
    private String token;
    private String email;
    private String role;
    private String name;

    public AuthResponse() {}
    public AuthResponse(String token, String email, String role, String name) {
        this.token = token;
        this.email = email;
        this.role = role;
        this.name = name;
    }

    public static AuthResponseBuilder builder() {
        return new AuthResponseBuilder();
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public static class AuthResponseBuilder {
        private String token;
        private String email;
        private String role;
        private String name;
        public AuthResponseBuilder token(String token) { this.token = token; return this; }
        public AuthResponseBuilder email(String email) { this.email = email; return this; }
        public AuthResponseBuilder role(String role) { this.role = role; return this; }
        public AuthResponseBuilder name(String name) { this.name = name; return this; }
        public AuthResponse build() { return new AuthResponse(token, email, role, name); }
    }
}
