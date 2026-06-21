package org.example.financeapp.Controllers;

import org.example.financeapp.Entity.EarningsEntity;
import org.example.financeapp.Entity.ExpenseEntity;
import org.example.financeapp.Entity.User;
import org.example.financeapp.Services.AIService;
import org.example.financeapp.Services.EarningsService;
import org.example.financeapp.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/Earnings")
public class EarningsController {
    @Autowired
    private EarningsService earningsService;

    @Autowired
    private UserService userService;

    @Autowired
    private AIService aiService;

    @GetMapping("/all")
    public List<EarningsEntity> findAllEarnings() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.findByUsername(username);
        if (user == null) {
            return Collections.emptyList();
        }
        return earningsService.findByUser(user);
    }

    @PostMapping("/post")
    public String saveEarnings(@RequestBody EarningsEntity earningsEntity) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.findByUsername(username);
        if (user != null) {
            earningsEntity.setUser(user);
        } else
            return "User not logged in";
        String unresolved = earningsEntity.getCategory();
        String resolved = aiService.classifyCategory(unresolved);
        earningsEntity.setCategory(resolved);
        earningsService.saveEarnings(earningsEntity);
        return "Saved Successfully";
    }

    @DeleteMapping("/delete/{id}")
    public String deleteExpenseById(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.findByUsername(username);
        if (user == null)
            return "User not logged in";
        EarningsEntity earnings = earningsService.findById(id);
        if (earnings == null)
            return "Earnings not found";
        if (!(earnings.getUser().getId() == (user.getId())))
            return "Unauthorized access";
        earningsService.deleteById(id);
        return "Deleted Successfully";
    }

    @PutMapping("/update/{id}")
    public String updateExpenseById(@RequestBody EarningsEntity earningsEntity, @PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.findByUsername(username);
        if (user != null) {
            earningsEntity.setUser(user);
        } else
            return "User not logged in";

        EarningsEntity prevEarnings = earningsService.findById(id);
        if (!(prevEarnings.getUser().getId() == (user.getId()))) {
            return "Unauthorized access";
        }
        String unresolved = earningsEntity.getCategory();
        String resolved = aiService.classifyCategory(unresolved);
        prevEarnings.setCategory(resolved);

        prevEarnings.setAmount(earningsEntity.getAmount());
        prevEarnings.setDate(earningsEntity.getDate());
        earningsService.saveEarnings(prevEarnings);
        return "Updated Successfully";

    }

    @GetMapping("/analytics")
    public ResponseEntity<Map<String, Object>> earningsSummary(
            @RequestParam(value = "month", required = false) Integer month,
            @RequestParam(value = "year", required = false) Integer year) {


        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.findByUsername(username);
        Long userId = user.getId();


        java.time.LocalDate now = java.time.LocalDate.now();
        int targetMonth = (month != null) ? month : now.getMonthValue();
        int targetYear = (year != null) ? year : now.getYear();


        List<Object[]> rawChartData = earningsService.getMonthlyEarningsGroupedByCategory(userId,targetMonth,targetYear);
        List<Map<String, Object>> formattedChartData = new ArrayList<>();

        for (Object[] row : rawChartData) {
            Map<String, Object> dataPoint = new HashMap<>();
            dataPoint.put("category", row[0] != null ? row[0].toString() : "Uncategorized");
            dataPoint.put("value", row[1]);
            formattedChartData.add(dataPoint);
        }


        double monthlyEarnings = earningsService.getMonthlyEarnings(userId, targetMonth, targetYear);
        double totalEarned = earningsService.getTotalEarned(userId, targetMonth, targetYear);
        String mostEarnedCategory = earningsService.getMostEarnedCategory(userId, targetMonth, targetYear);
        String mostSavingsCategory = earningsService.getMostSavingsCategory(userId, targetMonth, targetYear);


        Map<String, Object> summaryPayload = new HashMap<>();
        summaryPayload.put("chartData", formattedChartData);
        summaryPayload.put("monthlyEarnings", monthlyEarnings);
        summaryPayload.put("totalEarnedToDate", totalEarned);
        summaryPayload.put("mostEarnedCategory", mostEarnedCategory != null ? mostEarnedCategory : "None");
        summaryPayload.put("mostSavingsCategory", mostSavingsCategory != null ? mostSavingsCategory : "None");

        return ResponseEntity.ok(summaryPayload);
    }
}

