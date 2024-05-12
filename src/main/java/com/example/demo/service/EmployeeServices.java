package com.example.demo.service;

import com.example.demo.entity.Employee;
import com.example.demo.entity.EntryExit;

import java.util.List;

public interface EmployeeServices {

    List<Employee> getAllEmployee();

    void save(Employee employee);

    Employee getById(Long id);

    void deleteViaId(long id);

    //report

    Employee getByPin(int pin);

    void save(EntryExit entryExit);

    List<EntryExit> getReportsForEmployee(Long employeeId);


}