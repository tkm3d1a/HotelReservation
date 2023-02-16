package com.reservation.hotel.HotelReservation.hoteluser;

import com.reservation.hotel.HotelReservation.hoteluser.HotelUser;
import com.reservation.hotel.HotelReservation.hoteluser.HotelUserRepository;
import com.reservation.hotel.HotelReservation.util.ValidationUtil;
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

    @Autowired
    ValidationUtil validationUtil;

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
        if (!validationUtil.checkIfUserNameAndEmailAlreadyExistsInDB(newHotelUser.getUsername(), newHotelUser.getEmail())) {
            hotelUserRepository.save(newHotelUser);
            return true;
        } else {
            return false;
        }
    }
}
