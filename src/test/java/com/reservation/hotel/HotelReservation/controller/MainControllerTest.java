package com.reservation.hotel.HotelReservation.controller;

import com.reservation.hotel.HotelReservation.hoteluser.HotelUser;
import com.reservation.hotel.HotelReservation.hoteluser.HotelUserRepository;
import com.reservation.hotel.HotelReservation.hoteluser.HotelUserService;
import org.jboss.jandex.Main;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class MainControllerTest {
    @Autowired
    HotelUserRepository hotelUserRepository;

    @Autowired
    HotelUserService hotelUserService;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    MainController mainController;

    @Mock
    Model model;

    @Mock
    RedirectAttributes redirectAttributes;

    @Test
    @WithMockUser(roles="GUEST", username = "testuser")
    void getMessageShouldReturnIndexPage() {
        String template = mainController.getMessage(model);

        Assertions.assertEquals("index", template);
    }

    @Test
    @WithMockUser(roles="GUEST", username = "testuser")
    void loginUserShouldReturnLoginPage() {
        String template = mainController.loginUser();

        Assertions.assertEquals("login", template);
    }

    @Test
    void registerShouldReturnRegisterPage() {
        String template = mainController.register(model);

        Assertions.assertEquals("register", template);
    }

    @Test
    void registerShouldInsertUserAndRedirectToLoginPage() {
        HotelUser testHotelUser = getTestUser();

        String template = mainController.registerUser(testHotelUser, redirectAttributes);
        Optional<HotelUser> hotelUser = hotelUserRepository.findHotelUserByUsername("testUser");

        Assertions.assertEquals(testHotelUser.getUsername(), hotelUser.get().getUsername());
        Assertions.assertEquals(testHotelUser.getEmail(), hotelUser.get().getEmail());
        Assertions.assertEquals("redirect:/login?success=testUser", template);
    }

    @Test
    void addClerkProfileShouldInsertClerkUserAndRedirectToAdminDashboard() {
        HotelUser testHotelUser = getTestUser();
        testHotelUser.setUsername("clerkUser");
        testHotelUser.setEmail("clerktestuser@hotel.com");

        String template = mainController.addClerkProfile(testHotelUser);
        Optional<HotelUser> hotelClerk = hotelUserRepository.findHotelUserByEmail("clerktestuser@hotel.com");

        Assertions.assertEquals(testHotelUser.getEmail(), hotelClerk.get().getEmail());
        Assertions.assertEquals("ROLE_CLERK", hotelClerk.get().getRole());
        Assertions.assertEquals("redirect:/admin?success=" + hotelClerk.get().getUsername(), template);
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

}