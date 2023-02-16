package com.reservation.hotel.HotelReservation;


import com.reservation.hotel.HotelReservation.hoteluser.HotelUserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class HotelUserTests{
    @Autowired
    private HotelUserRepository repo;

    @Test
    public void testAddNewAdmin(){
        //TODO: implement testAddNewAdmin
    }

    @Test
    public void testAddNewClerk(){
        //TODO: implement testAddNewClerk
    }

    @Test
    public void testAddNewGuest(){
        //TODO: implement testAddNewGuest
    }

    @Test
    public void testAddNewRoom(){
        //TODO: implement testAddNewRoom
    }
}
