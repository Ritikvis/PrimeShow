package com.Backend.BookMyShow.Service;

import com.Backend.BookMyShow.Repository.UserRepository;
import com.Backend.BookMyShow.Requests.AddUserRequest;
import com.Backend.BookMyShow.Models.User;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final JavaMailSender javaMailSender;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserService(UserRepository userRepository, JavaMailSender javaMailSender) {
        this.userRepository = userRepository;
        this.javaMailSender = javaMailSender;
    }

    public String addUser(AddUserRequest userRequest) {
        // Check if user already exists
        Optional<User> existingUser = userRepository.findByEmailId(userRequest.getEmailId());
        if (existingUser.isPresent()) {
            return "User with this email ID already exists. Please use a different email.";
        }

        // Encrypt password before saving
        String encryptedPassword = passwordEncoder.encode(userRequest.getPassword());

        // Create and save new user
        User user = User.builder()
                .name(userRequest.getName())
                .age(userRequest.getAge())
                .emailId(userRequest.getEmailId())
                .mobileNo(userRequest.getMobileNo())
                .password(encryptedPassword) // Storing encrypted password
                .roles(userRequest.getRoles()) // Setting roles
                .build();

        user = userRepository.save(user);

        // Send a welcome email
        try {
            sendWelcomeEmail(userRequest.getEmailId(), userRequest.getName());
        } catch (Exception e) {
            System.err.println("Error sending email: " + e.getMessage());
            return "User registered successfully, but email sending failed.";
        }

        return "User registered successfully with userId: " + user.getUserId();
    }

    private void sendWelcomeEmail(String email, String name) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(email);
        mailMessage.setFrom("projectbackend45@gmail.com");
        mailMessage.setSubject("Welcome to Book My Show!");
        mailMessage.setText("Hello " + name + ",\n\n" +
                "Welcome to the Book My Show family! We're thrilled to have you on board. " +
                "Use WELCOME10 to get 10% off on your next ticket purchase. " +
                "Thank you for choosing Book My Show. Enjoy your movies!\n\n" +
                "Best regards,\nThe Book My Show Team");

        javaMailSender.send(mailMessage);
    }

    public List<User> ListOfUsers() {
        List<User> users = userRepository.findAll();
        return users;
    }
}
