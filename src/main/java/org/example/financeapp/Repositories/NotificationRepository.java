package org.example.financeapp.Repositories;

import org.example.financeapp.Entity.PaymentNotifications;
import org.example.financeapp.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<PaymentNotifications,Long> {
    public List<PaymentNotifications> findByUser(User user);
    public void deleteById(Long id);
}
