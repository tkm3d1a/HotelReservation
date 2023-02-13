package com.reservation.hotel.HotelReservation.hotelroom;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RoomService {
    @Autowired
    private RoomRepository roomRepository;
}
