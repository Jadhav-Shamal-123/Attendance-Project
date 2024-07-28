package com.example.student_attendance_system.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.student_attendance_system.Model.NewStudentRegModel;
import com.example.student_attendance_system.R;

import java.util.ArrayList;

public class NewStudentRegAdapter extends RecyclerView.Adapter<NewStudentRegAdapter.Viewholder> {

    ArrayList<NewStudentRegModel> arrayList;

    public NewStudentRegAdapter(ArrayList<NewStudentRegModel> arrayList) {
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public NewStudentRegAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Viewholder(LayoutInflater.from(parent.getContext()).inflate(R.layout.student_regcard,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull NewStudentRegAdapter.Viewholder holder, int position) {

        NewStudentRegModel po = arrayList.get(position);

        holder.srollTV.setText("Roll No.: "+po.getRollNo());
        holder.snameTV.setText("Student Name: "+po.getSName());
        holder.CoursedetailTV.setText("Course : "+po.getCourseName()+" ("+po.getYear()+")");
        holder.PNumberTV.setText("Parent Number : "+po.getPNumber());



    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder {

        TextView srollTV,snameTV,CoursedetailTV,PNumberTV;
        public Viewholder(@NonNull View itemView) {
            super(itemView);

            srollTV = itemView.findViewById(R.id.srollTV);
            snameTV = itemView.findViewById(R.id.snameTV);
            CoursedetailTV = itemView.findViewById(R.id.CoursedetailTV);
            PNumberTV = itemView.findViewById(R.id.PNumberTV);
        }
    }
}
