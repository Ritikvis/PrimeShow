package com.Backend.BookMyShow.Requests;

import com.Backend.BookMyShow.Enum.Language;
import lombok.Data;

@Data
public class UpdateMovieRequest {
    private String movieName;
    private Language newLanguage;
    private Double newRating;
}
