package org.example.financeapp.Repositories;

import org.example.financeapp.Entity.Summary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.YearMonth;
import java.util.Optional;

public interface SummaryRepository extends JpaRepository<Summary, Long> {
    public Optional<Summary> findByUserIdAndMonth(long userId, YearMonth monthStr);
}
