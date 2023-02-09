package com.reservation.hotel.HotelReservation.controller;

import com.reservation.hotel.HotelReservation.model.TeamsFranchise;
import com.reservation.hotel.HotelReservation.service.BaseballDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
public class TeamController {
    private final BaseballDataService baseballDataService;

    public TeamController(BaseballDataService baseballDataService) {
        this.baseballDataService = baseballDataService;
    }

    @GetMapping("/teams")
    public ResponseEntity<List<TeamsFranchise>> getAllTeamFranchises(){
        return ResponseEntity.ok(baseballDataService.listAllTeamsFranchises());
    }

    @GetMapping("/teams/{franchID}")
    public ResponseEntity<TeamsFranchise> getTeamById(@PathVariable String franchID){
        return ResponseEntity.ok(baseballDataService.getTeamByFranchID(franchID));
    }
}
