package com.reservation.hotel.HotelReservation.hoteluser;

import com.reservation.hotel.HotelReservation.util.ValidationUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.ui.Model;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
class HotelUserControllerTest {

    @Autowired
    HotelUserRepository hotelUserRepository;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    HotelUserController hotelUserController;

    @Mock
    Model model;


    @Test
    @WithMockUser(roles="GUEST", username = "testUser1")
    void getUserProfileShouldReturnProfileViewPage() {
        HotelUser hotelUser = getTestUser();
        hotelUser.setUsername("testUser1");
        hotelUser.setEmail("testUser1@hotel.com");
        hotelUserRepository.save(hotelUser);

        String template = hotelUserController.getUserProfile(model);

        Assertions.assertEquals("view-profile", template);
    }

    @Test
    @WithMockUser(roles="GUEST", username = "testuser")
    void editProfileShouldReturnEditProfilePage() {
        HotelUser hotelUser = getTestUser();
        hotelUser.setUsername("testUser2");
        hotelUser.setEmail("testUser2@hotel.com");
        hotelUserRepository.save(hotelUser);

        String template = hotelUserController.editProfile(model);

        Assertions.assertEquals("edit-profile", template);
    }

    @Test
    void updateProfileShouldRedirectToProfilePageAfterUpdatingProfile() {
        HotelUser hotelUser = getTestUser();
        hotelUser.setUsername("testUser3");
        hotelUser.setEmail("testUser3@hotel.com");
        hotelUserRepository.save(hotelUser);

        HotelUser userToUpdate = hotelUser;
        userToUpdate.setPhoneNumber("9998887776");
        userToUpdate.setStreetAddress("Updated Streed Blvd");
        userToUpdate.setPassword("");

        String template = hotelUserController.updateProfile(userToUpdate);
        HotelUser updatedHotelUser = hotelUserRepository.findByUsername("testUser3");

        Assertions.assertEquals(updatedHotelUser.getPhoneNumber(), userToUpdate.getPhoneNumber());
        Assertions.assertEquals(updatedHotelUser.getStreetAddress(), userToUpdate.getStreetAddress());
        Assertions.assertEquals("redirect:/profile", template);
    }

    private HotelUser getTestUser() {
        HotelUser testHotelUser = new HotelUser();
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
}