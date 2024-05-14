package com.example.demo.controller;

import com.example.demo.entity.Employee;
import com.example.demo.entity.EmployeeReport;
import com.example.demo.entity.EntryExit;
import com.example.demo.repository.EmployeeRepository;
import com.example.demo.repository.EntryExitRepository;
import com.example.demo.service.EmployeeServiceImpl;
import com.example.demo.service.EmployeeServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Controller
public class EmployeeController {

    @Autowired
    private EmployeeServiceImpl employeeServiceImpl;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private EntryExitRepository entryExitRepository;

    @Autowired
    private EmployeeServices employeeServices;

    @Autowired
    public EmployeeController(EmployeeServices employeeServices) {
        this.employeeServices = employeeServices;
    }

    @GetMapping("/")
    public String viewHomePage(Model model) {
        List<Employee> allEmployees = employeeServices.getAllEmployee();
        model.addAttribute("allemplist", allEmployees);
        return "index";
    }

    @GetMapping("/addnew")
    public String addNewEmployee(Model model) {
        Employee employee = new Employee();
        model.addAttribute("employee", employee);
        return "newemployee";
    }

    @PostMapping("/save")
    public String saveEmployee(@ModelAttribute("employee") Employee employee) {
        employeeServiceImpl.save(employee);
        return "redirect:/";
    }

    @GetMapping("/showFormForUpdate/{id}")
    public String updateForm(@PathVariable(value = "id") long id, Model model) {
        Employee employee = employeeServiceImpl.getById(id);
        model.addAttribute("employee", employee);
        return "update";
    }

    @GetMapping("/deleteEmployee/{id}")
    public String deleteThroughId(@PathVariable(value = "id") long id) {
        employeeServiceImpl.deleteViaId(id);
        return "redirect:/";
    }

    @GetMapping("/entry")
    public String addNewEntry(Model model) {
        EntryExit report = new EntryExit();
        model.addAttribute("report", report);
        return "entry";
    }

    @PostMapping("/saveEntry")
    public String updateForm(int pin) {
        try {
            Employee employee = employeeServiceImpl.getByPin(pin);
            LocalDateTime now = LocalDateTime.now();
            EntryExit entryExit = entryExitRepository.findFirstByEmployeeAndEndTimeIsNullOrderByStartTimeDesc(employee);
            if (entryExit != null && entryExit.getStartTime() != null && entryExit.getEndTime() == null) {
                return "erroruser";

            } else {
                entryExit = new EntryExit();
                entryExit.setStartTime(now);
                entryExit.setEmployee(employee);
                employeeServiceImpl.save(entryExit);
                return "redirect:/";

            }
        } catch (Exception e) {
            System.err.println("Wystąpił błąd podczas ustawiania właściwości: " + e.getMessage());
            return "redirect:/erroruser";
        }
    }

    @GetMapping("/exit")
    public String addNewExit(Model model) {
        EntryExit report = new EntryExit();
        model.addAttribute("report", report);
        return "exit";
    }

    @PostMapping("/exitEntry")
    public String exitForm(int pin, Model model) {
        try {
            Employee employee = employeeServiceImpl.getByPin(pin);
            LocalDateTime now = LocalDateTime.now();
            EntryExit entryExit = entryExitRepository.findFirstByEmployeeAndEndTimeIsNullOrderByStartTimeDesc(employee);
            if (entryExit != null && entryExit.getStartTime() != null && entryExit.getEndTime() != null) {
                return "erroruser";

            } else {
                entryExit.getStartTime();
                entryExit.setEndTime(now);
                employeeServiceImpl.save(entryExit);
                return "redirect:/";
            }
        } catch (Exception e) {
            System.err.println("Wystąpił błąd podczas ustawiania właściwości: " + e.getMessage());
            return "erroruser";
        }
    }

    @GetMapping("/reports/{employeeId}")
    public String getReportsForEmployee(@PathVariable Long employeeId, Model model) {
        Employee employee = employeeRepository.findById(employeeId).orElse(null);
        if (employee == null) {
            return "usernotfound";
        }
        List<EntryExit> reports = entryExitRepository.findByEmployee(employee);
        model.addAttribute("reports", reports);
        model.addAttribute("employee", employee);
        return "report";
    }

    @GetMapping("/addreports")
    public String addNewReport(Model model) {
        List<Employee> allEmployees = employeeServices.getAllEmployee();
        model.addAttribute("allemplist", allEmployees);
        return "addreports";
    }

    @PostMapping("/reports")
    public String getReportsForEmployeee(@RequestParam Long employeeId, Model model) {
        Employee employee = employeeRepository.findById(employeeId).orElse(null);

        if (employee == null) {
            return "usernotfound";
        }
        List<EntryExit> reports = entryExitRepository.findByEmployee(employee);
        model.addAttribute("reports", reports);
        model.addAttribute("employee", employee);

        return "report";
    }

    @GetMapping("/reports")
    public String getAllUserReports(@RequestParam(value = "startTime", required = false) String startTimeString,
                                    @RequestParam(value = "endTime", required = false) String endTimeString,
                                    @RequestParam(value = "employeeId", required = false) Long employeeId,
                                    Model model) {
        List<EntryExit> allReports;
        long getWorkHours = 0;


        if (employeeId != null && startTimeString != null && !startTimeString.isEmpty() && endTimeString != null && !endTimeString.isEmpty()) {
            // Jeśli wybrano pracownika i przekazano daty wejścia i wyjścia, filtruj raporty dla tego pracownika z uwzględnieniem dat
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDateTime startTime = LocalDate.parse(startTimeString, formatter).atStartOfDay();
            LocalDateTime endTime = LocalDate.parse(endTimeString, formatter).atTime(LocalTime.MAX);
            allReports = entryExitRepository.findByTimeRangeAndEmployee(startTime, endTime, employeeId);
        } else if (employeeId != null) {
            // Jeśli wybrano pracownika, ale nie przekazano dat, zwróć wszystkie raporty dla tego pracownika
            allReports = entryExitRepository.findByEmployeeId(employeeId);
            for (EntryExit ele : allReports) {
                getWorkHours += ele.getWorkTime();
            }
        } else {
            // Jeśli nie wybrano pracownika, sprawdź, czy przekazano daty
            if ((startTimeString == null || startTimeString.isEmpty()) && (endTimeString == null || endTimeString.isEmpty())) {
                // Jeśli nie przekazano dat, zwróć wszystkie raporty
                allReports = entryExitRepository.findAll();
            } else if (startTimeString != null && !startTimeString.isEmpty() && (endTimeString == null || endTimeString.isEmpty())) {
                // Jeśli przekazano tylko datę początkową, filtruj raporty po dacie początkowej
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDateTime startTime = LocalDate.parse(startTimeString, formatter).atStartOfDay();
                LocalDateTime endTime = LocalDateTime.MAX;
                allReports = entryExitRepository.findByStartTime(startTime);
            } else if (startTimeString == null || startTimeString.isEmpty()) {
                // Jeśli przekazano tylko datę końcową, filtruj raporty po dacie końcowej
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDateTime startTime = LocalDateTime.MIN;
                LocalDateTime endTime = LocalDate.parse(endTimeString, formatter).atTime(LocalTime.MAX);
                allReports = entryExitRepository.findByEndTime(endTime);
            } else {
                // Jeśli przekazano obie daty, filtruj raporty po zakresie dat
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDateTime startTime = LocalDate.parse(startTimeString, formatter).atStartOfDay();
                LocalDateTime endTime = LocalDate.parse(endTimeString, formatter).atTime(LocalTime.MAX);
                allReports = entryExitRepository.findByTimeRange(startTime, endTime);
            }
        }
        List<Employee> allEmployees = employeeRepository.findAll();
        model.addAttribute("allemplist", allEmployees);
        model.addAttribute("reports", allReports);
        model.addAttribute("totalWorkHours", getWorkHours);


        return "allreports";
    }

    @GetMapping("/indexreport")
    public String calculateWorkTimeForEmployee(
            @RequestParam(value = "startTime", required = false) String startTimeString,
            @RequestParam(value = "endTime", required = false) String endTimeString,
            @RequestParam(value = "employeeId", required = false) Long employeeId,
            Model model) {

        List<Employee> allEmployees = employeeRepository.findAll();
        List<EmployeeReport> employeeReports = new ArrayList<>();

        for (Employee employee : allEmployees) {
            long totalWorkHours = 0;

            List<EntryExit> reportsForEmployee = employeeServiceImpl.getReportsForEmployee(employee.getId());

            if (startTimeString != null && !startTimeString.isEmpty() && endTimeString != null && !endTimeString.isEmpty()) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDateTime startTime = LocalDate.parse(startTimeString, formatter).atStartOfDay();
                LocalDateTime endTime = LocalDate.parse(endTimeString, formatter).atTime(LocalTime.MAX);
                totalWorkHours = employeeServiceImpl.calculateWorkTimeForEmployee(employee.getId(), startTime, endTime);
            } else {
                // Oblicz podsumowanie godzin dla wszystkich dostępnych raportów pracownika
                totalWorkHours = reportsForEmployee.stream()
                        .mapToLong(EntryExit::getWorkTime)
                        .sum();
            }

            EmployeeReport employeeReport = new EmployeeReport(employee, totalWorkHours);
            employeeReports.add(employeeReport);
        }

        model.addAttribute("allemplist", allEmployees);
        model.addAttribute("employeeReports", employeeReports);
        return "indexreport";
    }
}