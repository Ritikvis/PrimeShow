package com.Backend.BookMyShow.Repository;

import com.Backend.BookMyShow.Models.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Integer> {
}
