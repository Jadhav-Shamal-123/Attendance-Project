package com.example.student_attendance_system.Model;

import java.util.List;

public class NewStudentRegModel {

    String SName;
    String rollNo;
    String CourseName;
    String Year;
    String Username;
    String Password;
    String PNumber;

    public NewStudentRegModel(String SName, String rollNo, String courseName, String year, String username, String password, String PNumber) {
        this.SName = SName;
        this.rollNo = rollNo;
        CourseName = courseName;
        Year = year;
        Username = username;
        Password = password;
        this.PNumber = PNumber;
    }


    public String getSName() {
        return SName;
    }

    public void setSName(String SName) {
        this.SName = SName;
    }

    public String getRollNo() {
        return rollNo;
    }

    public void setRollNo(String rollNo) {
        this.rollNo = rollNo;
    }

    public String getCourseName() {
        return CourseName;
    }

    public void setCourseName(String courseName) {
        CourseName = courseName;
    }

    public String getYear() {
        return Year;
    }

    public void setYear(String year) {
        Year = year;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getPNumber() {
        return PNumber;
    }

    public void setPNumber(String PNumber) {
        this.PNumber = PNumber;
    }
}
