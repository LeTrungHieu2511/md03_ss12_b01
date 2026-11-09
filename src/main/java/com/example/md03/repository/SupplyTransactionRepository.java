package com.example.md03.repository;

import com.example.md03.entity.SupplyTransaction;
import com.example.md03.entity.TransactionType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface SupplyTransactionRepository extends JpaRepository<SupplyTransaction, Long> {

    @Query("""
        select s.id as supplyId, s.name as supplyName, sum(t.amount) as totalQuantity
        from SupplyTransaction t join t.supply s
        where t.type = :type and t.createdAt >= :start and t.createdAt < :end
        group by s.id, s.name
        """)
    List<ExportSummary> sumByTypeBetween(@Param("type") TransactionType type,
                                         @Param("start") LocalDateTime start,
                                         @Param("end") LocalDateTime end);

    @Query("""
        select s.id as supplyId, s.name as supplyName, sum(t.amount) as totalQuantity
        from SupplyTransaction t join t.supply s
        where t.type = :type
        group by s.id, s.name
        order by sum(t.amount) desc
        """)
    List<ExportSummary> findTopByType(@Param("type") TransactionType type, Pageable pageable);
}