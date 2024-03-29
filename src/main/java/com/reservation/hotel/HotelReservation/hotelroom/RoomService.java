package com.reservation.hotel.HotelReservation.hotelroom;

import com.reservation.hotel.HotelReservation.reservation.Reservation;
import com.reservation.hotel.HotelReservation.reservation.ReservationRepository;
import com.reservation.hotel.HotelReservation.util.ValidationUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RoomService {
    @Resource
    private RoomRepository roomRepository;

    @Resource
    private ReservationRepository reservationRepository;

    @Resource
    ValidationUtil validationUtil;

    public boolean addNewRoom(Room newRoom) {
        log.info("Clicked 'addNewRoom'");
        if (!validationUtil.checkIfRoomNumberAlreadyExistsInDB(newRoom.getRoomNumber())) {
            log.info("Room to be added: {}", newRoom);
            int numBeds = newRoom.getBedNumber();

            int qualityRate;
            if(newRoom.getQuality().equalsIgnoreCase("Executive")){
                qualityRate = 5;
            } else if(newRoom.getQuality().equalsIgnoreCase("Business")) {
                qualityRate = 4;
            } else if(newRoom.getQuality().equalsIgnoreCase("Comfort")) {
                qualityRate = 3;
            } else if (newRoom.getQuality().equalsIgnoreCase("Economy")) {
                qualityRate = 2;
            } else {
                qualityRate = 1;
            }

            int bedRate;
            if(newRoom.getBedType().equalsIgnoreCase("King")){
                bedRate = 4;
            } else if(newRoom.getBedType().equalsIgnoreCase("Queen")){
                bedRate = 3;
            } else if (newRoom.getBedType().equalsIgnoreCase("Twin")) {
                bedRate = 2;
            } else {
                bedRate = 1;
            }

            int smokingRate;
            if(newRoom.getSmokingStatus().equalsIgnoreCase("Smoking")) {
                smokingRate = 2;
            } else {
                smokingRate = 1;
            }

            int roomRate = 10;
            roomRate *= bedRate;
            roomRate *= numBeds;
            roomRate *= qualityRate;
            roomRate *= smokingRate;

            newRoom.setBaseRate(roomRate);
            roomRepository.save(newRoom);
            log.info("Room {} added", newRoom.getRoomNumber());
            return true;
        } else {
            return false;
        }
    }

    public Room findRoomByID(int searchID){
        Optional<Room> foundRoomOpt = roomRepository.findById(searchID);
        Room foundRoom = new Room();
        if(foundRoomOpt.isPresent()){
            foundRoom = foundRoomOpt.get();
        }
        return foundRoom;
    }

    public List<Room> findRoomsMatchingSearchCriteria(SearchCriteria searchCriteria){
        List<Room> filteredRooms = roomRepository.findAll();

        if(!searchCriteria.isBusiness() && !searchCriteria.isExecutive() && !searchCriteria.isEconomy() && !searchCriteria.isComfort()) {
            log.info("Room Quality level not selected by user. Quality Filters will not be applied");
        } else {
            if(!searchCriteria.isBusiness()){
                filteredRooms = filteredRooms.stream()
                        .filter(room -> !room.getQuality().equalsIgnoreCase("Business"))
                        .collect(Collectors.toList());
            }

            if(!searchCriteria.isExecutive()) {
                filteredRooms = filteredRooms.stream()
                        .filter(room -> !room.getQuality().equalsIgnoreCase("Executive"))
                        .collect(Collectors.toList());
            }

            if(!searchCriteria.isComfort()) {
                filteredRooms = filteredRooms.stream()
                        .filter(room -> !room.getQuality().equalsIgnoreCase("Comfort"))
                        .collect(Collectors.toList());
            }

            if(!searchCriteria.isEconomy()) {
                filteredRooms = filteredRooms.stream()
                        .filter(room -> !room.getQuality().equalsIgnoreCase("Economy"))
                        .collect(Collectors.toList());
            }
        }
        //pseudocode for finding all conflicting reservations within the dates in search criteria
        // for each reservation:
        // if search.start is between r.start and r.end, add r to exclusion list
        // if search.end is between r.start and r.end, add r to exclusion list
        // if r.start is equal to or after search.start and r.end is equal to or before search.end, add r to exclusion list
        List<Reservation> conflictingReservations = reservationRepository.findAll().stream()
                .filter(reservation -> (searchCriteria.getCheckInDate().isAfter(reservation.getStartDate()) &&
                                searchCriteria.getCheckInDate().isBefore(reservation.getEndDate()))
                                || (searchCriteria.getCheckOutDate().isAfter(reservation.getStartDate()) &&
                        searchCriteria.getCheckOutDate().isBefore(reservation.getEndDate()))
                        || ((reservation.getStartDate().isAfter(searchCriteria.getCheckInDate()) ||
                        reservation.getStartDate().isEqual(searchCriteria.getCheckInDate())) &&
                        (reservation.getEndDate().isBefore(searchCriteria.getCheckOutDate()) ||
                                reservation.getEndDate().isEqual(searchCriteria.getCheckOutDate()))))
                .toList();


        //get the room ids associated with conflicting reservations
        List<Integer> conflictingRoomIds = conflictingReservations.stream()
                .map(reservation -> reservation.getRoom().getId())
                .toList();

        // filter out rooms with existing reservations on those dates and return available rooms
        filteredRooms = filteredRooms.stream()
                .filter(room -> !conflictingRoomIds.contains(room.getId()))
                .collect(Collectors.toList());

        return filteredRooms;
    }

    public Room findRoomByRoomNumber(String roomNumber) {
        int roomNumberInt = Integer.parseInt(roomNumber);
        return roomRepository.findRoomByRoomNumber(roomNumberInt);
    }
}
