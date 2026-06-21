package org.example.financeapp.Services;

import org.example.financeapp.Entity.ExpenseEntity;
import org.example.financeapp.Entity.Summary;
import org.example.financeapp.Entity.User;
import org.example.financeapp.Repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
public class SummaryService {
    @Autowired
    private ExpenseRepository expenseRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private GoalsRepository goalsRepository;
    @Autowired
    private SummaryRepository summaryRepository;
    @Autowired
    private EarningsRepository earningsRepository;

    public Summary getMonthlySummary(long userId,YearMonth monthStr){
        return summaryRepository.findByUserIdAndMonth(userId,monthStr).orElseThrow(()->new RuntimeException("Summary not found for month"+monthStr));
    }

    public void calculateAndSaveSummary(long userId,int month,int year){
        YearMonth yearMonth=YearMonth.of(year,month);
        String formatted=yearMonth.format(DateTimeFormatter.ofPattern("yyyy-MM"));
        User user =userService.findById(userId);
        Optional<Summary> existingSummary=summaryRepository.findByUserIdAndMonth(userId,yearMonth);
        Summary summary;
        if(existingSummary.isPresent()){
            summary=existingSummary.get();
        }
        else{
            summary=new Summary();
            summary.setUser(user);
            summary.setMonth(yearMonth);
        }
        double monthlyEarned=earningsRepository.getMonthlyEarnings(userId,month,year);
        double monthlySpent=expenseRepository.getMonthlySpent(userId,month,year);
        summary.setMonthlyEarned(monthlyEarned);
        summary.setMonthlySpent(monthlySpent);
        summary.setMonthlySavings(monthlyEarned-monthlySpent);

        double totalEarned= earningsRepository.getTotalEarned(userId,month,year);
        double totalSpent=expenseRepository.getTotalSpent(userId,month,year);
        summary.setTotalEarned(totalEarned);
        summary.setTotalSpent(totalSpent);
        summary.setTotalSavings(totalEarned-totalSpent);
        summary.setMostEarnedCategory(earningsRepository.getMostEarnedCategory(userId,month,year));
        summary.setMostSpentCategory(expenseRepository.getMostSpentCategory(userId,month,year));
        summary.setMostSavingsCategory(earningsRepository.getMostSavingsCategory(userId,month,year));
        summary.setGoalsAchieved(goalsRepository.countGoalsCompleted(userId));
        summary.setLimitCrossed(expenseRepository.countLimitsCrossed(userId,month,year));
        summaryRepository.save(summary);
    }

    public void preFillAllHistoricalSummaries(Long userId) {
        YearMonth startPoint = YearMonth.now().minusMonths(12);
        YearMonth currentPoint = YearMonth.now();

        while (!startPoint.isAfter(currentPoint)) {
            calculateAndSaveSummary(userId, startPoint.getMonthValue(), startPoint.getYear());
            startPoint = startPoint.plusMonths(1);
        }
    }
}

