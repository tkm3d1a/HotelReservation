package com.reservation.hotel.HotelReservation.controller;

import com.reservation.hotel.HotelReservation.hotelroom.SearchCriteria;
import com.reservation.hotel.HotelReservation.hoteluser.HotelUser;
import com.reservation.hotel.HotelReservation.hoteluser.HotelUserRepository;
import com.reservation.hotel.HotelReservation.hoteluser.HotelUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;

@Slf4j
@Controller
public class MainController {

    @Autowired
    HotelUserRepository hotelUserRepository;

    @Autowired
    HotelUserService hotelUserService;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @GetMapping("/")
    public String getMessage(Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        log.info("{}, Roles: {}", currentPrincipalName, authorities);
        SearchCriteria searchCriteria = new SearchCriteria();
        searchCriteria.setSourceForm("indexPage");
        searchCriteria.setCheckInDate(LocalDate.now());
        searchCriteria.setCheckOutDate(LocalDate.now().plusDays(3));
        model.addAttribute("searchCriteria", searchCriteria);
        model.addAttribute("currentDate", LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        model.addAttribute("minCheckOutDate", LocalDate.now().plusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        return "index";
    }

    @GetMapping("/login")
    public String loginUser(){
        return "login";
    }

    @GetMapping("/register")
    public String register(Model model){
        model.addAttribute("hotelUser", new HotelUser());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute HotelUser hotelUser, RedirectAttributes redirectAttributes){
        hotelUser.setPassword(bCryptPasswordEncoder.encode(hotelUser.getPassword()));
        if (hotelUserService.registerNewHotelUser(hotelUser)){
            log.info("User inserted: {}", hotelUser);
        } else {
            log.error("Unable to register new user, username/email might already exist");
            return "redirect:/register?error=";
        }
        //TODO: Right now, spring-security captures and redirects to login -> update to follow correct redirect path
        //      on new register
        redirectAttributes.addAttribute("username", hotelUser.getUsername());
        return "redirect:/login?success=" + hotelUser.getUsername();
    }

    @GetMapping("/result")
    public String afterSignup(){
        return "result";
    }

    @PostMapping("/result")
    public String resultTest(){
        return "result";
    }

    /*
    Admin related endpoints
     */
    @GetMapping("/admin")
    public String adminDashboard(@ModelAttribute HotelUser hotelUser, Model model){
        model.addAttribute("newEmployee", hotelUser);
        List<HotelUser> hotelEmployeeList = hotelUserRepository.findAll();
        List<HotelUser> hotelGuestList = hotelUserRepository.findAll();
        hotelEmployeeList.removeIf(hotelUserLoop -> hotelUserLoop.getRole().equals("ROLE_GUEST"));
        hotelGuestList.removeIf(hotelUserLoop -> !hotelUserLoop.getRole().equals("ROLE_GUEST"));
        model.addAttribute("employeeList", hotelEmployeeList);
        model.addAttribute("guestList", hotelGuestList);
        return "admin-dashboard";
    }

    @PostMapping("/admin/addNew")
    public String addClerkProfile(@ModelAttribute("user") HotelUser hotelUser){
        hotelUser.setPassword(bCryptPasswordEncoder.encode("default1234"));
        if(hotelUserService.addNewEmployee(hotelUser)){
            log.info("Clerk Added: {}", hotelUser);
        } else {
            log.error("Unable to add new Clerk, email for clerk might already exist");
            return "redirect:/admin?error=";
        }
        return "redirect:/admin?success=" + hotelUser.getUsername();
    }

    @GetMapping("/admin/resetUserPassword/{username}")
    public String resetUserPassword(@PathVariable String username){
        HotelUser user = hotelUserService.findUserByUsername(username);
        user.setPassword(bCryptPasswordEncoder.encode(username));
        hotelUserRepository.save(user);

        return "redirect:/admin?resetok=" + username;
    }

    @GetMapping("/setup/addAdmin")
    public String addAdmin(Model model){
        HotelUser newAdmin = new HotelUser();
        String defaultPassword = "Testpw99";
        newAdmin.setEmail("testadmin@hotel.com");
        newAdmin.setUsername("tadmin");
        newAdmin.setPassword(bCryptPasswordEncoder.encode(defaultPassword));
        newAdmin.setRole("ROLE_ADMIN");
        newAdmin.setZipCode("00000");
        model.addAttribute("newAdmin", newAdmin);
        model.addAttribute("password", defaultPassword);

        hotelUserService.registerNewHotelUser(newAdmin);

        return "result";
    }
}
