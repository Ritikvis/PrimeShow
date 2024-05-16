package com.Backend.BookMyShow.Requests;

import lombok.Data;

@Data
public class AddTheaterRequest {

    private Integer noOfScreens;
    private String name;
    private String address;
}
