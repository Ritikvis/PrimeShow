package com.Backend.BookMyShow.Controller;


import com.Backend.BookMyShow.Requests.BookTicketRequest;
import com.Backend.BookMyShow.Response.TicketResponse;
import com.Backend.BookMyShow.Service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
}
