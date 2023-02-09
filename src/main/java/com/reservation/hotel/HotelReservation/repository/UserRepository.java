package com.reservation.hotel.HotelReservation.repository;

import com.reservation.hotel.HotelReservation.model.HotelUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<HotelUser, Integer> {
    HotelUser findByUsername(String username);
}
