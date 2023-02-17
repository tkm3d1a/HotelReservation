package com.reservation.hotel.HotelReservation;


import com.reservation.hotel.HotelReservation.hoteluser.HotelUser;
import com.reservation.hotel.HotelReservation.hoteluser.HotelUserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.annotation.Rollback;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class HotelUserTests{
    @Autowired
    private HotelUserRepository repo;

    @Autowired //TODO: Figure out why autowiring to BCrypt is not working.  Returns "bean not valid" error
    BCryptPasswordEncoder pwEncoder;

    @Test
    public void testAddNewAdmin(){
        //TODO: implement testAddNewAdmin
        HotelUser newAdmin = new HotelUser();
        newAdmin.setEmail("testadmin@hotel.com");
        newAdmin.setUsername("tadmin");
        newAdmin.setPassword(pwEncoder.encode("Testpw99"));
        newAdmin.setZipCode("00000");

        repo.save(newAdmin);

        Assertions.assertNotNull(repo.findByUsername("tadmin"));
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
