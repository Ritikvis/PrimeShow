package com.Backend.BookMyShow.Requests;

import lombok.Data;

@Data
public class AddTheaterSeatsRequest {
    private Integer theaterId;
    private Integer noOfClassicSeats;
    private Integer noOfPremiumSeats;
}
