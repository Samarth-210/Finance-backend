package org.example.financeapp.Controllers;

import org.example.financeapp.Entity.PaymentNotifications;
import org.example.financeapp.Entity.User;
import org.example.financeapp.Repositories.NotificationRepository;
import org.example.financeapp.Services.NotificationService;
import org.example.financeapp.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Component
@RestController
@RequestMapping("/notify")
public class NotificationController {
    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UserService userService;

    @GetMapping("/get")
    public List<PaymentNotifications> getPaymentNotifications() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String name=authentication.getName();
        User user=userService.findByUsername(name);
        return notificationService.findByUser(user);

    }

    @DeleteMapping("/delete/{id}")
    public String deletePaymentNotifications(@PathVariable long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String name=authentication.getName();
        User user=userService.findByUsername(name);
        PaymentNotifications notification=notificationService.findById(id);
        if(notification==null){
            return "No notification found";
        }
        if(!(notification.getUser().getId()==user.getId())){
            return "Unauthorized access";
        }
        notificationService.deleteById(id);
        return "Deleted successfully";
    }
}
