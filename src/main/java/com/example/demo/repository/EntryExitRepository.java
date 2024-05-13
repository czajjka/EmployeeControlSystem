package com.example.demo.repository;

import com.example.demo.entity.Employee;
import com.example.demo.entity.EntryExit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


@Repository
public interface EntryExitRepository extends JpaRepository<EntryExit, Long> {
    List<EntryExit> findByEmployee(Employee employee);

    EntryExit findFirstByEmployeeAndEndTimeIsNullOrderByStartTimeDesc(Employee employee);

    @Query("SELECT e FROM EntryExit e WHERE YEAR(e.startTime) = YEAR(:startTime) AND MONTH(e.startTime) = MONTH(:startTime) AND DAY(e.startTime) = DAY(:startTime)")
    List<EntryExit> findByStartTime(LocalDateTime startTime);

    @Query("SELECT e FROM EntryExit e WHERE YEAR(e.endTime) = YEAR(:endTime) AND MONTH(e.endTime) = MONTH(:endTime) AND DAY(e.endTime) = DAY(:endTime)")
    List<EntryExit> findByEndTime(LocalDateTime endTime);

    @Query("SELECT e FROM EntryExit e WHERE e.startTime BETWEEN :startTime AND :endTime")
    List<EntryExit> findByTimeRange(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    @Query("SELECT e FROM EntryExit e WHERE e.startTime BETWEEN :startTime AND :endTime AND e.employee = :employee")
    List<EntryExit> findByTimeRangeAndEmployee(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime, @Param("employee") Long employee);

    List<EntryExit> findByEmployeeId(Long employeeId);

}

