package com.reservation.hotel.HotelReservation;


import com.reservation.hotel.HotelReservation.hotelroom.Room;
import com.reservation.hotel.HotelReservation.hotelroom.RoomRepository;
import com.reservation.hotel.HotelReservation.hoteluser.HotelUser;
import com.reservation.hotel.HotelReservation.hoteluser.HotelUserRepository;
import com.reservation.hotel.HotelReservation.reservation.Reservation;
import com.reservation.hotel.HotelReservation.reservation.ReservationService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;

//@ExtendWith(MockitoExtension.class)

public class TestReservationService {

    static Reservation reservation;
    static HotelUser user;
    static Room room;

    ReservationService reservationService;

    @MockBean
    static HotelUserRepository hotelUserRepositoryMock;
    @MockBean
    static RoomRepository roomRepositoryMock;

    @BeforeAll
    public static void setupTests(){
        reservation = new Reservation();
        user = new HotelUser(
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
        room = new Room(
                1,
                100,
                "Twin",
                1,
                "comfort",
                "smoking",
                100);


        hotelUserRepositoryMock.save(user);
        roomRepositoryMock.save(room);
    }

    //add a reservation
    @Test
    public void createNewReservationTest() {
        reservation = reservationService.createNewReservation(
                "100",
                "Tester",
                "2023-04-13",
                "2023-04-15"
                );

        Assertions.assertEquals(2, reservation.getNumDays());
    }

    // add promo code before confirming
    @Test
    public void applyPromoBeforeConfirmTest() {

    }

    //add promo code after confirming
    @Test
    public void applyPromoAfterConfirmingTest() {

    }

    //cant do save reservation to db

    //
}
