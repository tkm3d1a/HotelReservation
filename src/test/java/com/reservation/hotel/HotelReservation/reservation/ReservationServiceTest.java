package com.reservation.hotel.HotelReservation.reservation;


import com.reservation.hotel.HotelReservation.hotelroom.Room;
import com.reservation.hotel.HotelReservation.hotelroom.RoomRepository;
import com.reservation.hotel.HotelReservation.hoteluser.HotelUser;
import com.reservation.hotel.HotelReservation.hoteluser.HotelUserRepository;
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
        @Sql(value = "classpath:reservationServiceTest.sql", executionPhase = BEFORE_TEST_METHOD)
})
public class ReservationServiceTest {

    Reservation reservationStatic;

    @Autowired
    ReservationService reservationService;

    @Autowired
    HotelUserRepository hotelUserRepository;

    @Autowired
    RoomRepository roomRepository;

    @BeforeEach
    public void setupTests(){
        reservationStatic = new Reservation();
        saveTestUser_1();
        saveTestRoom_1();
        saveTestRoom_2();
        saveTestRoom_3();
    }
//
//    @AfterEach
//    public void tearDownTests() {
//        hotelUserRepository.deleteAll();
//        roomRepository.deleteAll();
//    }

    //add a reservation
    @Test
    public void createNewReservationTest() {
        Reservation reservation = reservationService.createNewReservation(
                "100",
                "tester",
                "2023-04-13",
                "2023-04-15"
                );

        Assertions.assertEquals(2, reservation.getNumDays());
    }

    @Test
    public void saveReservationTest() {
        Reservation reservation = reservationService.createNewReservation(
                "100",
                "tester",
                "2023-04-13",
                "2023-04-15"
        );

        reservationService.saveReservation(reservation);
        log.info("{}", reservation);

        Assertions.assertEquals(1, reservation.getId());
    }

    // add promo code before confirming
    @Test
    public void applyPromoBeforeConfirmTest() {
        Reservation reservation = reservationService.createNewReservation(
                "100",
                "tester",
                "2023-04-13",
                "2023-04-15"
        );
        reservationService.saveReservation(reservation);
        int savedID = reservation.getId();
        Reservation reservationFoundBefore = reservationService.findReservationByID(savedID);
        Assertions.assertEquals(100, reservationFoundBefore.getRoom().getRoomNumber());
        Assertions.assertFalse(reservationFoundBefore.isConfirmed());
        Assertions.assertFalse(reservationFoundBefore.isPromoApplied());

        reservationService.applyPromo("1234", savedID);
        Reservation reservationFoundAfter = reservationService.findReservationByID(savedID);
        Assertions.assertEquals(100, reservationFoundAfter.getRoom().getRoomNumber());
        Assertions.assertFalse(reservationFoundAfter.isConfirmed());
        Assertions.assertTrue(reservationFoundAfter.isPromoApplied());
    }

    //add promo code after confirming
    @Test
    public void applyPromoAfterConfirmingTest() {
        Reservation reservation = reservationService.createNewReservation(
                "100",
                "tester",
                "2023-04-13",
                "2023-04-15"
        );
        reservationService.saveReservation(reservation);
        int savedID = reservation.getId();
        Reservation reservationFoundBefore = reservationService.findReservationByID(savedID);
        Assertions.assertEquals(100, reservationFoundBefore.getRoom().getRoomNumber());
        Assertions.assertFalse(reservationFoundBefore.isConfirmed());
        Assertions.assertFalse(reservationFoundBefore.isPromoApplied());

        reservationService.confirmRoom(savedID, "tester");
        Reservation reservationFoundAfter = reservationService.findReservationByID(savedID);
        Assertions.assertEquals(100, reservationFoundAfter.getRoom().getRoomNumber());
        Assertions.assertTrue(reservationFoundAfter.isConfirmed());
        Assertions.assertFalse(reservationFoundAfter.isPromoApplied());

        reservationService.applyPromo("1234", savedID);
        Reservation reservationFoundAfterPromo = reservationService.findReservationByID(savedID);
        Assertions.assertEquals(100, reservationFoundAfterPromo.getRoom().getRoomNumber());
        Assertions.assertTrue(reservationFoundAfterPromo.isConfirmed());
        Assertions.assertTrue(reservationFoundAfterPromo.isPromoApplied());
    }

    @Test
    public void findRoomForResWithOnlyRoomIDPopulated() {
        Reservation reservation = reservationService.createNewReservation(
                "100",
                "tester",
                "2023-04-13",
                "2023-04-15"
        );
        log.info("{}", reservation.getRoom());
        // Need to force room after creation to be an empty room object with just a room ID for searching in DB
        Room room = new Room();
        room.setId(reservation.getRoom().getId());
        reservation.setRoom(room);

        Assertions.assertEquals(0, reservation.getRoom().getRoomNumber());

        reservationService.findRoomForRes(reservation);

        Assertions.assertEquals(100, reservation.getRoom().getRoomNumber());
    }

    @Test
    public void findAllReservationsShouldReturnList() {
        List<Reservation> reservations = reservationService.findAllReservations();
        int reservationsSize = reservations.size();
        log.info("{}", reservations);

        Assertions.assertEquals(0, reservationsSize);

        createThreeReservations();
        reservations = reservationService.findAllReservations();
        log.info("{}", reservations);
        reservationsSize = reservations.size();
        Assertions.assertEquals(3, reservationsSize);
    }

    @Test
    public void findAllGuestReservationsShouldReturnList() {
        List<Reservation> reservations = reservationService.findAllReservationsForUser("tester");
        int reservationsSize = reservations.size();
        log.info("{}", reservations);

        Assertions.assertEquals(0, reservationsSize);

        createThreeReservations();
        reservations = reservationService.findAllReservationsForUser("tester");
        log.info("{}", reservations);
        reservationsSize = reservations.size();
        Assertions.assertEquals(3, reservationsSize);
    }

    //cant do save reservation to db

    //User and room setup methods
    private void saveTestUser_1() {
        HotelUser user = new HotelUser(
                1,
                "tester",
                "password",
                "email",
                "ROLE_GUEST",
                "test",
                "user",
                "address",
                "city",
                "state",
                "99999",
                "1234567890");

        hotelUserRepository.save(user);
    }

    private void saveTestRoom_1() {
        Room room = new Room(
                1,
                100,
                "Twin",
                1,
                "comfort",
                "smoking",
                100);

        roomRepository.save(room);
    }

    private void saveTestRoom_2() {
        Room room = new Room(
                2,
                101,
                "King",
                1,
                "comfort",
                "smoking",
                200);

        roomRepository.save(room);
    }

    private void saveTestRoom_3() {
        Room room = new Room(
                3,
                102,
                "Queen",
                1,
                "comfort",
                "smoking",
                150);

        roomRepository.save(room);
    }

    private void createThreeReservations() {
        Reservation reservation1 = reservationService.createNewReservation(
                "100",
                "tester",
                "2023-04-13",
                "2023-04-15"
        );
        Reservation reservation2 = reservationService.createNewReservation(
                "101",
                "tester",
                "2023-04-16",
                "2023-04-17"
        );
        Reservation reservation3 = reservationService.createNewReservation(
                "102",
                "tester",
                "2023-04-18",
                "2023-04-20"
        );
        reservationService.saveReservation(reservation1);
        reservationService.saveReservation(reservation2);
        reservationService.saveReservation(reservation3);
    }
}
