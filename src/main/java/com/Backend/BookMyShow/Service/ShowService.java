package com.Backend.BookMyShow.Service;

import com.Backend.BookMyShow.Models.*;
import com.Backend.BookMyShow.Repository.MovieRepository;
import com.Backend.BookMyShow.Repository.ShowRepository;
import com.Backend.BookMyShow.Repository.ShowSeatRepository;
import com.Backend.BookMyShow.Repository.TheaterRepository;
import com.Backend.BookMyShow.Requests.AddShowRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ShowService {
    @Autowired
    private ShowRepository showRepository;

    @Autowired
    private TheaterRepository theaterRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private ShowSeatRepository showSeatRepository;

    public String addShow(AddShowRequest showRequest) {
        Movie movie = movieRepository.findMovieByMovieName(showRequest.getMovieName());
        if (movie == null) {
            return "Error: Movie not found.";
        }

        Theater theater = theaterRepository.findById(showRequest.getTheaterId()).orElse(null);
        if (theater == null) {
            return "Error: Theater not found.";
        }

        Show show = Show.builder()
                .showDate(showRequest.getShowDate())
                .showTime(showRequest.getShowTime())
                .movie(movie)
                .theater(theater)
                .build();
        show = showRepository.save(show);

        List<TheaterSeat> theaterSeatList = theater.getTheaterSeatList();

        List<ShowSeat> showSeatList = new ArrayList<>();
        for (TheaterSeat theaterSeat : theaterSeatList) {
            ShowSeat showSeat = ShowSeat.builder()
                    .seatNo(theaterSeat.getSeatNo())
                    .seatType(theaterSeat.getSeatType())
                    .isBooked(Boolean.FALSE)
                    .isFoodAttached(Boolean.FALSE)
                    .show(show)
                    .build();
            showSeatList.add(showSeat);
        }

        show.setShowSeatList(showSeatList);
        showSeatRepository.saveAll(showSeatList);

        return "The show has been saved to the DB with showId " + show.getShowId();
    }
}
