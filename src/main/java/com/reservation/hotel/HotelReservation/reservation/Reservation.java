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

    private int dailyRate = 1;
    private int totalRate = 1;
    private boolean isConfirmed = false;
    private boolean isNotStarted = true;
    private boolean isPromoApplied = false;

    @Column(nullable = false)
    private java.time.LocalDate startDate;
    @Column(nullable = false)
    private java.time.LocalDate endDate;
    private int numDays = 1;

}
