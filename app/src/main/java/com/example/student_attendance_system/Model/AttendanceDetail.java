package com.example.student_attendance_system.Model;

public class AttendanceDetail {
    private String date;
    private String status;

    public AttendanceDetail(String date, String status) {
        this.date = date;
        this.status = status;
    }

    // Getters and setters
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
