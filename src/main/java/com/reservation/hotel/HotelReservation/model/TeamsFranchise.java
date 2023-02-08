package com.reservation.hotel.HotelReservation.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="teamsfranchises")
public class TeamsFranchise {
    @Id
    @Column(name = "franchID")
    private String franchID;
    @Column(name = "franchName")
    private String franchName;
    @Column(name = "active")
    private String isActive;
    @Column(name = "NAassoc")
    private String naAssoc;
}
