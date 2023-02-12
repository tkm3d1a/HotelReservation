package com.reservation.hotel.HotelReservation.hoteluser;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HotelUserRepository extends JpaRepository<HotelUser, Integer> {
    HotelUser findByUsername(String username);
}
