package com.Backend.BookMyShow.Repository;

import com.Backend.BookMyShow.Models.Movie;
import com.Backend.BookMyShow.Models.Show;
import com.Backend.BookMyShow.Models.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket,String> {
    List<Ticket> findByShowTheaterTheaterIdAndShowShowDate(Integer theaterId, LocalDate date);


    List<Ticket> findByShow(Show show);

}
