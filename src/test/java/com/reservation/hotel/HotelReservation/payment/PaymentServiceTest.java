package com.reservation.hotel.HotelReservation.payment;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;

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

    @Test
    public void createReceiptTestWithBadReservationID() {
        Payment payment = new Payment();
        int reservationID = 55;

        Assertions.assertThrows(RuntimeException.class, () -> paymentService.createReceipt(payment, reservationID));
    }

    @Test
    public void createReceiptTestWithGoodReservationID() {
        Payment payment = new Payment();
        int reservationID = 99;
        Assertions.assertNotEquals(1, payment.getId());
        paymentService.createReceipt(payment, reservationID);
        log.info("{}", payment);
        Assertions.assertEquals(1000, payment.getTotalBilled());
    }

}
