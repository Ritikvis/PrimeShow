package com.Backend.BookMyShow.Service;

import com.Backend.BookMyShow.Enum.SeatType;
import com.Backend.BookMyShow.Exception.BookingException;
import com.Backend.BookMyShow.Models.*;
import com.Backend.BookMyShow.Repository.*;
import com.Backend.BookMyShow.Requests.BookTicketRequest;
import com.Backend.BookMyShow.Response.TicketResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
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
    @Autowired
    private UserService userService;
    @Autowired
    private JavaMailSender javaMailSender;

    private static final int FOOD_PRICE = 130;
    private static final int CLASSIC_SEAT_PRICE = 130;
    private static final int PREMIUM_SEAT_PRICE = 250;// Define food price


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
                        totalAmount +=CLASSIC_SEAT_PRICE ; // Classic seat price
                    } else {
                        totalAmount += PREMIUM_SEAT_PRICE; // Premium seat price
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
        sendBookingEmail(user, ticket);

        // 5. Save the ticket into the database and return the Ticket ID
        return ticket.getTicketId();
    }

    private void sendBookingEmail(User user, Ticket ticket) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(user.getEmailId());
        mailMessage.setFrom("projectbackend45@gmail.com");
        mailMessage.setSubject("Your Ticket Booking Confirmation - BookMyShow");

        StringBuilder emailBody = new StringBuilder();
        emailBody.append("Hi ").append(user.getName()).append(",\n\n");
        emailBody.append("Thank you for booking your ticket with BookMyShow!\n");
        emailBody.append("Here are your ticket details:\n");
        emailBody.append("Movie Name: ").append(ticket.getMovieName()).append("\n");
        emailBody.append("Theater Name: ").append(ticket.getTheaterName()).append("\n");
        emailBody.append("Show Date: ").append(ticket.getShowDate()).append("\n");
        emailBody.append("Show Time: ").append(ticket.getShowTime()).append("\n");
        emailBody.append("Booked Seats: ").append(ticket.getBookedSeats()).append("\n");
        emailBody.append("Total Amount: ").append(ticket.getTotalAmount()).append("\n\n");
        emailBody.append("Please proceed to payment to confirm your booking.\n");
        emailBody.append("Thank you!\n\nBest Regards,\nBookMyShow Team");

        mailMessage.setText(emailBody.toString());

        // Send the email
        javaMailSender.send(mailMessage);
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
    public String cancelTicket(String ticketId) {
        // 1. Find the Ticket Entity
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new BookingException("Ticket not found"));

        // 4. If the ticket had booked seats, make them available again
        List<ShowSeat> showSeats = ticket.getShow().getShowSeatList();
        for (ShowSeat seat : showSeats) {
            if (ticket.getBookedSeats().contains(seat.getSeatNo())) {
                seat.setIsBooked(false); // Mark seat as available
            }
        }

        // 5. Save updated seats
        showSeatRepository.saveAll(showSeats);
        ticketRepository.delete(ticket);

        // 6. Send cancellation confirmation email
        sendCancellationEmail(ticket.getUser(), ticket);

        return "Ticket canceled successfully";
    }

    private void sendCancellationEmail(User user, Ticket ticket) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(user.getEmailId());
        mailMessage.setFrom("projectbackend45@gmail.com");
        mailMessage.setSubject("Your Ticket Cancellation Confirmation - BookMyShow");

        StringBuilder emailBody = new StringBuilder();
        emailBody.append("Hi ").append(user.getName()).append(",\n\n");
        emailBody.append("We regret to inform you that your ticket has been successfully canceled.\n");
        emailBody.append("Here are the ticket details:\n");
        emailBody.append("Movie Name: ").append(ticket.getMovieName()).append("\n");
        emailBody.append("Theater Name: ").append(ticket.getTheaterName()).append("\n");
        emailBody.append("Show Date: ").append(ticket.getShowDate()).append("\n");
        emailBody.append("Show Time: ").append(ticket.getShowTime()).append("\n");
        emailBody.append("Booked Seats: ").append(ticket.getBookedSeats()).append("\n");
        emailBody.append("We hope to see you again soon!\n\nBest Regards,\nBookMyShow Team");

        mailMessage.setText(emailBody.toString());

        // Send the email
        javaMailSender.send(mailMessage);
    }




}