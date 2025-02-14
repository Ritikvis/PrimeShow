package com.Backend.BookMyShow.Configuration;

import com.Backend.BookMyShow.Models.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CustomUserDetails implements UserDetails {
    private String username;
    private String password;
    private List<GrantedAuthority> authorityList;

    public CustomUserDetails(User user) {
        this.username = user.getEmailId(); // Using email as username
        this.password = user.getPassword();

        // Handling roles properly
        String[] roles = user.getRoles().split(",");
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (String role : roles) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role.trim())); // Prefixing roles with "ROLE_"
        }
        this.authorityList = authorities;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorityList; // ✅ Returning actual roles
    }

    @Override
    public String getPassword() {
        return password; // ✅ Returning actual password
    }

    @Override
    public String getUsername() {
        return username; // ✅ Returning actual username
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // ✅ Changing to true so account is considered active
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // ✅ Changing to true so account isn't locked
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // ✅ Changing to true so credentials don't expire
    }

    @Override
    public boolean isEnabled() {
        return true; // ✅ Changing to true so user is enabled
    }
}
