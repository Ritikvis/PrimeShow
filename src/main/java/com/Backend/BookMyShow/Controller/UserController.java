package com.Backend.BookMyShow.Controller;

import com.Backend.BookMyShow.Models.Movie;
import com.Backend.BookMyShow.Models.Theater;
import com.Backend.BookMyShow.Requests.AddUserRequest;
import com.Backend.BookMyShow.Requests.BookTicketRequest;
import com.Backend.BookMyShow.Response.TicketResponse;
import com.Backend.BookMyShow.Service.MovieService;
import com.Backend.BookMyShow.Service.TicketService;
import com.Backend.BookMyShow.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private TicketService ticketService;
    @Autowired
    private MovieService movieService;

    @PostMapping("/signup")
    public ResponseEntity<String> addUser(@RequestBody AddUserRequest userRequest) {
        return ResponseEntity.ok(userService.addUser(userRequest));
    }
    @GetMapping("theater/by-movie/{movieName}")
    public List<Theater> getTheatersByMovie(@PathVariable String movieName) {
        return ticketService.findTheatersByMovie(movieName);
    }
    @PostMapping("bookTicket")
    public String bookTicket(@RequestBody BookTicketRequest bookTicketRequest){

        return ticketService.bookTicket(bookTicketRequest);
    }

    @GetMapping("generateTicket")
    public TicketResponse generateTicket(@RequestParam("ticketId") String ticketId) {

        return ticketService.generateTicket(ticketId);
    }
    @GetMapping("ListOfMovies")
    public List<Movie> ListOfMovies(){
        return movieService.ListOfMovies();
    }
    @DeleteMapping("/cancel/{ticketId}")
    public ResponseEntity<String> cancelTicket(@PathVariable String ticketId) {
        // Find the ticket and cancel it
        String response = ticketService.cancelTicket(ticketId);
        return ResponseEntity.ok(response);
    }
}
