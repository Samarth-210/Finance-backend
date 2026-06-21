package org.example.financeapp.Entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Data
@Entity
@Table(name="notifications")
public class PaymentNotifications {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    private String message;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="paymentId")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})// Prevents Jackson JSON serialization from crashing on Hibernate lazy-loading proxy wrappers
    private Payment payment;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="userId")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})// Prevents Jackson JSON serialization from crashing on Hibernate lazy-loading proxy wrappers
    private User user;
}
