package com.example.student_attendance_system.Model;

public class LectureModel {

    int id;
    String SName,Department,Division,Type,year,RollNo,Schedule;

    public LectureModel(int Did,String SName, String department, String division, String type, String year, String rollNo, String schedule) {
        this.id = Did;
        this.SName = SName;
        Department = department;
        Division = division;
        Type = type;
        this.year = year;
        RollNo = rollNo;
        Schedule = schedule;
    }

    public int getId() {
        return id;
    }

    public String getSName() {
        return SName;
    }

    public String getDepartment() {
        return Department;
    }

    public String getDivision() {
        return Division;
    }

    public String getType() {
        return Type;
    }

    public String getYear() {
        return year;
    }

    public String getRollNo() {
        return RollNo;
    }

    public String getSchedule() {
        return Schedule;
    }
}
