package org.example.financeapp.Repositories;

import org.example.financeapp.Entity.Goals;
import org.example.financeapp.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GoalsRepository extends JpaRepository<Goals, Long> {
    public List<Goals> findByUser(User user);

    @Query(value="SELECT COUNT(*) FROM goals WHERE user_id=:userId AND completed=true AND completionDate<=deadline",nativeQuery=true)
    int countGoalsCompleted(@Param("userId")long userId );
}
