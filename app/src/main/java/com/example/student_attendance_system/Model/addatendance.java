package com.example.student_attendance_system.Model;

public class addatendance {

    private String rollno;
    private String status;

    public addatendance(String rollno, String status) {
        this.rollno = rollno;
        this.status = status;
    }

    public String getRollno() {
        return rollno;
    }

    public String getStatus() {
        return status;
    }
}
