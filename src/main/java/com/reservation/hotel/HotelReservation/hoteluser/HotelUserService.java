package com.reservation.hotel.HotelReservation.hoteluser;

import com.reservation.hotel.HotelReservation.util.ValidationUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

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

    public boolean registerNewHotelUser(HotelUser newHotelUser) {
        if (!validationUtil.checkIfUserNameAlreadyExistsInDB(newHotelUser.getUsername())
                || !validationUtil.checkIfEmailAlreadyExistsInDB(newHotelUser.getEmail())) {
            hotelUserRepository.save(newHotelUser);
            return true;
        } else {
            return false;
        }
    }

    public boolean addNewEmployee(HotelUser hotelClerk){
        log.info("Clicked 'addNewEmployee'");
        StringBuilder userName = new StringBuilder(hotelClerk.getFirstName().substring(0,1).toLowerCase());
        if(hotelClerk.getLastName().length() > 7){
            userName.append(hotelClerk.getLastName().substring(0,7).toLowerCase());
        } else {
            userName.append(hotelClerk.getLastName().toLowerCase());
        }
        String email = hotelClerk.getEmail();
        Boolean isUserNameDuplicate = validationUtil.checkIfUserNameAlreadyExistsInDB(userName.toString());
        Boolean isEmailDuplicate = validationUtil.checkIfEmailAlreadyExistsInDB(email);

        if(isUserNameDuplicate){
            userName.append(hotelUserRepository.findMaxID() + 1);
        }

        if(!isEmailDuplicate) {
            hotelClerk.setUsername(userName.toString());
            hotelClerk.setRole("ROLE_CLERK");
            hotelClerk.setZipCode("99999");
            hotelUserRepository.save(hotelClerk);
            return true;
        } else {
            return false;
        }
    }

    public HotelUser findUserByID(int searchID){
        Optional<HotelUser> foundUserOpt = hotelUserRepository.findById(searchID);
        HotelUser foundUser = new HotelUser();
        if(foundUserOpt.isPresent()){
            foundUser = foundUserOpt.get();
        }
        return foundUser;
    }
}
