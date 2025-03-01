package com.Backend.BookMyShow.Repository;

import com.Backend.BookMyShow.Models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Integer> {

    Optional<User> findByEmailId(String emailId);

}
