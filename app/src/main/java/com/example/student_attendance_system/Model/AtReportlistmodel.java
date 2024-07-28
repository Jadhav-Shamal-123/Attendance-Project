package com.example.student_attendance_system.Model;

public class AtReportlistmodel {

    private String RollNo;
    private String SName;
    private String PCount;
    private String ACount;
    private String Percentage;
    private String Remark;

    public AtReportlistmodel(String rollNo, String SName, String PCount, String ACount, String percentage, String remark) {
        RollNo = rollNo;
        this.SName = SName;
        this.PCount = PCount;
        this.ACount = ACount;
        Percentage = percentage;
        Remark = remark;
    }


    public String getRollNo() {
        return RollNo;
    }

    public void setRollNo(String rollNo) {
        RollNo = rollNo;
    }

    public String getSName() {
        return SName;
    }

    public void setSName(String SName) {
        this.SName = SName;
    }

    public String getPCount() {
        return PCount;
    }

    public void setPCount(String PCount) {
        this.PCount = PCount;
    }

    public String getACount() {
        return ACount;
    }

    public void setACount(String ACount) {
        this.ACount = ACount;
    }

    public String getPercentage() {
        return Percentage;
    }

    public void setPercentage(String percentage) {
        Percentage = percentage;
    }

    public String getRemark() {
        return Remark;
    }

    public void setRemark(String remark) {
        Remark = remark;
    }
}
