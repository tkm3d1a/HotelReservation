package com.reservation.hotel.HotelReservation.payment;

import com.reservation.hotel.HotelReservation.reservation.Reservation;
import com.reservation.hotel.HotelReservation.reservation.ReservationRepository;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class PaymentService{

    @Resource
    PaymentRepository paymentRepository;

    @Resource
    ReservationRepository reservationRepository;

    public void createReceipt(Payment payment, int reservationID){
        Optional<Reservation> reservationOptional = reservationRepository.findById(reservationID);
        Reservation reservation;

        if(reservationOptional.isPresent()) {
            log.info("Reservation with ResID {} found!", reservationID);
            reservation = reservationOptional.get();
            payment.setReservation(reservation);
        } else {
            String msg = "Reservation with ResID " + reservationID + " was not found";
            log.warn(msg);
            throw new RuntimeException(msg);
        }

        paymentRepository.save(payment);
    }

}
