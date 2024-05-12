package com.example.demo.repository;

import com.example.demo.entity.Employee;
import com.example.demo.entity.EntryExit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


@Repository
public interface EntryExitRepository extends JpaRepository<EntryExit, Long> {
    List<EntryExit> findByEmployee(Employee employee);
    EntryExit findFirstByEmployeeAndEndTimeIsNullOrderByStartTimeDesc(Employee employee);
    List<EntryExit> findByStartTime(LocalDateTime startTime);

}

