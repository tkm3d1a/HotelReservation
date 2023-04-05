package com.reservation.hotel.HotelReservation.controller;

import com.reservation.hotel.HotelReservation.hoteluser.HotelUserRepository;
import com.reservation.hotel.HotelReservation.hoteluser.HotelUserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = MainController.class)
@ActiveProfiles("test")
class MainControllerMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private HotelUserService hotelUserService;

    @MockBean
    private HotelUserRepository hotelUserRepository;

    @MockBean
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Test
    void registerUserShouldReturn403WhenAnonymousUser() throws Exception {
        mockMvc.perform(post("/register")).andExpect(status().isForbidden());
    }
//    @Test
//    @WithMockUser(roles="GUEST", username = "testuser")
//    void registerUserTest() throws Exception {
//        mockMvc.perform(post("/register")).andExpect(status().isForbidden());
//    }
}