package org.example.financeapp.Controllers;

import lombok.extern.slf4j.Slf4j;
import org.example.financeapp.Entity.ExpenseEntity;
import org.example.financeapp.Entity.User;
import org.example.financeapp.Services.AIService;
import org.example.financeapp.Services.ExpenseService;
import org.example.financeapp.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/expense")
public class ExpenseController {
    @Autowired
    private ExpenseService expenseService;
    @Autowired
    private UserService userService;
    @Autowired
    private AIService aiService;
@GetMapping("/all")
    public List<ExpenseEntity> findAll(){
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String username = authentication.getName();
    User user = userService.findByUsername(username);
    return expenseService.findByUser(user);

}

@PostMapping("/post")
    public ResponseEntity<?> saveExpense(@RequestBody ExpenseEntity expenseEntity){
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null) {
        System.out.println("DEBUG: Authentication object is completely NULL!");
    } else {
        System.out.println("DEBUG: Principal Name is -> " + authentication.getName());
        System.out.println("DEBUG: Is Authenticated? -> " + authentication.isAuthenticated());
    }
    String username = authentication.getName();
    User user=userService.findByUsername(username);




    if(user!=null)
    expenseEntity.setUser(user);
    else
        return new ResponseEntity<>(Map.of("message","User not found"), HttpStatus.BAD_REQUEST);
    String unresolved=expenseEntity.getCategory();
    String resolved=aiService.classifyCategory(unresolved);
    expenseEntity.setCategory(resolved);
    log.info("resolved category"+resolved);
    String alert=expenseService.saveExpense(expenseEntity);
    return ResponseEntity.ok(Map.of("message","Saved Successfully"+alert));
}

@DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteExpenseById(@PathVariable Long id){
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String username = authentication.getName();
    User user = userService.findByUsername(username);
    if(user==null)
        return new ResponseEntity<>(Map.of("message","User not logged in"), HttpStatus.BAD_REQUEST);
    ExpenseEntity expenseEntity = expenseService.findById(id);
    if(expenseEntity==null)
        return new ResponseEntity<>(Map.of("message","Expense not found"), HttpStatus.NOT_FOUND);
    if(!(expenseEntity.getUser().getId()==user.getId()))
        return new  ResponseEntity<>(Map.of("message","Unauthorized Request"), HttpStatus.BAD_REQUEST);

    expenseService.deleteById(id);
    return ResponseEntity.ok(Map.of("message","Deleted Successfully"));
}

@PutMapping("/update/{id}")
    public ResponseEntity<?> updateExpenseById(@RequestBody ExpenseEntity expenseEntity, @PathVariable Long id){
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null) {
        System.out.println("DEBUG: Authentication object is completely NULL!");
    } else {
        System.out.println("DEBUG: Principal Name is -> " + authentication.getName());
        System.out.println("DEBUG: Is Authenticated? -> " + authentication.isAuthenticated());
    }

    String username = authentication.getName();
    User user=userService.findByUsername(username);
    if(user!=null)
        expenseEntity.setUser(user);
    else
        return new ResponseEntity<>(Map.of("message","User not logged in"), HttpStatus.BAD_REQUEST);
    ExpenseEntity prevExpense=expenseService.findById(id);
    if(!(prevExpense.getUser().getId()==user.getId()))
        return new ResponseEntity<>(Map.of("message","Expense not found"), HttpStatus.BAD_REQUEST);
    String unresolved=expenseEntity.getCategory();
    String resolved=aiService.classifyCategory(unresolved);
    prevExpense.setCategory(resolved);
    prevExpense.setDate(expenseEntity.getDate());

    prevExpense.setAmount(expenseEntity.getAmount());
    prevExpense.setExpenseLimit(expenseEntity.getExpenseLimit());
    String alert=expenseService.saveExpense(prevExpense);
    return  ResponseEntity.ok(Map.of("message","Saved Successfully "+alert));
}

    @GetMapping("/analytics")
    public ResponseEntity<Map<String, Object>> getDashboardSummary(
            @RequestParam(value = "month", required = false) Integer month,
            @RequestParam(value = "year", required = false) Integer year
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.findByUsername(username);
        Long userId = user.getId();


        java.time.LocalDate now = java.time.LocalDate.now();
        int targetMonth = (month != null) ? month : now.getMonthValue();
        int targetYear = (year != null) ? year : now.getYear();

        List<Object[]> rawChartData = expenseService.getMonthlyTotalsGroupedByCategory(userId,targetMonth,targetYear);
        List<Map<String, Object>> formattedChartData = new ArrayList<>();
        for (Object[] row : rawChartData) {
            Map<String, Object> dataPoint = new HashMap<>();
            dataPoint.put("category", row[0] != null ? row[0].toString() : "Uncategorized");
            dataPoint.put("value", row[1]);
            formattedChartData.add(dataPoint);
        }


        double monthlySpent = expenseService.getMonthlySpent(userId, targetMonth, targetYear);
        double totalSpentToDate = expenseService.getTotalSpent(userId, targetMonth, targetYear);
        String mostSpentCategory = expenseService.getMostSpentCategory(userId, targetMonth, targetYear);
        int limitsCrossed = expenseService.countLimitsCrossed(userId, targetMonth, targetYear);


        Map<String, Object> summaryPayload = new HashMap<>();
        summaryPayload.put("chartData", formattedChartData);
        summaryPayload.put("monthlySpent", monthlySpent);
        summaryPayload.put("totalSpentToDate", totalSpentToDate);
        summaryPayload.put("mostSpentCategory", mostSpentCategory != null ? mostSpentCategory : "None");
        summaryPayload.put("limitsCrossedCount", limitsCrossed);

        return ResponseEntity.ok(summaryPayload);
    }
}
