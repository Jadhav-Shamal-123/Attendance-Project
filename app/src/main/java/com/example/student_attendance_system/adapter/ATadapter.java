package com.example.student_attendance_system.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.student_attendance_system.AttendanceReportTeacher;
import com.example.student_attendance_system.Model.AtReportlistmodel;
import com.example.student_attendance_system.R;

import java.util.ArrayList;

public class ATadapter extends RecyclerView.Adapter<ATadapter.Viewholder> {

    Context context;
    ArrayList<AtReportlistmodel> arrayList;

    public ATadapter(Context context, ArrayList<AtReportlistmodel> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public ATadapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Viewholder(LayoutInflater.from(context).inflate(R.layout.atreportcard,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ATadapter.Viewholder holder, int position) {

        holder.RollNoTV.setText(arrayList.get(position).getRollNo());
        holder.StNameTV.setText(arrayList.get(position).getSName());
        holder.ACTV.setText(arrayList.get(position).getACount());
        holder.PCTV.setText(arrayList.get(position).getPCount());
        holder.PercentageTV.setText(arrayList.get(position).getPercentage()+" %");
        holder.RemarkTV.setText(arrayList.get(position).getRemark());

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder{

        TextView RollNoTV, StNameTV, ACTV, PCTV, PercentageTV, RemarkTV;
        public Viewholder(@NonNull View itemView) {
            super(itemView);

            RollNoTV = itemView.findViewById(R.id.RollNoTV);
            StNameTV = itemView.findViewById(R.id.StNameTV);
            ACTV = itemView.findViewById(R.id.ACTV);
            PCTV = itemView.findViewById(R.id.PCTV);
            PercentageTV = itemView.findViewById(R.id.PercentageTV);
            RemarkTV = itemView.findViewById(R.id.RemarkTV);
        }
    }
}
