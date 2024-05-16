package com.Backend.BookMyShow.Repository;

import com.Backend.BookMyShow.Models.TheaterSeat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TheaterSeatsRepository extends JpaRepository<TheaterSeat,Integer> {
}
