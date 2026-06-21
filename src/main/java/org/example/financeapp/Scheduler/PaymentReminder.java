package org.example.financeapp.Scheduler;

import org.example.financeapp.Entity.Payment;
import org.example.financeapp.Entity.PaymentNotifications;
import org.example.financeapp.Repositories.NotificationRepository;
import org.example.financeapp.Repositories.PaymentRepository;
import org.example.financeapp.Services.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Component
public class PaymentReminder {
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private NotificationService notificationService;

    @Scheduled(cron="0 0 1 * * *")
    @Transactional
    public String paymentReminder(){
        System.out.println("Notifications are being processed");
        String notification="";


        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY,0);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);
        calendar.set(Calendar.MILLISECOND,0);
       Date today=calendar.getTime();

        Calendar endCal = Calendar.getInstance();
        endCal.setTime(today);
        endCal.add(Calendar.DAY_OF_YEAR, 5);
        endCal.set(Calendar.HOUR_OF_DAY, 23);
        endCal.set(Calendar.MINUTE, 59);
        endCal.set(Calendar.SECOND, 59);
        Date fiveDaysEnd = endCal.getTime();

        List<Payment> pendingPayments=paymentRepository.findByDeadlineBetweenAndCompletedFalse(today,fiveDaysEnd);

            for (Payment payment : pendingPayments) {
                PaymentNotifications pay=new PaymentNotifications();
                pay.setPayment(payment);
                if(payment.getUser()!=null)
                    pay.setUser(payment.getUser());
                pay.setCreatedAt(new Date());
                
                pay.setMessage(String.format(
                        "Reminder: Your payment of %d for %s is due on %s.",
                        payment.getAmountPayable(),
                        payment.getEnterprise(),
                        payment.getDeadline().toString()
                ));
                notificationService.save(pay);

                paymentRepository.save(payment);
            }


        return notification;
    }
}
