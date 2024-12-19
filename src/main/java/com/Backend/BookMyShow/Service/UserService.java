package com.Backend.BookMyShow.Service;

import com.Backend.BookMyShow.Repository.UserRepository;
import com.Backend.BookMyShow.Requests.AddUserRequest;
import com.Backend.BookMyShow.Models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JavaMailSender javaMailSender;

    public String addUser(AddUserRequest userRequest) {
        // Check if a user with this email ID already exists
        Optional<User> existingUser = userRepository.findByEmailId(userRequest.getEmailId());
        if (existingUser.isPresent()) {
            return "User with this email ID already exists. Please use a different email.";
        }

        // Create a new user
        User user = User.builder().age(userRequest.getAge())
                .emailId(userRequest.getEmailId())
                .name(userRequest.getName())
                .mobileNo(userRequest.getMobileNo())
                .build();

        try {
            // Send a welcome email
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(userRequest.getEmailId());
            mailMessage.setFrom("projectbackend45@gmail.com");
            mailMessage.setSubject("Welcome to Book My Show Application!");
            mailMessage.setText("Hello " + userRequest.getName() + ",\n\nWelcome to the Book My Show family! We're thrilled to have you on board. Enjoy WELCOME10 to get 10% off on your next ticket purchase. Thank you for choosing Book My Show. Have an amazing time at the movies!\n\nBest regards,\nThe Book My Show Team");

            javaMailSender.send(mailMessage);
        } catch (Exception e) {
            System.err.println("Error sending email: " + e.getMessage());
            return "User saved, but email sending failed.";
        }

        // Save the user in the database
        user = userRepository.save(user);
        return "User has been saved to the DB with userId " + user.getUserId();
    }
}
