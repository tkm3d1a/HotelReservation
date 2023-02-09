package com.reservation.hotel.HotelReservation.repository;

import com.reservation.hotel.HotelReservation.model.HotelUser;
import com.reservation.hotel.HotelReservation.model.TeamsFranchise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamFranchisesRepository extends JpaRepository<TeamsFranchise, String> {
}
