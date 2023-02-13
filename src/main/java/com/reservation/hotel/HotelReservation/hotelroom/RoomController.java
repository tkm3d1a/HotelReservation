package com.reservation.hotel.HotelReservation.hotelroom;

import com.reservation.hotel.HotelReservation.hoteluser.HotelUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/rooms")
public class RoomController {
    @Autowired
    private RoomRepository roomRepository;

    @GetMapping
    public String viewRooms(Model model, @ModelAttribute Room newRoom){
        model.addAttribute("newRoom", newRoom);
        List<Room> allRooms = roomRepository.findAll();
        model.addAttribute("allRooms", allRooms);
        return "view-rooms";
    }

    @PostMapping("/addNew")
    public String addNewRoom(@ModelAttribute("newRoom") Room newRoom){
        addNewRoomHelper(newRoom);
        return "redirect:/rooms";
    }

    @GetMapping("/{roomNumber}")
    public String viewRoomDetails(@PathVariable String roomNumber, Model model){
        //TODO: need to add check to make sure room exists? getting the following stacktraceerror when manually entering
        //      room numbers that do not exist --
        //          EL1007E: Property or field 'roomNumber' cannot be found on null
        int roomNumberInt = Integer.parseInt(roomNumber);
        Room showRoom = roomRepository.findRoomByRoomNumber(roomNumberInt);
        model.addAttribute("room", showRoom);
        return "room-details";
    }

    private void addNewRoomHelper(Room newRoom){
        log.info("Clicked 'addNewRoom'");
        log.info("Room to be added: {}", newRoom);
        roomRepository.save(newRoom);
        log.info("Room {} added", newRoom.getRoomNumber());
    }
}
