package com.sales.security;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class UserDetailsImpl implements UserDetails {
    private Long id;
    private String email;
    private String password;
    private boolean active;
    private Collection<? extends GrantedAuthority> authorities;

    public UserDetailsImpl(Long id, String email, String password, boolean active, Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.active = active;
        this.authorities = authorities;
    }

    public static UserDetailsImpl build(com.sales.model.User user) {
        List<GrantedAuthority> authorities = List.of(
            new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
        );

        return new UserDetailsImpl(
            user.getId(),
            user.getEmail(),
            user.getPassword(),
            user.isActive(),
            authorities
        );
    }

    public Long getId() { return id; }
    public String getEmail() { return email; }

    @Override public Collection<? extends GrantedAuthority> getAuthorities() { return authorities; }
    @Override public String getPassword() { return password; }
    @Override public String getUsername() { return email; }
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return active; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return active; }
}
