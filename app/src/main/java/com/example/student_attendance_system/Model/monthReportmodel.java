package com.example.student_attendance_system.Model;

import java.util.List;

public class monthReportmodel {

    private String rollNo;
    private String studentName;
    private String date;
    private String status;

    public monthReportmodel(String rollNo, String studentName, String date, String status) {
        this.rollNo = rollNo;
        this.studentName = studentName;
        this.date = date;
        this.status = status;
    }

    public String getRollNo() {
        return rollNo;
    }

    public void setRollNo(String rollNo) {
        this.rollNo = rollNo;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
