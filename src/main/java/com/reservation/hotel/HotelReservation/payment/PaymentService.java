package com.reservation.hotel.HotelReservation.payment;

import com.reservation.hotel.HotelReservation.reservation.Reservation;
import com.reservation.hotel.HotelReservation.reservation.ReservationService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class PaymentService{

    @Resource
    PaymentRepository paymentRepository;

    @Resource
    ReservationService reservationService;

    public void createReceipt(Payment payment, int reservationID){
        Reservation reservation = reservationService.findReservationByID(reservationID);
        log.info("Reservation info: {}", reservation);

        if(reservation.getGuest() != null) {
            log.info("Reservation with ResID {} found!", reservationID);
            payment.setReservation(reservation);
        } else {
            String msg = "Reservation with ResID " + reservationID + " was not found";
            log.warn(msg);
            log.warn("Guest was null: {}", reservation.getGuest());
            throw new RuntimeException(msg);
        }

        paymentRepository.save(payment);
    }

}
