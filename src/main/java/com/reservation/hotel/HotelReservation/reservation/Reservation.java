package com.reservation.hotel.HotelReservation.reservation;

import com.reservation.hotel.HotelReservation.hotelroom.Room;
import com.reservation.hotel.HotelReservation.hoteluser.HotelUser;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "guest_id")
    private HotelUser guest;

    @ManyToOne
    @JoinColumn(name = "room_id")
    private Room room;

    private int dailyRate;
    private boolean isConfirmed;

    private java.time.LocalDate startDate;
    private java.time.LocalDate endDate;

}
