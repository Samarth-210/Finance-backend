package org.example.financeapp.Entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Entity
@Data
@Table(name="expenses")
public class ExpenseEntity {
    private String category;
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private long id;
    private long amount;
    private Date date;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="user_id")
    private User user;

    private Long expenseLimit;

}
