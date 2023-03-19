package com.reservation.hotel.HotelReservation.hotelroom;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(
            unique = true,
            nullable = false
    )
    private int roomNumber;
    private String bedType;
    private int bedNumber;
    private String quality;
    private String smokingStatus;
    private int baseRate;
}
