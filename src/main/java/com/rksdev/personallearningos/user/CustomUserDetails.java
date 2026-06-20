package com.rksdev.personallearningos.user;

import com.rksdev.personallearningos.user.model.UserEntity;
import com.rksdev.security.api.IdentifiableUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.stream.Collectors;

public class CustomUserDetails implements UserDetails, IdentifiableUser {

    private final Long userId; // Stores our numeric ID
    private final String username;
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;

    // Constructor accepts the numeric ID from the database entity
    public CustomUserDetails(UserEntity user) {
        this.userId = user.getId();
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.authorities = user.getRoles().stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    @Override
    public Long getUserId() {
        return this.userId; // Satisfies IdentifiableUser for the library
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
