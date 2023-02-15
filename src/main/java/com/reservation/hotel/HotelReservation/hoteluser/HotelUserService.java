package com.reservation.hotel.HotelReservation.hoteluser;

import com.reservation.hotel.HotelReservation.hoteluser.HotelUser;
import com.reservation.hotel.HotelReservation.hoteluser.HotelUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class HotelUserService implements UserDetailsService {

    @Autowired
    private HotelUserRepository hotelUserRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        final HotelUser hotelUser = hotelUserRepository.findByUsername(username);
        if(hotelUser == null){
            throw new UsernameNotFoundException(username);
        }

        UserDetails userDetails = org.springframework.security.core.userdetails.User.withUsername(hotelUser.getUsername())
                .password(hotelUser.getPassword())
                .authorities(hotelUser.getRole())
                .build();
        log.info("{}", hotelUser);

        return userDetails;
    }

    public Boolean registerNewHotelUser(HotelUser newHotelUser) {
        if (!isExistingUser(newHotelUser)) {
            hotelUserRepository.save(newHotelUser);
            return true;
        } else {
            return false;
        }
    }

    private boolean isExistingUser(HotelUser newHotelUser) {
        List<HotelUser> allExistingUsers = hotelUserRepository.findAll();
        for (HotelUser user:allExistingUsers) {
            if(user.getUsername().equals(newHotelUser.getUsername()) || user.getEmail().equals(newHotelUser.getEmail())){
                log.info("username: {} and/or Email: {} already exists", newHotelUser.getUsername(), newHotelUser.getEmail());
                return true;
            }
        }
        return false;
    }
}
