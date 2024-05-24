package com.Backend.BookMyShow.Repository;

import com.Backend.BookMyShow.Models.Movie;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MovieRepository extends JpaRepository<Movie,Integer> {
    Movie findMovieByMovieName(String movieName);

}
