package com.reservation.hotel.HotelReservation.hoteluser;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class HotelUser{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(
            unique = true,
            nullable = false,
            length = 40
    )
    private String username;

    @Column(
            nullable = false
    )
    @ToString.Exclude private String password;

    @Column(
            unique = true,
            nullable = false
    )
    private String email;
    private String role = "ROLE_GUEST";
    private String firstName;
    private String lastName;
    private String streetAddress;
    private String city;
    private String state;

    @Column(
            nullable = false,
            length = 5
    )
    private String zipCode;
    private String phoneNumber;
}
