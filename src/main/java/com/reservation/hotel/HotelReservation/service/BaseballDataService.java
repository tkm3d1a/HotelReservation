package com.reservation.hotel.HotelReservation.service;

import com.reservation.hotel.HotelReservation.model.TeamsFranchise;
import com.reservation.hotel.HotelReservation.repository.TeamFranchisesRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class BaseballDataService {

    @Autowired
    private TeamFranchisesRepository teamFranchisesRepository;

    public List<TeamsFranchise> listAllTeamsFranchises(){
        return teamFranchisesRepository.findAll();
    }

    public TeamsFranchise getTeamByFranchID(String franchID) {
        return teamFranchisesRepository.findById(franchID).get();
    }
}
