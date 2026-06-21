package org.example.financeapp.Services;

import org.example.financeapp.Entity.PaymentNotifications;
import org.example.financeapp.Entity.User;
import org.example.financeapp.Repositories.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {
    @Autowired
    private NotificationRepository notificationRepository;

    public List<PaymentNotifications> findByUser(User user){
        List<PaymentNotifications>list= notificationRepository.findByUser(user);

        return list;
    }
    public void save(PaymentNotifications notification){
        notificationRepository.save(notification);
    }
    public void deleteById(Long id){
        notificationRepository.deleteById(id);
    }
    public PaymentNotifications findById(Long id){
        return notificationRepository.findById(id).orElse(null);
    }
}


