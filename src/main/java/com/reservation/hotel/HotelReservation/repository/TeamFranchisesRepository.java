package com.reservation.hotel.HotelReservation.repository;

import com.reservation.hotel.HotelReservation.model.TeamsFranchise;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamFranchisesRepository extends JpaRepository<TeamsFranchise, String> {
}
