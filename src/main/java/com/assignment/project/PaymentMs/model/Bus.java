package com.assignment.project.PaymentMs.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class Bus implements Serializable {
    @JsonProperty("bookingNumber")
    private String bookingNumber;

    @JsonProperty("numberOfSeats")
    private Integer numberOfSeats;

    public Bus(){}

    public Bus(String bookingNumber, Integer numberOfSeats) {
        this.bookingNumber=bookingNumber;
        this.numberOfSeats=numberOfSeats;
    }
}
