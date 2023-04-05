package com.reservation.hotel.HotelReservation.hoteluser;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HotelUserRepository extends JpaRepository<HotelUser, Integer> {
    HotelUser findByUsername(String username);
    Optional<HotelUser> findHotelUserByUsername(String username);
    Optional<HotelUser> findHotelUserByEmail(String email);

    @Query(value = "SELECT MAX(id) FROM hoteluser", nativeQuery = true)
    int findMaxID();
}
