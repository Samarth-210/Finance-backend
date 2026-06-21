package org.example.financeapp.Controllers;

import org.example.financeapp.Entity.Payment;
import org.example.financeapp.Entity.User;
import org.example.financeapp.Services.PaymentService;
import org.example.financeapp.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.Map;

@Component
@RestController
@RequestMapping("/payments")
public class PaymentController {
    @Autowired
    private PaymentService paymentService;

    @Autowired
    private UserService userService;

    @GetMapping("/find")
    public List<Payment> getPayments() {
       Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
       String name=authentication.getName();
       User user=userService.findByUsername(name);
       return paymentService.findByUser(user);
    }

    @PostMapping("/store")
    public ResponseEntity<?> save(@RequestBody Payment payment) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String  name=authentication.getName();
        User user=userService.findByUsername(name);

        payment.setUser(user);
        paymentService.save(payment);
        return ResponseEntity.ok(Map.of("message","Saved Successfully"));
    }

    @PutMapping("/change/{id}")
    public ResponseEntity<?> update(@RequestBody Payment payment, @PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String name=authentication.getName();
        User user=userService.findByUsername(name);
        Payment oldPayment=paymentService.findById(id);

        if(!(user.getId()==oldPayment.getUser().getId()))
            return ResponseEntity.badRequest().build();
        oldPayment.setDeadline(payment.getDeadline());
        oldPayment.setCompleted(payment.isCompleted()); // This marks it cleared/completed
        oldPayment.setAmountPayable(payment.getAmountPayable());
        oldPayment.setEnterprise(payment.getEnterprise());
        paymentService.save(oldPayment);
        return ResponseEntity.ok(Map.of("message","Saved Successfully"));

    }

    @DeleteMapping("/erase/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String name=authentication.getName();
        User user = userService.findByUsername(name);
        if(user==null)
            return new ResponseEntity<>(Map.of("message","User Not Found"), HttpStatus.NOT_FOUND);
        Payment payment=paymentService.findById(id);
        if(payment==null)
            return new ResponseEntity<>(Map.of("message","Payment Not Found"), HttpStatus.NOT_FOUND);
        if(!(user.getId()==payment.getUser().getId()))
            return new ResponseEntity<>(Map.of("message","Unauthorized Access"), HttpStatus.BAD_REQUEST);
        paymentService.deleteById(id);
        return ResponseEntity.ok(Map.of("message","Deleted Successfully"));
    }
}
