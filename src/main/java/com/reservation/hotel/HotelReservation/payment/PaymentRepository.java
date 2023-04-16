package com.reservation.hotel.HotelReservation.payment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Integer>{
    Optional<Payment> findByReservation_Id(int reservationID);
    List<Payment> findAllByReservation_Guest_Id(int id);
    List<Payment> findAllByReservation_Guest_Username(String userName);
}
