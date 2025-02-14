package com.Backend.BookMyShow.Configuration;

import com.Backend.BookMyShow.Models.User;
import com.Backend.BookMyShow.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String emailId) throws UsernameNotFoundException {
        Optional<User> userOptional = userRepository.findByEmailId(emailId);
        if (userOptional.isEmpty()) {
            throw new UsernameNotFoundException("Invalid Email");
        }

        User user = userOptional.get();

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmailId())
                .password(user.getPassword()) // ✅ Password must be encoded in DB
                .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRoles().toUpperCase()))) // ✅ Ensure correct role format
                .build();
    }
}
