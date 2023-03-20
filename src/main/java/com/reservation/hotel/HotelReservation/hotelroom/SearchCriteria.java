package com.reservation.hotel.HotelReservation.hotelroom;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchCriteria {

    private java.time.LocalDate checkInDate;
    private java.time.LocalDate checkOutDate;
    private boolean executive;
    private boolean business;
    private boolean comfort;
    private boolean economy;
}
