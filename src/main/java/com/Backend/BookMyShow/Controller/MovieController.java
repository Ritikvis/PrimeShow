package com.Backend.BookMyShow.Controller;

import com.Backend.BookMyShow.Models.Movie;
import com.Backend.BookMyShow.Requests.AddMovieRequest;
import com.Backend.BookMyShow.Requests.UpdateMovieRequest;
import com.Backend.BookMyShow.Service.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("movie")
public class MovieController {
    @Autowired
    private MovieService movieService;

    @PostMapping("add")
    public ResponseEntity addMovie(@RequestBody AddMovieRequest movieRequest){

        String response = movieService.addMovie(movieRequest);
        return new ResponseEntity(response, HttpStatus.OK);
    }

    @PutMapping("update")
    public ResponseEntity updateMovieAttributes(@RequestBody UpdateMovieRequest updateMovieRequest){

        String response = movieService.updateMovieAttributes(updateMovieRequest);
        return new ResponseEntity(response, HttpStatus.OK);
    }

}
