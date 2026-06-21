package org.example.financeapp.Services;

import org.example.financeapp.Entity.Goals;
import org.example.financeapp.Entity.User;
import org.example.financeapp.Repositories.GoalsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Service
public class GoalsService {
    @Autowired
    GoalsRepository goalsRepository;

    public List<Goals> getGoals(){
        return goalsRepository.findAll();
    }
    public List<Goals> findByUser(User user){
        return goalsRepository.findByUser(user);
    }
    public void saveGoals(Goals goals){
        goalsRepository.save(goals);
    }
    public Goals findGoalsById(long id){
        return goalsRepository.findById(id).get();
    }
    public void deleteGoalsById(long id){
        goalsRepository.deleteById(id);
    }
    public void updateGoalStatus(long goalId,boolean isCompleted){
        Goals goal=goalsRepository.findById(goalId).orElseThrow(()->new RuntimeException("Goal not found"));
        goal.setCompleted(isCompleted);
        if(isCompleted){
            goal.setCompletionDate(new Date());
        }
        else {
            goal.setCompletionDate(null);
        }
        goalsRepository.save(goal);
    }

}
