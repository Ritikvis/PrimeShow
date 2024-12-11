package com.Backend.BookMyShow.Service;

import com.Backend.BookMyShow.Enum.SeatType;
import com.Backend.BookMyShow.Exception.BookingException;
import com.Backend.BookMyShow.Models.*;
import com.Backend.BookMyShow.Repository.*;
import com.Backend.BookMyShow.Requests.BookTicketRequest;
import com.Backend.BookMyShow.Response.TicketResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class TicketService {

    @Autowired
    private ShowRepository showRepository;

    @Autowired
    private ShowSeatRepository showSeatRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TicketRepository ticketRepository;

    private static final int FOOD_PRICE = 130; // Define food price

    public String bookTicket(BookTicketRequest bookTicketRequest) {
        // 1. Find the Show Entity
        Show show = showRepository.findById(bookTicketRequest.getShowId())
                .orElseThrow(() -> new BookingException("Show not found"));

        // 2. Find the User Entity
        User user = userRepository.findById(bookTicketRequest.getUserId())
                .orElseThrow(() -> new BookingException("User not found"));

        // 3. Initialize total amount and mark requested seats as booked
        Integer totalAmount = 0;
        List<ShowSeat> showSeatList = show.getShowSeatList();
        List<String> requestedSeats = bookTicketRequest.getRequestedSeats();
        List<String> unavailableSeats = new ArrayList<>();

        for (ShowSeat showSeat : showSeatList) {
            String seatNo = showSeat.getSeatNo();

            if (requestedSeats.contains(seatNo)) {
                // Check if the seat is already booked
                if (Boolean.TRUE.equals(showSeat.getIsBooked())) {
                    unavailableSeats.add(seatNo);
                } else {
                    // Mark the seat as booked and calculate the amount
                    showSeat.setIsBooked(Boolean.TRUE);

                    if (showSeat.getSeatType().equals(SeatType.CLASSIC)) {
                        totalAmount += 100;
                    } else {
                        totalAmount += 150;
                    }
                }
            }
        }

        // If there are any unavailable seats, throw an exception
        if (!unavailableSeats.isEmpty()) {
            throw new BookingException("Seats already booked: " + unavailableSeats);
        }

        // Add food price if food is included
        if (bookTicketRequest.isIncludeFood()) {
            totalAmount += FOOD_PRICE;
        }

        // 4. Create the Ticket Entity and set the attributes
        Ticket ticket = Ticket.builder()
                .showDate(show.getShowDate())
                .showTime(show.getShowTime())
                .movieName(show.getMovie().getMovieName())
                .theaterName(show.getTheater().getName())
                .totalAmount(totalAmount)
                .bookedSeats(requestedSeats.toString())
                .show(show)
                .user(user)
                .includeFood(bookTicketRequest.isIncludeFood()) // Set food inclusion
                .build();

        // Save updated seat statuses and ticket
        showSeatRepository.saveAll(showSeatList);
        ticket = ticketRepository.save(ticket);

        // 5. Save the ticket into the database and return the Ticket ID
        return ticket.getTicketId();
    }



    public TicketResponse generateTicket(String ticketId){
        Ticket ticket = ticketRepository.findById(ticketId).orElseThrow(() -> new RuntimeException("Ticket not found"));

        //Entity needs to be converted into TicketResponse

        TicketResponse ticketResponse = TicketResponse.builder()
                .bookedSeats(ticket.getBookedSeats())
                .movieName(ticket.getMovieName())
                .showTime(ticket.getShowTime())
                .showDate(ticket.getShowDate())
                .theaterName(ticket.getTheaterName())
                .totalAmount(ticket.getTotalAmount())
                .includeFood(ticket.isIncludeFood()) // Include food information
                .build();

        return ticketResponse;
    }

    public Integer calculateTotalRevenueForTheatreOnDate(Integer theaterId, LocalDate date) {
        List<Ticket> tickets = ticketRepository.findByShowTheaterTheaterIdAndShowShowDate(theaterId, date);
        int totalRevenue = 0;
        for (Ticket ticket : tickets) {
            totalRevenue += ticket.getTotalAmount();
        }
        return totalRevenue;
    }

    public Integer calculateGrossRevenueForMovie(String movieName) {
        List<Show> shows = showRepository.findByMovie_MovieName(movieName);
        int grossRevenue = 0;
        for (Show show : shows) {
            List<Ticket> tickets = ticketRepository.findByShow(show);
            for (Ticket ticket : tickets) {
                grossRevenue += ticket.getTotalAmount();
            }
        }
        return grossRevenue;
    }

    public List<Theater> findTheatersByMovie(String movieName) {
        List<Theater> theaters = new ArrayList<>();

        // Find all shows for the given movie
        List<Show> shows = showRepository.findByMovie_MovieName(movieName);

        // Extract theaters from the shows
        for (Show show : shows) {
            theaters.add(show.getTheater());
        }

        return theaters;
    }
}