package com.reservation.hotel.HotelReservation.reservation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ReservationService {

    public void updateDailyRate(Reservation resToUpdate, int updatedRate){
        //TODO: update with logic to clamp rate to a base value
        resToUpdate.setDailyRate(updatedRate);
    }
}
