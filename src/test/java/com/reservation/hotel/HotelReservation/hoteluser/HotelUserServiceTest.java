package com.reservation.hotel.HotelReservation.hoteluser;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;

import java.util.Optional;

import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;


@SpringBootTest
@ActiveProfiles("test")
class HotelUserServiceTest {

    @Autowired
    HotelUserRepository hotelUserRepository;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    HotelUserService hotelUserService;

    @Test
    @SqlGroup({
            @Sql(value = "classpath:test-init.sql", executionPhase = BEFORE_TEST_METHOD)
    })
    public void findUserByUserNameShouldReturnAUser() {
        HotelUser testHotelUser = getTestUser();

        hotelUserRepository.save(testHotelUser);

        Optional<HotelUser> hotelUser = hotelUserRepository.findHotelUserByUsername("testUser");

        Assertions.assertEquals(testHotelUser.getUsername(), hotelUser.get().getUsername());
        Assertions.assertEquals(testHotelUser.getEmail(), hotelUser.get().getEmail());

    }

    @Test
    @SqlGroup({
            @Sql(value = "classpath:test-init.sql", executionPhase = BEFORE_TEST_METHOD)
    })
    public void registerNewHotelUserShouldReturnTrueIfUserCanBeRegistered() {
        hotelUserRepository.save(getTestUser());

        Assertions.assertFalse(hotelUserService.registerNewHotelUser(getTestUser()));
        Assertions.assertTrue(hotelUserService.registerNewHotelUser(getTestUser2()));
    }

    private HotelUser getTestUser() {
        HotelUser testHotelUser = new HotelUser();
        testHotelUser.setUsername("testUser");
        testHotelUser.setEmail("testUser@Hhotel.com");
        testHotelUser.setRole("ROLE_GUEST");
        testHotelUser.setFirstName("Test");
        testHotelUser.setLastName("User");
        testHotelUser.setStreetAddress("Street Address");
        testHotelUser.setZipCode("88776");
        testHotelUser.setState("TX");
        testHotelUser.setPhoneNumber("1233031223");
        testHotelUser.setPassword(bCryptPasswordEncoder.encode("Test123"));

        return testHotelUser;
    }

    private HotelUser getTestUser2() {
        HotelUser testHotelUser = new HotelUser();
        testHotelUser.setUsername("testUser2");
        testHotelUser.setEmail("testUser2@Hhotel.com");
        testHotelUser.setRole("ROLE_GUEST");
        testHotelUser.setFirstName("Test2");
        testHotelUser.setLastName("User2");
        testHotelUser.setStreetAddress("Street Address");
        testHotelUser.setZipCode("88774");
        testHotelUser.setState("TX");
        testHotelUser.setPhoneNumber("1233031224");
        testHotelUser.setPassword(bCryptPasswordEncoder.encode("Test123"));

        return testHotelUser;
    }

}