package org.example.financeapp.Repositories;

import org.example.financeapp.Entity.Payment;
import org.example.financeapp.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    public Payment findByPaymentId(Long paymentId);
    public List<Payment> findByDeadlineBetweenAndCompletedFalse(Date start,Date end);
    public List<Payment> findByUser(User user);

}
