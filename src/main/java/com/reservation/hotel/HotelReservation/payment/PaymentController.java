package com.reservation.hotel.HotelReservation.payment;


import com.reservation.hotel.HotelReservation.reservation.Reservation;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@Controller
@RequestMapping("/payment")
public class PaymentController {

    @Resource
    PaymentService paymentService;

    @GetMapping("")
    public String baseMapping(){
        return "payment-base";
    }

    @GetMapping("/view")
    public String viewPaymentInformation(Model model) {
        log.info("In payment view GET");

        Reservation passedReservation = (Reservation) model.asMap().get("reservation");
        Payment payment = paymentService.findPaymentForReservation(passedReservation);
        model.addAttribute("payment", payment);
        log.info("{}", model.asMap().get("reservation"));
        log.info("{}", model.asMap().get("payment"));
        return "payment-base";
    }

    @PostMapping("/view")
    public String viewPaymentPost(@ModelAttribute Reservation reservation, RedirectAttributes redirectAttributes) {
        log.info("In payment view POST");
        log.info("{}", reservation);
        redirectAttributes.addFlashAttribute("reservation", reservation);
        return "redirect:/payment/view";
    }
}
