package com.reservation.hotel.HotelReservation.hotelroom;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchCriteria {

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private java.time.LocalDate checkInDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private java.time.LocalDate checkOutDate;
    private boolean executive;
    private boolean business;
    private boolean comfort;
    private boolean economy;
    private String sourceForm;
}
