package com.example.demo.service;

import com.example.demo.entity.Employee;
import com.example.demo.entity.EntryExit;
import com.example.demo.exelexport.ExcelExportUtils;
import com.example.demo.repository.EmployeeRepository;
import com.example.demo.repository.EntryExitRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EmployeeServiceImpl implements EmployeeServices {

    @Autowired
    private EmployeeRepository empRepo;

    @Autowired
    private EntryExitRepository entryExitRepo;

    @Autowired
    public EmployeeServiceImpl(EmployeeRepository empRepo, EntryExitRepository entryExitRepo) {
        this.empRepo = empRepo;
        this.entryExitRepo = entryExitRepo;
    }

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

    public List<Employee> exportEmployeeToExcel(HttpServletResponse response, String startTimeString, String endTimeString, Long employeeId) throws IOException {
        List<EntryExit> filteredReports;
        long getWorkHours = 0;
        System.out.println("AAA");
        System.out.println(response.getCharacterEncoding());
        System.out.println(employeeId);
        System.out.println("AAA");

        // Filtruj raporty na podstawie przekazanych parametrów
        if (employeeId != null && startTimeString != null && !startTimeString.isEmpty() && endTimeString != null && !endTimeString.isEmpty()) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDateTime startTime = LocalDate.parse(startTimeString, formatter).atStartOfDay();
            LocalDateTime endTime = LocalDate.parse(endTimeString, formatter).atTime(LocalTime.MAX);
            filteredReports = entryExitRepo.findByTimeRangeAndEmployee(startTime, endTime, employeeId);
        } else if (employeeId != null) {
            filteredReports = entryExitRepo.findByEmployeeId(employeeId);
            for (EntryExit ele : filteredReports) {
                getWorkHours += ele.getWorkTime();
            }
        } else {
            // Jeśli nie podano żadnych filtrów, zwróć wszystkie raporty
            filteredReports = entryExitRepo.findAll();
        }

        // Twórz listę pracowników na podstawie filtrowanych raportów
        List<Employee> employees = new ArrayList<>();
        for (EntryExit entryExit : filteredReports) {
            employees.add(entryExit.getEmployee());
        }

        // Eksportuj dane do excela
        ExcelExportUtils exportUtils = new ExcelExportUtils(employees);
        exportUtils.exportDataToExcel(response);
        return employees;
    }
}