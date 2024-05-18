package com.Backend.BookMyShow.Repository;

import com.Backend.BookMyShow.Models.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketRepository extends JpaRepository<Ticket,String> {
}
