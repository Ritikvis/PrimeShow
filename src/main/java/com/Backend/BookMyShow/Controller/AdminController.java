package com.Backend.BookMyShow.Controller;

import com.Backend.BookMyShow.Models.Movie;
import com.Backend.BookMyShow.Models.TheaterSeat;
import com.Backend.BookMyShow.Models.User;
import com.Backend.BookMyShow.Requests.*;
import com.Backend.BookMyShow.Service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private TheaterService theaterService;
    @Autowired
    private ShowService showService;
    @Autowired
    private MovieService movieService;
    @Autowired
    private TicketService ticketService;
    @Autowired
    private UserService userService;
    @PreAuthorize("hasRole('ADMIN')") // Only allow access to admin
    @GetMapping("/dashboard")
    public String getAdminDashboard() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String adminName = authentication.getName(); // Get the authenticated username (admin name)
        return "Welcome " + adminName ;
    }


    @GetMapping("/gross-revenue/{movieName}")
    public Integer getGrossRevenueForMovie(@PathVariable String movieName) {
        return ticketService.calculateGrossRevenueForMovie(movieName);
    }
    @GetMapping("/total-revenue/theatre/{theaterId}/date/{date}")
    public Integer getTotalRevenueForTheatreOnDate(@PathVariable Integer theaterId, @PathVariable String date) {
        LocalDate localDate = LocalDate.parse(date);
        return ticketService.calculateTotalRevenueForTheatreOnDate(theaterId, localDate);
    }


    @PostMapping("add/Movie")
    public ResponseEntity addMovie(@RequestBody AddMovieRequest movieRequest){

        String response = movieService.addMovie(movieRequest);
        return new ResponseEntity(response, HttpStatus.OK);
    }

    @PutMapping("update/Movie")
    public ResponseEntity updateMovieAttributes(@RequestBody UpdateMovieRequest updateMovieRequest){

        String response = movieService.updateMovieAttributes(updateMovieRequest);
        return new ResponseEntity(response, HttpStatus.OK);
    }

    @PostMapping("add/Show")
    public ResponseEntity addShow(@RequestBody AddShowRequest addShowRequest){

        String response = showService.addShow(addShowRequest);
        return new ResponseEntity(response, HttpStatus.OK);
    }
    @PostMapping("add/Theaters")
    public ResponseEntity addTheater(@RequestBody AddTheaterRequest theaterRequest){

        String response = theaterService.addTheater(theaterRequest);
        return new ResponseEntity(response, HttpStatus.OK);
    }


    @PutMapping("theaters/associateSeats")
    public ResponseEntity associateSeats(@RequestBody AddTheaterSeatsRequest theaterSeatsRequest){

        String response = theaterService.associateTheaterSeats(theaterSeatsRequest);
        return new ResponseEntity(response,HttpStatus.OK);
    }
    @GetMapping("ListOfUsers")
    public List<User> ListOfUsers(){
        return userService.ListOfUsers();
    }


}
