package com.reservation.hotel.HotelReservation.payment;

import com.reservation.hotel.HotelReservation.reservation.Reservation;
import com.reservation.hotel.HotelReservation.reservation.ReservationService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Slf4j
@Service
public class PaymentService{

    @Resource
    PaymentRepository paymentRepository;

    @Resource
    ReservationService reservationService;

    public void associateReservation(Payment payment, int reservationID) {
        Reservation reservation = reservationService.findReservationByID(reservationID);
        log.info("Reservation info: {}", reservation);

        if(reservation.getGuest() != null) {
            log.info("Reservation with ResID {} found!", reservationID);
            if(reservation.isConfirmed()){
                payment.setReservation(reservation);
                paymentRepository.save(payment);
                log.info("Reservation was confirmed, created payment record");
            } else {
                log.warn("Reservation is not confirmed, cannot create payment record");
            }
        } else {
            String msg = "Reservation with ResID " + reservationID + " was not found";
            log.warn(msg);
            log.warn("Guest was null: {}", reservation.getGuest());
            throw new RuntimeException(msg);
        }

    }

    public void enterPaymentInfo(Payment payment, String paymentInfo) {
        if(!payment.isPaymentProcessed()){
            log.info("Payment not process yet, able to update payment info");
            payment.setPaymentInfo(paymentInfo);
            paymentRepository.save(payment);
        } else {
            log.warn("Payment has been processed, unable to update this payment object");
        }
    }

    public void processPayment(Payment payment) {
        payment.setPaymentProcessed(true);
        paymentRepository.save(payment);
    }

    public Payment getPaymentByReservationID(int reservationID) {
        Optional<Payment> oPayment = paymentRepository.findByReservation_Id(reservationID);
        Payment payment = new Payment();

        if(oPayment.isPresent()) {
            payment = oPayment.get();
            log.info("{}", payment);
        } else {
            log.warn("Payment not found for resID {}", reservationID);
            log.warn("Returning empty payment object: {}", payment);
        }

        return payment;
    }


    public List<Payment> findAllByGuestID(int guestID) {
        return paymentRepository.findAllByReservation_Guest_Id(guestID);
    }

    public Payment findPaymentForReservation(Reservation reservation) {
        Optional<Payment> optionalPayment = paymentRepository.findByReservation_Id(reservation.getId());
        Payment payment = new Payment();

        if(optionalPayment.isPresent()) {
            log.info("Payment found");
            payment = optionalPayment.get();
        } else {
            log.info("Payment not found, creating initial record");
            associateReservation(payment, reservation.getId());
        }

        return payment;
    }
}
