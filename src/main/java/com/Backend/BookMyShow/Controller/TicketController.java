package com.Backend.BookMyShow.Controller;


import com.Backend.BookMyShow.Models.Movie;
import com.Backend.BookMyShow.Models.Theater;
import com.Backend.BookMyShow.Requests.BookTicketRequest;
import com.Backend.BookMyShow.Response.TicketResponse;
import com.Backend.BookMyShow.Service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("ticket")
public class TicketController {
    @Autowired
    private TicketService ticketService;

    @PostMapping("bookTicket")
    public String bookTicket(@RequestBody BookTicketRequest bookTicketRequest){

        return ticketService.bookTicket(bookTicketRequest);
    }

    @GetMapping("generateTicket")
    public TicketResponse generateTicket(@RequestParam("ticketId") String ticketId) {

        return ticketService.generateTicket(ticketId);
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
    @GetMapping("/by-movie/{movieName}")
    public List<Theater> getTheatersByMovie(@PathVariable String movieName) {
        return ticketService.findTheatersByMovie(movieName);
    }

}
