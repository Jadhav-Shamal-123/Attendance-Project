package com.example.student_attendance_system.Model;

import java.io.Serializable;

public class attendancemodel  implements Serializable {

    private boolean isChecked;
    private String id;

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
