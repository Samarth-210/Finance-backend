package org.example.financeapp.Entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.YearMonth;

@Data
@Entity
@Table(name="summary")
public class Summary {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    private YearMonth month;
    private double totalSpent;
    private double totalEarned;
    private double totalSavings;
    private double monthlySpent;
    private double monthlyEarned;
    private double monthlySavings;
    private String mostSpentCategory;
    private String mostEarnedCategory;
    private String mostSavingsCategory;
    private int goalsAchieved;
    private int limitCrossed;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="user_id")
    private User user;
}
