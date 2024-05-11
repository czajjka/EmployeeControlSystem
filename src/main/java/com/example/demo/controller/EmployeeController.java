package com.example.demo.controller;

import com.example.demo.entity.Employee;
import com.example.demo.entity.EntryExit;
import com.example.demo.repository.EmployeeRepository;
import com.example.demo.repository.EntryExitRepository;
import com.example.demo.service.EmployeeServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
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
    public String exitForm(int pin) {
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

}
