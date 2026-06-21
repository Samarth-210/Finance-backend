package org.example.financeapp.Services;

import org.example.financeapp.Entity.ExpenseEntity;
import org.example.financeapp.Entity.User;
import org.example.financeapp.Repositories.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class ExpenseService {
    @Autowired
    private ExpenseRepository expenseRepository;
    public List<ExpenseEntity> findAll(){
        return expenseRepository.findAll();
    }

    public String saveExpense(ExpenseEntity expenseEntity){

        ExpenseEntity saved=expenseRepository.save(expenseEntity);
        java.time.LocalDate now = java.time.LocalDate.now();
        int targetMonth = now.getMonthValue();
        int targetYear =  now.getYear();

        Map<String,Long> map=setMonthlyTotal(saved.getUser().getId(),(double)saved.getAmount(),targetMonth,targetYear);
        String message=checkAlert(saved,map);
        return message;
    }

    public void deleteById(Long id){
        expenseRepository.deleteById(id);
    }

    public ExpenseEntity findById(Long id){
        return expenseRepository.findById(id).get();
    }

    public HashMap<String,Long> setMonthlyTotal(Long id,Double amount,Integer month,Integer year){
        HashMap<String,Long> map=new HashMap<>();
        List<Object[]> monthly= expenseRepository.getMonthlyTotalsGroupedByCategory(id,month,year);
        for(Object[] ob:monthly){
            String key=String.valueOf(ob[0]);
            Long value=((Number)ob[1]).longValue();
            map.put(key,value);
        }
        return map;
    }
    public String checkAlert(ExpenseEntity saved,Map<String,Long> map) {
        long newTotal=map.getOrDefault(saved.getCategory(),0L);

        map.put(saved.getCategory(),newTotal);
        if(newTotal>saved.getExpenseLimit()){
            return "Expense Limit Exceeded";
        }
        else if(newTotal>=0.50*saved.getExpenseLimit()){
            return "50% expense limit exceeded";
        }
        else{
            return "expenses under control";
        }
    }
    public List<ExpenseEntity> findByUser(User user){
        return expenseRepository.findByUser(user);
    }

    public List<Object[]> getMonthlyTotalsGroupedByCategory(Long userId,Integer month,Integer year) {
        return expenseRepository.getMonthlyTotalsGroupedByCategory(userId,month,year);
    }

    public Double getMonthlySpent(Long userId, int currentMonth, int currentYear) {
        Double total= expenseRepository.getMonthlySpent(userId, currentMonth, currentYear);
        if(total==null)
            total=0.0;
        return total;
    }

    public Double getTotalSpent(Long userId, int currentMonth, int currentYear) {
        Double total= expenseRepository.getTotalSpent(userId, currentMonth, currentYear);
        if(total==null)
            total=0.0;
        return total;
    }

    public String getMostSpentCategory(Long userId, int currentMonth, int currentYear) {
        return expenseRepository.getMostSpentCategory(userId, currentMonth, currentYear);
    }

    public int countLimitsCrossed(Long userId, int currentMonth, int currentYear) {
        return expenseRepository.countLimitsCrossed(userId, currentMonth, currentYear);
    }
}
