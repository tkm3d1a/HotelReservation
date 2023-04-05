package com.reservation.hotel.HotelReservation.reservation;

import com.reservation.hotel.HotelReservation.hotelroom.Room;
import com.reservation.hotel.HotelReservation.hotelroom.RoomRepository;
import com.reservation.hotel.HotelReservation.hoteluser.HotelUser;
import com.reservation.hotel.HotelReservation.hoteluser.HotelUserRepository;
import com.reservation.hotel.HotelReservation.util.FormEncapsulate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
class ReservationControllerTest {
    @Autowired
    ReservationRepository reservationRepository;

    @Autowired
    RoomRepository roomRepository;

    @Autowired
    HotelUserRepository hotelUserRepository;

    @Autowired
    ReservationController reservationController;

    @Mock
    ReservationService reservationService;

    @Mock
    Model model;

    @Mock
    FormEncapsulate formEncapsulate;

    @Mock
    RedirectAttributes redirectAttributes;

    @BeforeEach
    public void setUp(){
        reservationController = new ReservationController(reservationRepository, roomRepository, reservationService);
        saveTestRoom();
        saveTestUser();
        saveTestReservation();
    }

    @Test
    @WithMockUser(roles="GUEST", username = "testuser")
    void getAllReservationsShouldReturnTestReservationPage() {
        String template = reservationController.getAllReservations(model);

        Assertions.assertEquals("test-reservation", template);
    }

    @Test
    void getGuestReservationsShouldReturnTestReservationPage() {
        String template = reservationController.getGuestReservations(model, 1);

        Assertions.assertEquals("test-reservation", template);
    }

    @Test
    @WithMockUser(roles="GUEST", username = "testUser")
    void editReservationShouldReturnEditReservationPage() {
        when(reservationService.findReservationByGuestIDAndReservationID(anyInt(), anyString())).thenReturn(getTestReservation());
        String template = reservationController.editReservation(model, 1);

        Assertions.assertEquals("edit-reservation", template);
    }

    @Test
    @WithMockUser(roles="GUEST", username = "testUser")
    void reserveRoomShouldSetReservationGuestAndReturnMakeReservationPage() {
        String template = reservationController.reserveRoom("111", LocalDate.now().toString(),
                LocalDate.now().plusDays(2).toString(), model);

        Assertions.assertEquals("make-reservation", template);
    }

    @Test
    void stageReservationShouldSaveReservationAndRedirectToReservationView() {
        String template = reservationController.stageReservation(getTestReservation());
        Optional<Reservation> savedReservation = reservationRepository.findById(1);

        Assertions.assertEquals("redirect:/reservation/view", template);
        Assertions.assertTrue(savedReservation.isPresent());
    }

    @Test
    void confirmReservationViewShouldReturnConfirmReservationPage() {
        String template = reservationController.confirmReservationView(model);

        Assertions.assertEquals("confirm-reservation", template);
    }

    @Test
    @WithMockUser(roles="GUEST", username = "testUser")
    void confirmReservationShouldRedirectToReservationView() {
        saveTestReservation();
        String template = reservationController.confirmReservation(0);

        Assertions.assertEquals("redirect:/reservation/view", template);
    }

    @Test
    void applyPromoShouldUpdateRatesAndRedirectToReservationConfirmation() {
        when(formEncapsulate.getFormString()).thenReturn("1234");
        when(reservationService.findreservationByID(anyInt())).thenReturn(getTestReservation());

        String template = reservationController.applyPromo(formEncapsulate, getTestReservation(), redirectAttributes);

        Assertions.assertEquals("redirect:/reservation/confirm", template);

    }

    @Test
    void cancelReservationShouldRedirectToreservationView() {
        String template = reservationController.cancelReservation(1);

        Assertions.assertEquals("redirect:/reservation/view", template);
    }

    private Reservation getTestReservation() {
        Reservation testReservation =  new Reservation(1,getTestUser(), getTestRoom(), 1, 1, false,
                true, false, LocalDate.now(), LocalDate.now().plusDays(2), 2);

        return testReservation;
    }

    private HotelUser getTestUser() {
        HotelUser testHotelUser = new HotelUser(1, "testUser", "Password",
                "testUser@Hhotel.com", "ROLE_GUEST", "Test", "User",
                "Street Address", "City", "TX", "77868", "1233031223");

        return testHotelUser;
    }

    private void saveTestUser(){
        hotelUserRepository.save(getTestUser());
    }

    private Room getTestRoom(){
        Room testRoom = new Room(1, 111, "King", 2, "Comfort", "Smoking", 60);

        return testRoom;
    }

    private void saveTestRoom(){
        roomRepository.save(getTestRoom());
    }

    private void saveTestReservation(){
        reservationRepository.save(getTestReservation());
    }
}