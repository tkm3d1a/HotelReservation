package com.reservation.hotel.HotelReservation.hotelroom;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
class RoomControllerTest {
    @Autowired
    RoomRepository roomRepository;

    @Autowired
    RoomService roomService;

    @Mock
    Model model;

    @Mock
    RedirectAttributes redirectAttributes;

    @Mock
    SearchCriteria searchCriteria;

    @Autowired
    RoomController roomController;

    @Test
    void viewRoomsShouldReturnViewRoomsPage() {
        Room testRoom = getTestRoom();

        String template = roomController.viewRooms(model, testRoom);

        Assertions.assertEquals("view-rooms", template);
    }

    @Test
    void addNewRoomShouldInsertARoomAndRedirectToRoomsPage() {
        Room testRoom = getTestRoom();
        testRoom.setRoomNumber(101);

        String template = roomController.addNewRoom(testRoom);
        Room addedRoom = roomRepository.findRoomByRoomNumber(101);

        Assertions.assertNotNull(addedRoom);
        Assertions.assertEquals("redirect:/rooms?success=" + testRoom.getRoomNumber(), template);
    }

    @Test
    void viewRoomDetails() {
        Room testRoom = getTestRoom();
        testRoom.setRoomNumber(102);
        roomController.addNewRoom(testRoom);

        String template = roomController.viewRoomDetails("102", model);

        Assertions.assertEquals("view-room-details", template);
    }

    @Test
    void editRoomDetailsShouldReturnEditRoomDetailsPage() {
        Room testRoom = getTestRoom();
        testRoom.setRoomNumber(103);
        roomController.addNewRoom(testRoom);

        String template  = roomController.editRoomDetails("103", model);

        Assertions.assertEquals("edit-room-details", template);
    }

    @Test
    void postEditDetailsShouldUpdateRoomDetailsAndRedirectToRoomsPage() {
        Room testRoom = getTestRoom();
        testRoom.setRoomNumber(104);
        roomController.addNewRoom(testRoom);

        Room updatedRoomDetails = getTestRoom();
        updatedRoomDetails.setRoomNumber(104);
        updatedRoomDetails.setSmokingStatus("Non-Smoking");
        updatedRoomDetails.setQuality("Executive");

        String template = roomController.postEditDetails("104", updatedRoomDetails);
        Room updatedRoom = roomRepository.findRoomByRoomNumber(104);

        Assertions.assertEquals(updatedRoomDetails.getSmokingStatus(), updatedRoom.getSmokingStatus());
        Assertions.assertEquals(updatedRoomDetails.getQuality(), updatedRoom.getQuality());
        Assertions.assertEquals("redirect:/rooms/104" , template );
    }

    @Test
    void searchAvailableRoomsShouldReturnViewAvailableRoomsPage() {
        when(searchCriteria.getCheckInDate()).thenReturn(LocalDate.now());
        when(searchCriteria.getCheckOutDate()).thenReturn(LocalDate.now().plusDays(3));

        String template = roomController.searchAvailableRooms(searchCriteria, model);

        Assertions.assertEquals("view-available-rooms", template);
    }

    @Test
    void searchAvailableRoomsShouldRedirectToErrorPageWhenDatesAreInvalid() {
        when(searchCriteria.getCheckInDate()).thenReturn(LocalDate.now());
        when(searchCriteria.getCheckOutDate()).thenReturn(LocalDate.now().minusDays(3));
        when(searchCriteria.getSourceForm()).thenReturn("indexPage");

        String template = roomController.searchAvailableRooms(searchCriteria, model);

        Assertions.assertEquals("redirect:/?error=", template);
    }

    private Room getTestRoom(){
        Room testRoom = new Room();
        testRoom.setRoomNumber(1);
        testRoom.setQuality("Comfort");
        testRoom.setBedType("King");
        testRoom.setBedNumber(2);
        testRoom.setBaseRate(60);
        testRoom.setSmokingStatus("Smoking");

        return testRoom;
    }
}