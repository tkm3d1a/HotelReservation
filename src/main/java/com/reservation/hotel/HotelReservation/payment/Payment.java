package com.reservation.hotel.HotelReservation.payment;


import com.reservation.hotel.HotelReservation.reservation.Reservation;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToOne
    private Reservation reservation;

    private String paymentInfo;
    private int totalBilled;

    //TODO: remove these? they are contained in reservation class
//    private int totalDaysReserved;
//    private int dailyRate;
}
