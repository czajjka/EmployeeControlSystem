package com.example.demo.entity;

public class EmployeeReport {

    private Employee employee;
    private long totalWorkHours;

    public EmployeeReport(Employee employee, long totalWorkHours) {
        this.employee = employee;
        this.totalWorkHours = totalWorkHours;
    }

    // Getters and Setters
    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public long getTotalWorkHours() {
        return totalWorkHours;
    }

    public void setTotalWorkHours(long totalWorkHours) {
        this.totalWorkHours = totalWorkHours;
    }
}