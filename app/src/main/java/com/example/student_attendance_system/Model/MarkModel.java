package com.example.student_attendance_system.Model;

public class MarkModel {

    String rollno ;
    String courseName ;
    String year ;
    String SubjectName ;
    String Teacher ;
    String studentName ;
    String Schedule ;

    public MarkModel(String rollno, String courseName, String year, String subjectName, String teacher, String studentName, String schedule) {
        this.rollno = rollno;
        this.courseName = courseName;
        this.year = year;
        SubjectName = subjectName;
        Teacher = teacher;
        this.studentName = studentName;
        Schedule = schedule;
    }

    public String getRollno() {
        return rollno;
    }

    public String getCourseName() {
        return courseName;
    }

    public String getYear() {
        return year;
    }

    public String getSubjectName() {
        return SubjectName;
    }

    public String getTeacher() {
        return Teacher;
    }

    public String getStudentName() {
        return studentName;
    }

    public String getSchedule() {
        return Schedule;
    }
}
