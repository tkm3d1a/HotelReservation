package com.reservation.hotel.HotelReservation.reservation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Integer> {
    List<Reservation> findAllByGuest_Id(int id);
    List<Reservation> findAllByIsConfirmed(boolean isConfirmed);

    List<Reservation> findAllByStartDateBetween(LocalDate startDateSearch, LocalDate endDateSearch);
    List<Reservation> findAllByEndDateBetween(LocalDate startDateSearch, LocalDate endDateSearch);
}
