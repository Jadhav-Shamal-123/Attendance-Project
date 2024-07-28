package com.example.student_attendance_system;

public class PresentyModel
{
    int RollNo;
    String Name;
    private boolean isSelected;
    String status;
    int Pcount;
    int Acount;

    public int getPcount()
    {
        return Pcount;
    }

    public void setPcount(int pcount)
    {
        Pcount = pcount;
    }

    public int getAcount() {
        return Acount;
    }

    public void setAcount(int acount) {
        Acount = acount;
    }

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isSelected() {
        return isSelected;
    }
    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public int getRollNo() {
        return RollNo;
    }
    public void setRollNo(int rollno) {
        RollNo = rollno;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }


}
