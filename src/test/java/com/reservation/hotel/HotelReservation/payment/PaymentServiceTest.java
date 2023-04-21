package com.reservation.hotel.HotelReservation.payment;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;

import java.util.List;

import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;

@Slf4j
@SpringBootTest
@ActiveProfiles("test")
@SqlGroup({
        @Sql(value = "classpath:paymentServiceTest.sql", executionPhase = BEFORE_TEST_METHOD)
})
public class PaymentServiceTest{

    @Autowired
    PaymentService paymentService;

    Payment payment;
    int[] reservationID = {99, 100, 101, 102};
    int badReservationID = 55;
    int guestID = 99;
//    int badGuestID = 55;

    @BeforeEach
    public void setup() {
        payment = new Payment();
    }

    @Test
    public void associateReservationTestWithBadReservationID() {
        Assertions.assertThrows(RuntimeException.class, () -> paymentService.associateReservation(payment, badReservationID));
    }

    @Test
    public void associateReservationTestWithGoodReservationID() {
        Assertions.assertNull(payment.getReservation());

        paymentService.associateReservation(payment, reservationID[3]);
        log.info("{}", payment);
        Assertions.assertEquals(2000, payment.getTotalToBill());
        Assertions.assertFalse(payment.isPaymentProcessed());
    }

    @Test
    public void processPaymentTest() {
        paymentService.associateReservation(payment, reservationID[0]);
        Assertions.assertFalse(payment.isPaymentProcessed());

        paymentService.processPayment(payment);
        Assertions.assertTrue(payment.isPaymentProcessed());
    }

    @Test
    public void getPaymentByResIDTestBadResID() {
        Assertions.assertNull(payment.getReservation());
        paymentService.associateReservation(payment, reservationID[0]);

        Payment testPayment = paymentService.getPaymentByReservationID(badReservationID);

        Assertions.assertNull(testPayment.getReservation());
        Assertions.assertEquals("EMPTY", testPayment.getPaymentInfo());
    }

    @Test
    public void getPaymentByResIDTest() {
        Assertions.assertNull(payment.getReservation());
        paymentService.associateReservation(payment, reservationID[0]);

        Payment testPayment = paymentService.getPaymentByReservationID(reservationID[0]);

        Assertions.assertEquals(payment, testPayment);
    }

    @Test
    public void findAllByGuestIDTest() {
        for(int id : reservationID) {
            Payment fePayment = new Payment();
            paymentService.associateReservation(fePayment, id);
        }
        List<Payment> payments;
        payments = paymentService.findAllByGuestID(guestID);
        Assertions.assertEquals(reservationID.length, payments.size());
        Assertions.assertEquals(1000, payments.get(0).getTotalToBill());
        Assertions.assertEquals(1500, payments.get(1).getTotalToBill());
        Assertions.assertEquals("PENDING", payments.get(0).getPaymentInfo());
    }

    @Test
    public void updatePaymentInfoTest() {
        Assertions.assertEquals("EMPTY", payment.getPaymentInfo());

        paymentService.associateReservation(payment, reservationID[0]);
        Assertions.assertEquals("PENDING", payment.getPaymentInfo());

        paymentService.enterPaymentInfo(payment, "Test update");
        Assertions.assertEquals("Test update", payment.getPaymentInfo());

        paymentService.processPayment(payment);
        paymentService.enterPaymentInfo(payment, "Test update after process");
        Assertions.assertNotEquals("Test update after process", payment.getPaymentInfo());
    }

}
