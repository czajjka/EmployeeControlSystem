package com.example.demo.controller;

import com.example.demo.entity.Employee;
import com.example.demo.entity.EntryExit;
import com.example.demo.repository.EmployeeRepository;
import com.example.demo.repository.EntryExitRepository;
import com.example.demo.service.EmployeeServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
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

    @GetMapping("/")
    public String viewHomePage(Model model) {
        model.addAttribute("allemplist", employeeServiceImpl.getAllEmployee());
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
        Employee employee = new Employee();
        model.addAttribute("employee", employee);
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
    public String getAllUserReports(@RequestParam(value = "startTime", required = false) String startTimeString, Model model) {
        List<EntryExit> allReports;

        if (startTimeString == null || startTimeString.isEmpty()) {
            allReports = entryExitRepository.findAll();
        } else {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDateTime dt = LocalDate.parse(startTimeString, formatter).atStartOfDay();
            allReports = entryExitRepository.findByStartTime(dt);
        }

        model.addAttribute("reports", allReports);

        return "allreports";
    }
}

//    @GetMapping("/reports")
//    public String getAllUserReports(@RequestParam(value = "startTime", required = false) String startTimeString, @RequestParam(value = "endTime", required = false) String endTimeString,
//                                    Model model) {
//        List<EntryExit> reports;
//
//        if ((startTimeString == null || startTimeString.isEmpty()) && (endTimeString == null || endTimeString.isEmpty())) {
//            reports = entryExitRepository.findAll();
//            LocalDateTime startTime = null;
//            LocalDateTime endTime = null;
//
//            // Parsuj datę początkową, jeśli jest dostępna
//            if (startTimeString != null && !startTimeString.isEmpty()) {
//                startTime = LocalDateTime.parse(startTimeString + " 00:00:00.000000", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS"));
//            }
//
//            // Parsuj datę końcową, jeśli jest dostępna
//            if (endTimeString != null && !endTimeString.isEmpty()) {
//                endTime = LocalDateTime.parse(endTimeString + " 23:59:59.999999", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS"));
//            }
//
//            // Jeśli obie daty są dostępne, użyj filtrowania między datami
//            if (startTime != null && endTime != null) {
//                reports = entryExitRepository.findByStartTimeBetween(startTime, endTime);
//            } else if (startTime != null) { // Jeśli jest dostępna tylko data początkowa, użyj filtrowania począwszy od tej daty
//                reports = entryExitRepository.findByStartTimeAfter(startTime);
//            } else { // Jeśli jest dostępna tylko data końcowa, użyj filtrowania do tej daty
//                reports = entryExitRepository.findByStartTimeBefore(endTime);
//            }
//        }
//
//        model.addAttribute("reports", reports);
//
//        return "allreports";
//    }
//}
