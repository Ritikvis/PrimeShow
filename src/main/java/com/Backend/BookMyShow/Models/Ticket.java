package com.Backend.BookMyShow.Models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "tickets")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String ticketId;

    private String bookedSeats;

    private LocalDate showDate;

    private LocalTime showTime;

    private String movieName;

    private String theaterName;

    private Integer totalAmount;

    @JoinColumn
    @ManyToOne
    private Show show;

    @JoinColumn
    @ManyToOne
    private User user;

}
