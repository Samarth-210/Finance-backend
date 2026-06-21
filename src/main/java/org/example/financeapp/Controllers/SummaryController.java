package org.example.financeapp.Controllers;

import org.example.financeapp.Entity.Summary;
import org.example.financeapp.Entity.User;
import org.example.financeapp.Services.SummaryService;
import org.example.financeapp.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.YearMonth;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/summary")
public class SummaryController {
    @Autowired
    SummaryService summaryService;
    @Autowired
    UserService userService;

    @GetMapping("/get/{month}/{year}")
    public ResponseEntity<?> getSummary(@PathVariable("month") int month, @PathVariable("year") int year){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username=authentication.getName();
        User user=userService.findByUsername(username);
        YearMonth yearMonth=YearMonth.of(month,year);
        Summary summary=summaryService.getMonthlySummary(user.getId(),yearMonth);
        Map<String,Summary> map=new HashMap<>();
        map.put("summary",summary);
        return ResponseEntity.ok(map);
    }
}
