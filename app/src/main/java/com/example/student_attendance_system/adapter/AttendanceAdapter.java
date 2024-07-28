package com.example.student_attendance_system.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.student_attendance_system.Model.addatendance;
import com.example.student_attendance_system.Model.attendancemodel;
import com.example.student_attendance_system.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AttendanceAdapter extends RecyclerView.Adapter<AttendanceAdapter.Viewholder> {

    private Context context;
    private Map<Integer,attendancemodel> attendance;
    List<Integer> rollList;
    onClickedItem listener;

    public void setCountListener(onClickedItem listener) {
        this.listener = listener;
    }

    public AttendanceAdapter(Context context, Map<Integer,attendancemodel> attendance,List<Integer> rollList) {
        this.context = context;
        this.attendance = attendance;
        this.rollList=rollList;
    }

    @NonNull
    @Override
    public AttendanceAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Viewholder(LayoutInflater.from(context).inflate(R.layout.layoutfor_attendance,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull AttendanceAdapter.Viewholder holder, int position) {

        int loc=rollList.get(position);
        holder.textView.setText(attendance.get(loc).getId());
        holder.setAllNotCheck(attendance.get(loc));
        holder.bind(attendance.get(loc));

    }

    @Override
    public int getItemCount() {
        return attendance.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder {

        ImageView absent,present;
        TextView textView;

        public Viewholder(@NonNull View itemView) {
            super(itemView);
            absent = itemView.findViewById(R.id.absent);
            present = itemView.findViewById(R.id.present);
            textView = itemView.findViewById(R.id.rollNo);

        }

        void setAllNotCheck(final attendancemodel employee) {
            employee.setChecked(!employee.isChecked());
            int count=getPresent().size();
            listener.onClicked(count);

        }

        void bind(final attendancemodel employee) {
            absent.setVisibility(employee.isChecked() ? View.VISIBLE : View.GONE);
            present.setVisibility(employee.isChecked() ? View.GONE : View.VISIBLE);
            textView.setText(employee.getId());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    employee.setChecked(!employee.isChecked());
                    absent.setVisibility(employee.isChecked() ? View.VISIBLE : View.GONE);
                    present.setVisibility(employee.isChecked() ? View.GONE : View.VISIBLE);

                    int count=getPresent().size();
                    listener.onClicked(count);
                }
            });
        }


    }

    public ArrayList<addatendance> getall() {
        ArrayList<addatendance> allAtendance = new ArrayList<>();
        for (int i = 0; i < attendance.size(); i++) {
            int pos=rollList.get(i);
            if (attendance.get(pos).isChecked()) {
                allAtendance.add(new addatendance(attendance.get(pos).getId(),"A"));
            }else {
                allAtendance.add(new addatendance(attendance.get(pos).getId(),"P"));
            }
        }
        return allAtendance;
    }



    public ArrayList<attendancemodel> getAbsent() {
        ArrayList<attendancemodel> absentList = new ArrayList<>();
        for (int i = 0; i < attendance.size(); i++) {
            int pos=rollList.get(i);
            if (attendance.get(pos).isChecked()) {
                absentList.add(attendance.get(pos));
            }
        }
        return absentList;
    }

    public ArrayList<attendancemodel> getPresent() {
        ArrayList<attendancemodel> PresentList = new ArrayList<>();
        for (int i = 0; i < attendance.size(); i++) {
            int pos=rollList.get(i);
            if (!(attendance.get(pos).isChecked())) {
                PresentList.add(attendance.get(pos));
            }
        }
        return PresentList;
    }

    public interface onClickedItem{
        void onClicked(int count);
    }

}
