package com.reservation.hotel.HotelReservation.hotelroom;

import com.reservation.hotel.HotelReservation.reservation.Reservation;
import com.reservation.hotel.HotelReservation.reservation.ReservationRepository;
import com.reservation.hotel.HotelReservation.util.ValidationUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
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

        Predicate<Room> isBusiness = room -> room.getQuality().equalsIgnoreCase("Business");
        Predicate<Room> isExecutive = room -> room.getQuality().equalsIgnoreCase("Executive");
        Predicate<Room> isComfort = room -> room.getQuality().equalsIgnoreCase("Comfort");
        Predicate<Room> isEconomy = room -> room.getQuality().equalsIgnoreCase("Economy");

        if(!searchCriteria.isBusiness() && !searchCriteria.isExecutive() && !searchCriteria.isEconomy() && !searchCriteria.isComfort()) {
            log.info("Room Quality level not selected by user. Quality Filters will not be applied");
        } else {
            if(!searchCriteria.isBusiness()){
                filteredRooms = filteredRooms.stream()
                        .filter(isComfort.or(isEconomy).or(isExecutive))
                        .collect(Collectors.toList());
            }

            if(!searchCriteria.isExecutive()) {
                filteredRooms = filteredRooms.stream()
                        .filter(isComfort.or(isBusiness).or(isEconomy))
                        .collect(Collectors.toList());
            }

            if(!searchCriteria.isComfort()) {
                filteredRooms = filteredRooms.stream()
                        .filter(isExecutive.or(isBusiness).or(isEconomy))
                        .collect(Collectors.toList());
            }

            if(!searchCriteria.isEconomy()) {
                filteredRooms = filteredRooms.stream()
                        .filter(isExecutive.or(isBusiness).or(isComfort))
                        .collect(Collectors.toList());
            }
        }

        //find all conflicting reservations within the dates in search criteria
        List<Reservation> conflictingReservations = reservationRepository
                .findAllByStartDateBetween(searchCriteria.getCheckInDate(), searchCriteria.getCheckOutDate());
        conflictingReservations.addAll(reservationRepository.
                findAllByEndDateBetween(searchCriteria.getCheckInDate(), searchCriteria.getCheckInDate()));

        //get the room ids associated with conflicting reservations
        List<Integer> conflictingRoomIds = conflictingReservations.stream()
                .map(reservation -> reservation.getRoom().getId())
                .collect(Collectors.toList());

        // filter out rooms with existing reservations on those dates and return available rooms
        filteredRooms = filteredRooms.stream()
                .filter(room -> !conflictingRoomIds.contains(room.getId()))
                .collect(Collectors.toList());

        return filteredRooms;
    }
}
