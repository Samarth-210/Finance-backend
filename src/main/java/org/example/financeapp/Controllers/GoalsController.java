package org.example.financeapp.Controllers;

import org.example.financeapp.Entity.Goals;
import org.example.financeapp.Entity.User;
import org.example.financeapp.Services.GoalsService;
import org.example.financeapp.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/goals")
public class GoalsController {
    @Autowired
    UserService userService;

    @Autowired
    GoalsService goalsService;

    @GetMapping("/get")
    public List<Goals> findGoals() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user=userService.findByUsername(username);
        return goalsService.findByUser(user);
    }

    @PostMapping("/post")
    public ResponseEntity<?> saveGoals(@RequestBody Goals goals){
         Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
         String username = authentication.getName();
         User user=userService.findByUsername(username);
         goals.setUser(user);
         goalsService.saveGoals(goals);
         return new ResponseEntity<>(Map.of("message","goals saved successfully"), HttpStatus.OK);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateGoals(@RequestBody Goals goals, @PathVariable long id){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user=userService.findByUsername(username);
        Goals prevGoals=goalsService.findGoalsById(id);

        if((prevGoals.getUser().getId()!=user.getId())){
            return new ResponseEntity<>(Map.of("message","Unauthorized access"), HttpStatus.UNAUTHORIZED);
        }
        Goals prev=goalsService.findGoalsById(id);
        prev.setAim(goals.getAim());
        prev.setTask(goals.getTask());
        prev.setDeadline(goals.getDeadline());
        prev.setCompleted(goals.isCompleted());
        goalsService.saveGoals(goals);
        return new  ResponseEntity<>(Map.of("message","goals updated successfully"), HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteGoals(@PathVariable long id){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user=userService.findByUsername(username);
        if(goalsService.findGoalsById(id).getUser().getId()!=user.getId()){
            return new  ResponseEntity<>(Map.of("message","Unauthorized access"), HttpStatus.UNAUTHORIZED);
        }
        goalsService.deleteGoalsById(id);
        return new ResponseEntity<>(Map.of("message","goals deleted successfully"), HttpStatus.OK);
    }
    //handles the case when user clicks the completed check box by mistake or repeated number of times
    @PutMapping("change/{id}/status")
    public ResponseEntity<?> changeGoalStatus(@RequestParam("completed") boolean completed, @PathVariable("id") long id){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user=userService.findByUsername(username);
        Goals goal=goalsService.findGoalsById(id);
        if(goal.getUser().getId()!=user.getId()){
            return new  ResponseEntity<>(Map.of("message","Unauthorized access"), HttpStatus.UNAUTHORIZED);
        }
        goalsService.updateGoalStatus(id, completed);
        return new  ResponseEntity<>(Map.of("message","goals updated successfully"), HttpStatus.OK);
    }
}
