package com.reservation.hotel.HotelReservation.Reservation;


import com.reservation.hotel.HotelReservation.hotelroom.Room;
import com.reservation.hotel.HotelReservation.hotelroom.RoomRepository;
import com.reservation.hotel.HotelReservation.hoteluser.HotelUser;
import com.reservation.hotel.HotelReservation.hoteluser.HotelUserRepository;
import com.reservation.hotel.HotelReservation.reservation.Reservation;
import com.reservation.hotel.HotelReservation.reservation.ReservationService;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;

import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;

@SpringBootTest
@ActiveProfiles("test")
public class TestReservationService {

//    private static Reservation reservation;

    @Resource
    ReservationService reservationService;

    @Resource
    HotelUserRepository hotelUserRepository;

    @Resource
    RoomRepository roomRepository;

//    @BeforeAll
//    public static void setupTests(){
//        reservation = new Reservation();
//        saveTestUser_1();
//        saveTestRoom_1();
//    }

    //add a reservation
    @Test
    @SqlGroup({
            @Sql(value = "classpath:reservationServiceTest.sql", executionPhase = BEFORE_TEST_METHOD)
    })
    public void createNewReservationTest() {
        saveTestRoom_1();
        saveTestUser_1();
        Reservation reservation = reservationService.createNewReservation(
                "100",
                "Tester",
                "2023-04-13",
                "2023-04-15"
                );

        Assertions.assertEquals(2, reservation.getNumDays());
    }

    // add promo code before confirming
//    @Test
//    public void applyPromoBeforeConfirmTest() {
//
//    }
//
//    //add promo code after confirming
//    @Test
//    public void applyPromoAfterConfirmingTest() {
//
//    }

    //cant do save reservation to db

    //User and room setup methods
    private void saveTestUser_1() {
        HotelUser user = new HotelUser(
                1,
                "tester",
                "password",
                "email",
                "HOTEL_GUEST",
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
}
