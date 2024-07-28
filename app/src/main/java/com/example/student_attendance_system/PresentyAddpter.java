package com.example.student_attendance_system;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PresentyAddpter extends RecyclerView.Adapter<PresentyAddpter.ViewHolder>
{
    LayoutInflater inflater;
    List<PresentyModel> presentyModels;
    Context context;

    public PresentyAddpter(List<PresentyModel> presentyModels, Context context)
    {
        this.presentyModels = presentyModels;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }
    public void SetFilteredList(List<PresentyModel> filteredList){
        this.presentyModels = filteredList;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = inflater.inflate(R.layout.studentattendance,parent,false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position)
    {
        PresentyModel temp = presentyModels.get(position);
        holder.rollno.setText(String.valueOf(temp.getRollNo())); // Corrected order
        holder.name.setText(temp.getName());
        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context,Presenty.class);
                intent.putExtra("studentName", temp.getName());
                intent.putExtra("rollno", temp.getRollNo());
            }
        });
        temp.setStatus("");
        if (!holder.checkBox.isChecked())
        {
            temp.setStatus("A");
        }
        // Handle checkbox click event
        holder.checkBox.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View view) {

                temp.setSelected(holder.checkBox.isChecked());
                temp.setStatus("");
                if (holder.checkBox.isChecked())
                {
                    temp.setStatus("P");
                }
                if (!holder.checkBox.isChecked())
                {
                    temp.setStatus("A");
                }
            }
        });
    }
    @Override
    public int getItemCount() {
        return presentyModels.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView rollno,name;
        CardView card;
        CheckBox checkBox;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            rollno =itemView.findViewById(R.id.rollno);
            name =itemView.findViewById(R.id.name);
            card = itemView.findViewById(R.id.card1);
            checkBox=itemView.findViewById(R.id.checkBox);

        }
    }
}
