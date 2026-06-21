package org.example.financeapp.Services;

import org.example.financeapp.Entity.EarningsEntity;
import org.example.financeapp.Entity.ExpenseEntity;
import org.example.financeapp.Entity.User;
import org.example.financeapp.Repositories.EarningsRepository;
import org.example.financeapp.Repositories.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class EarningsService {
    @Autowired
    private EarningsRepository earningsRepository;

    public List<EarningsEntity> findByUser(User user){

        return earningsRepository.findByUser(user);
    }

    public void saveEarnings(EarningsEntity earningsEntity){
        earningsRepository.save(earningsEntity);
    }

    public EarningsEntity findById(Long id){
        return earningsRepository.findById(id).get();
    }
    public void deleteById(Long id){
        earningsRepository.deleteById(id);
    }



    public Double getMonthlyEarnings(Long userId, int targetMonth, int targetYear) {
        Double total=earningsRepository.getMonthlyEarnings(userId, targetMonth, targetYear);
        if(total==null){
            total=0.0;
        }
        return total;
    }

    public Double getTotalEarned(Long userId, int targetMonth, int targetYear) {
        Double total= earningsRepository.getTotalEarned(userId, targetMonth, targetYear);
        if(total==null){
            total=0.0;
        }
        return total;
    }

    public String getMostEarnedCategory(Long userId, int targetMonth, int targetYear) {
        return earningsRepository.getMostEarnedCategory(userId, targetMonth, targetYear);
    }

    public String getMostSavingsCategory(Long userId, int targetMonth, int targetYear) {
        return  earningsRepository.getMostSavingsCategory(userId, targetMonth, targetYear);
    }

    public List<Object[]> getMonthlyEarningsGroupedByCategory(Long userId,int month,int year) {
        return earningsRepository.getMonthlyEarningsGroupedByCategory(userId,month,year);
    }
}
