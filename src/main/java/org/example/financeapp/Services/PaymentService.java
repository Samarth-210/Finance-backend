package org.example.financeapp.Services;

import org.example.financeapp.Entity.Payment;
import org.example.financeapp.Entity.User;
import org.example.financeapp.Repositories.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentService {
    @Autowired
    private PaymentRepository paymentRepository;

    public List<Payment> findAll(){
        return paymentRepository.findAll();
    }
    public void save(Payment payment){
        paymentRepository.save(payment);
    }

    public Payment findById(Long id){
        return paymentRepository.findByPaymentId(id);
    }
    public void deleteById(Long id){
        paymentRepository.deleteById(id);
    }

    public List<Payment> findByUser(User user){
        return paymentRepository.findByUser(user);
    }

}
