package com.reservation.hotel.HotelReservation.hoteluser;

import com.reservation.hotel.HotelReservation.util.ValidationUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class HotelUserService implements UserDetailsService {

    @Resource
    private HotelUserRepository hotelUserRepository;

    @Resource
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
            try {
                hotelUserRepository.save(newHotelUser);
                return true;
            } catch (Exception e) {
                return false;
            }
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
        boolean isUserNameDuplicate = validationUtil.checkIfUserNameAlreadyExistsInDB(userName.toString());
        boolean isEmailDuplicate = validationUtil.checkIfEmailAlreadyExistsInDB(email);

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

    public HotelUser findUserByUsername(String username){
        Optional<HotelUser> foundUserOpt = hotelUserRepository.findHotelUserByUsername(username);
        HotelUser foundUser = new HotelUser();
        if(foundUserOpt.isPresent()){
            foundUser = foundUserOpt.get();
        }
        return foundUser;
    }

    public List<HotelUser> findAllGuestUsers(){
        List<HotelUser> hotelGuestList = hotelUserRepository.findAll();
        hotelGuestList.removeIf(hotelUserLoop -> !hotelUserLoop.getRole().equals("ROLE_GUEST"));

        return hotelGuestList;
    }
}
