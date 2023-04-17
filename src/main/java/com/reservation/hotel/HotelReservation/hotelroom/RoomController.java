package com.reservation.hotel.HotelReservation.hotelroom;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@Controller
@RequestMapping("/rooms")
public class RoomController {
    @Resource
    private RoomRepository roomRepository;

    @Resource
    private RoomService roomService;

    @GetMapping("")
    public String viewRooms(Model model, @ModelAttribute Room newRoom){
        model.addAttribute("newRoom", newRoom);
        List<Room> allRooms = roomRepository.findAll();
        model.addAttribute("allRooms", allRooms);
        return "view-rooms";
    }

    @PostMapping("/addNew")
    public String addNewRoom(@ModelAttribute("newRoom") Room newRoom){
        if(roomService.addNewRoom(newRoom)){
            log.info("Room Added: {}", newRoom);
        } else {
            log.error("Unable to add this room, Room Number {} already exists!", newRoom.getRoomNumber());
            return "redirect:/rooms?error=";
        }
        return "redirect:/rooms?success=" + newRoom.getRoomNumber();
    }

    @GetMapping("/{roomNumber}")
    public String viewRoomDetails(@PathVariable String roomNumber, Model model){
        int roomNumberInt = Integer.parseInt(roomNumber);
        Optional<Room> showRoom = Optional.ofNullable(roomRepository.findRoomByRoomNumber(roomNumberInt));

        if (showRoom.isPresent()) {
            model.addAttribute("room", showRoom.get());
            return "view-room-details";
        } else {
            return "redirect:/rooms?notFound";
        }
    }

    @GetMapping("/{roomNumber}/edit")
    public String editRoomDetails(@PathVariable String roomNumber, Model model){
        int roomNumberInt = Integer.parseInt(roomNumber);
        Room showRoom = roomRepository.findRoomByRoomNumber(roomNumberInt);
        model.addAttribute("room", showRoom);
        return "edit-room-details";
    }

    @PostMapping("/{roomNumber}/save-edits")
    public String postEditDetails(@PathVariable String roomNumber, @ModelAttribute Room room){
        int roomNumberInt = Integer.parseInt(roomNumber);
        Room roomToUpdate = roomRepository.findRoomByRoomNumber(roomNumberInt);
        log.info("Passed room info: {}",room);
        log.info("Retrieved room info: {}", roomToUpdate);
        boolean isSameRoom = roomNumberInt == room.getRoomNumber();
        log.info("Same Room check: {}", isSameRoom);

        if(roomRepository.findRoomByRoomNumber(room.getRoomNumber()) != null && !isSameRoom) {
            log.error("Room number {} already exists", room.getRoomNumber());
            return "redirect:/rooms/" + roomNumber + "/edit?error=";
        } else {
            roomToUpdate.setRoomNumber(room.getRoomNumber());
            roomToUpdate.setQuality(room.getQuality());
            roomToUpdate.setBedType(room.getBedType());
            roomToUpdate.setBedNumber(room.getBedNumber());
            roomToUpdate.setSmokingStatus(room.getSmokingStatus());
            roomRepository.save(roomToUpdate);
        }

        log.info("Updated room info: {}", roomToUpdate);
        String newRoomNumber = Integer.toString(roomToUpdate.getRoomNumber());
        return "redirect:/rooms/" + newRoomNumber;
    }

    @PostMapping("/searchAvailableRooms")
    public String searchAvailableRooms(@ModelAttribute SearchCriteria searchCriteria, Model model) {

        if((searchCriteria.getCheckOutDate().isEqual(searchCriteria.getCheckInDate()) ||
                searchCriteria.getCheckOutDate().isBefore(searchCriteria.getCheckInDate()))
                && searchCriteria.getSourceForm().equalsIgnoreCase("indexPage")) {
            return "redirect:/?error=";
        }

        if((searchCriteria.getCheckOutDate().isEqual(searchCriteria.getCheckInDate()) ||
                searchCriteria.getCheckOutDate().isBefore(searchCriteria.getCheckInDate()))
                && searchCriteria.getSourceForm().equalsIgnoreCase("reservationsPage")) {
            return "redirect:/reservation/view?error=";
        }

        List<Room> filteredRooms = roomService.findRoomsMatchingSearchCriteria(searchCriteria);

        model.addAttribute("filteredRooms", filteredRooms);
        model.addAttribute("searchCriteria", searchCriteria);
        return "view-available-rooms";
    }
}
