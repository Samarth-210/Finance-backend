package org.example.financeapp.Scheduler;

import org.example.financeapp.Entity.User;
import org.example.financeapp.Repositories.SummaryRepository;
import org.example.financeapp.Services.SummaryService;
import org.example.financeapp.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.YearMonth;
import java.util.List;

@Component
public class SummaryScheduler {
    @Autowired
    private SummaryService summaryService;

    @Autowired
    private UserService userService;

    @Scheduled(cron="0 0 0 1 * *")
    public void summaryGenerator(){
        YearMonth lastMonth=YearMonth.now().minusMonths(1);
        int targetMonth=lastMonth.getMonthValue();
        int targetYear=lastMonth.getYear();
        List<User> allUsers=userService.findAll();
        for(User user:allUsers){
          try{
           summaryService.calculateAndSaveSummary(user.getId(),targetMonth,targetYear);
          }catch(Exception e){
            System.err.println("Failed to generate summary for month"+targetMonth+"/"+targetYear);
            }
          }
        }
}