package com.reservation.hotel.HotelReservation.reservation;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Integer> {
    List<Reservation> findAllByGuest_Id(int id);
}
