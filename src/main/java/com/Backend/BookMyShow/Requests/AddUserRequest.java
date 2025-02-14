package com.Backend.BookMyShow.Requests;

import lombok.Data;

@Data
public class AddUserRequest {

    private String name;
    private Integer age;
    private  String emailId;
    private String mobileNo;
    private String password;
    private String roles;
}
