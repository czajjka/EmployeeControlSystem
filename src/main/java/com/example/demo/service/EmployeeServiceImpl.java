package com.example.demo.service;

import com.example.demo.entity.Employee;
import com.example.demo.entity.EntryExit;
import com.example.demo.repository.EmployeeRepository;
import com.example.demo.repository.EntryExitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EmployeeServiceImpl implements EmployeeServices {

    @Autowired
    private EmployeeRepository empRepo;

    @Autowired
    private EntryExitRepository entryExitRepo;


    @Override
    public List<Employee> getAllEmployee() {
        return empRepo.findAll();
    }

    @Override
    public void save(Employee employee) {
        empRepo.save(employee);
    }

    @Override
    public Employee getById(Long id) {
        Optional<Employee> optional = empRepo.findById(id);
        Employee employee = null;
        if (optional.isPresent())
            employee = optional.get();
        else
            throw new RuntimeException(
                    "Employee not found for id : " + id);
        return employee;
    }

    @Override
    public void deleteViaId(long id) {
        empRepo.deleteById(id);
    }

    @Override
    public Employee getByPin(int pin) {
        return empRepo.findByPin(pin);
    }

    @Override
    public void save(EntryExit entryExit) {
        entryExitRepo.save(entryExit);
    }

    @Override
    public List<EntryExit> getReportsForEmployee(Long employeeId) {
        Employee employee = empRepo.findById(employeeId).orElse(null);
        return entryExitRepo.findByEmployee(employee);
    }
    public long calculateWorkTimeForEmployee(Long employeeId, LocalDateTime startTime, LocalDateTime endTime) {
        // Pobierz wpisy dotyczące czasu pracy dla danego pracownika
        List<EntryExit> reportsForEmployee = (List<EntryExit>) empRepo.getById(employeeId);

        // Jeśli podano daty, to filtrowanie wpisów tylko dla określonego zakresu czasowego
        if (startTime != null && endTime != null) {
            reportsForEmployee = reportsForEmployee.stream()
                    .filter(entryExit -> entryExit.getStartTime().isAfter(startTime) && entryExit.getEndTime().isBefore(endTime))
                    .collect(Collectors.toList());
        }

        // Sumowanie czasu pracy tylko dla pracownika
        long totalWorkTime = reportsForEmployee.stream()
                .mapToLong(EntryExit::getWorkTime)
                .sum();

        return totalWorkTime;
    }
}