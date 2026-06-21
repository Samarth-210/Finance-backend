package org.example.financeapp.Repositories;

import org.example.financeapp.Entity.EarningsEntity;
import org.example.financeapp.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EarningsRepository extends JpaRepository<EarningsEntity,Long> {
    List<EarningsEntity> findByCategory(String category);
    List<EarningsEntity> findByUser(User user);

    @Query(value="SELECT SUM(e.amount) FROM earnings e WHERE e.user_id=:userId AND MONTH(e.date)=:month AND YEAR(e.date)=:year",nativeQuery=true)
    Double getMonthlyEarnings(@Param("userId") long userId, @Param("month") int month, @Param("year") int year);
    //using wrapper class instead of primitive type to handle possible generation of null values
    @Query(value="SELECT SUM(e.amount) FROM earnings e WHERE e.user_id=:userId AND e.date<=LAST_DAY(STR_TO_DATE(CONCAT(:year,'-',:month,'-01'),'%Y-%m-%d'))",nativeQuery = true)
    Double getTotalEarned(@Param("userId") long userId,@Param("month") int month, @Param("year") int year);

    @Query(value = "SELECT e.category FROM earnings e WHERE e.user_id = :userId AND MONTH(e.date) = :month AND YEAR(e.date) = :year GROUP BY e.category ORDER BY SUM(e.amount) DESC LIMIT 1", nativeQuery = true)
    String getMostEarnedCategory(@Param("userId") Long userId, @Param("month") int month, @Param("year") int year);

    @Query(value = "SELECT category FROM ( " +
            "  SELECT category, SUM(net_amount) AS total_savings FROM ( " +
            "    SELECT category, amount AS net_amount FROM earnings WHERE user_id = :userId AND MONTH(date) = :month AND YEAR(date) = :year " +
            "    UNION ALL " +
            "    SELECT category, -amount AS net_amount FROM expenses WHERE user_id = :userId AND MONTH(date) = :month AND YEAR(date) = :year " +
            "  ) AS combined_transactions GROUP BY category " +
            ") AS category_totals ORDER BY total_savings DESC LIMIT 1",
            nativeQuery = true)
    String getMostSavingsCategory(@Param("userId") Long userId, @Param("month") int month, @Param("year") int year);

    @Query("SELECT e.category, COALESCE(SUM(e.amount), 0) " +
            "FROM EarningsEntity e " +
            "WHERE e.user.id = :userId " +
            "AND MONTH(e.date) = :month " +
            "AND YEAR(e.date) = :year " +
            "GROUP BY e.category")
    List<Object[]> getMonthlyEarningsGroupedByCategory(
            @Param("userId") Long userId,
            @Param("month") int month,
            @Param("year") int year
    );
}

