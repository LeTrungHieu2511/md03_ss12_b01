package com.example.md03.repository;

import com.example.md03.entity.Supply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SupplyRepository extends JpaRepository<Supply, Long> {

    @Query("select s from Supply s where s.isDeleted = false")
    List<Supply> findAllActive();

    @Query("select s from Supply s where s.id = :id and s.isDeleted = false")
    Optional<Supply> findActiveById(@Param("id") Long id);

    @Query("select s from Supply s where s.isDeleted = false and lower(s.name) like :pattern")
    List<Supply> searchByName(@Param("pattern") String pattern);
}