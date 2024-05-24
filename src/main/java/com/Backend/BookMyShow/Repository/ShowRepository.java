package com.Backend.BookMyShow.Repository;

import com.Backend.BookMyShow.Models.Show;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface ShowRepository extends JpaRepository<Show,Integer> {

    List<Show> findByMovie_MovieName(String movieName);

}
