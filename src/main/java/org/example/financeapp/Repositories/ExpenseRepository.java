package org.example.financeapp.Repositories;

import org.example.financeapp.Entity.ExpenseEntity;
import org.example.financeapp.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ExpenseRepository extends JpaRepository<ExpenseEntity, Long> {

    List<ExpenseEntity> findByUser(User user);


@Query("SELECT e.category, COALESCE(SUM(e.amount), 0) " +
        "FROM ExpenseEntity e " +
        "WHERE e.user.id = :userId " +
        "AND MONTH(e.date) = :month " +
        "AND YEAR(e.date) = :year " +
        "GROUP BY e.category")
List<Object[]> getMonthlyTotalsGroupedByCategory(
        @Param("userId") Long userId,
        @Param("month") int month,
        @Param("year") int year
);
    //updating this handles the condition where a user clicks the submit button too quickly within millisecs
    //and then those 2 different clicks would be handled by 2 different threads who contain the same old data without update
    @Query(value="SELECT SUM(e.amount) FROM expenses e WHERE e.user_id=:userId AND MONTH(e.date)=:month AND YEAR(e.date)=:year",nativeQuery=true)
    Double getMonthlySpent(@Param("userId") Long userId, @Param("month") int month, @Param("year") int year);

    @Query(value="SELECT SUM(e.amount) FROM expenses e WHERE user_id=:userId AND e.date<=LAST_DAY(STR_TO_DATE(CONCAT(:year,'-',:month,'-01'),'%Y-%m-%d'))",nativeQuery=true)
    Double getTotalSpent(@Param("userId") Long userId, @Param("month") int month, @Param("year") int year);

    @Query(value="SELECT e.category FROM expenses e WHERE e.user_id=:userId AND MONTH(e.date)=:month AND YEAR(e.date)=:year GROUP BY e.category ORDER BY SUM(e.amount) DESC LIMIT 1",nativeQuery=true)
    String getMostSpentCategory(@Param("userId") Long userId, @Param("month") int month, @Param("year") int year);

    @Query(value = "SELECT COUNT(*) FROM (SELECT e.category FROM expenses e WHERE e.user_id = :userId AND MONTH(e.date) = :month AND YEAR(e.date) = :year GROUP BY e.category,e.expense_limit HAVING SUM(e.amount) > e.expense_limit ) AS temp", nativeQuery = true)
    int countLimitsCrossed(@Param("userId") Long userId, @Param("month") int month, @Param("year") int year);

}