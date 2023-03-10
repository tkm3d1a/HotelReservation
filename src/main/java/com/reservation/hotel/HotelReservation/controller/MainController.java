package com.reservation.hotel.HotelReservation.controller;

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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    public String getMessage(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        log.info("{}, Roles: {}", currentPrincipalName, authorities);
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
        List<HotelUser> hotelUserList = hotelUserRepository.findAll();
        hotelUserList.removeIf(hotelUserLoop -> hotelUserLoop.getRole().equals("ROLE_GUEST"));
        model.addAttribute("employeeList", hotelUserList);
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
