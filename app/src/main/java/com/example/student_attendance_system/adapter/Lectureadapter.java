package com.example.student_attendance_system.adapter;

import static java.security.AccessController.getContext;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.student_attendance_system.Attendance;
import com.example.student_attendance_system.AttendanceReportTeacher;
import com.example.student_attendance_system.CreateLecture;
import com.example.student_attendance_system.Model.LectureModel;
import com.example.student_attendance_system.MonthAttendanceReportActivity;
import com.example.student_attendance_system.R;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Lectureadapter extends RecyclerView.Adapter<Lectureadapter.Viewholder> {

    ArrayList<LectureModel> arrayList;
    Context context;

    public Lectureadapter(ArrayList<LectureModel> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public Lectureadapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Viewholder(LayoutInflater.from(context).inflate(R.layout.lecturecardlayout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Lectureadapter.Viewholder holder, @SuppressLint("RecyclerView") int position) {

        holder.more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.scheduleLayout.setVisibility(View.VISIBLE);
                holder.UpBtn.setVisibility(View.VISIBLE);
                holder.more.setVisibility(View.GONE);

            }
        });

        holder.UpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.scheduleLayout.setVisibility(View.GONE);
                holder.UpBtn.setVisibility(View.GONE);
                holder.more.setVisibility(View.VISIBLE);
            }
        });

        holder.Sub.setText(arrayList.get(position).getSName());
        holder.year.setText(arrayList.get(position).getSchedule());
        holder.type.setText(arrayList.get(position).getType());
        holder.dep.setText(arrayList.get(position).getDepartment());
        holder.div.setText(arrayList.get(position).getDivision());
        String[] str=arrayList.get(position).getSchedule().split(",");
        holder.schedule.setText(str[0]+", "+str[1]+", "+str[2]);

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LectureModel po = arrayList.get(position);

                /*check(po.getId(),po.getYear(),po.getDepartment(),po.getSName(),po.getSchedule());*/

                Intent in = new Intent(context, Attendance.class);
                in.putExtra("id", po.getId());
                in.putExtra("course", po.getDepartment());
                in.putExtra("year", po.getYear());
                in.putExtra("sub", po.getSName());
                in.putExtra("Schedule", po.getSchedule());
                context.startActivity(in);

            }
        });
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Alert!");
                builder.setMessage("Are you sure you want to delete ?");
                builder.setCancelable(false)
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                dialog.dismiss();
                                deleterecord(position);
                            }
                        })
                        .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();

            }
        });

        holder.view_month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LectureModel po = arrayList.get(position);

                Intent in = new Intent(context, MonthAttendanceReportActivity.class);
                in.putExtra("id", po.getId());
                in.putExtra("course", po.getDepartment());
                in.putExtra("year", po.getYear());
                in.putExtra("sub", po.getSName());
                in.putExtra("Schedule", po.getSchedule());
                context.startActivity(in);
            }
        });

        holder.chart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LectureModel po = arrayList.get(position);

                Intent in = new Intent(context, AttendanceReportTeacher.class);
                in.putExtra("id", po.getId());
                in.putExtra("course", po.getDepartment());
                in.putExtra("year", po.getYear());
                in.putExtra("sub", po.getSName());
                in.putExtra("Schedule", po.getSchedule());
                context.startActivity(in);
                //Toast.makeText(context, "in progress..", Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void deleterecord(int position) {

        int id = arrayList.get(position).getId();

        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);

                try {
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url("http://testproject.life/Projects/GPKSASystem/SASdeleteLecture.php?id=" + id + "")
                            .build();
                    Response response = client.newCall(request).execute();
                    String strResponse = response.body().string();

                    if (strResponse.equals("deleted")) {
                        Toast.makeText(context, "Record Deleted Successfully", Toast.LENGTH_SHORT).show();

                        arrayList.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeRemoved(position,getItemCount());

                        progressDialog.dismiss();
                    } else {
                        Toast.makeText(context, "Something went wrong try later", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }


                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(context, "Something went wrong try later", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }
        });




    }
    private void check(int id ,String year,String course,String sub,String schedule) {

        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        Date currentDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String CDate = dateFormat.format(currentDate);

        String Uname = context.getSharedPreferences("LoginData", 0).getString("uname", "");

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);

                try {
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url("http://tsm.ecssofttech.com/Library/api/SAS_checkatendance.php?year="+year+"&Adate="+CDate+"&course="+course+"&sub="+sub+"&schedule="+schedule+"&Tname="+Uname+"")
                            .build();
                    Response response = client.newCall(request).execute();
                    String strResponse = response.body().string();

                    if (strResponse.equals("nonexists")) {

                        progressDialog.dismiss();

                        Intent in = new Intent(context, Attendance.class);
                        in.putExtra("id", id);
                        in.putExtra("course", course);
                        in.putExtra("year", year);
                        in.putExtra("sub", sub);
                        in.putExtra("Schedule", schedule);
                        in.putExtra("VerifyS", "nonexists");
                        context.startActivity(in);

                    } else if (strResponse.equals("exists")){

                        Intent in = new Intent(context, Attendance.class);
                        in.putExtra("id", id);
                        in.putExtra("course", course);
                        in.putExtra("year", year);
                        in.putExtra("sub", sub);
                        in.putExtra("Schedule", schedule);
                        in.putExtra("VerifyS", "exists");
                        context.startActivity(in);
                        progressDialog.dismiss();

                    }else {

                        Toast.makeText(context, "Something went wrong try later", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(context, "Something went wrong try later", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }
        });




    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder {

        ImageButton more, view, chart, edit, delete, UpBtn,view_month;
        TextView Sub, year, type, dep, div, schedule;
        RelativeLayout scheduleLayout;

        public Viewholder(@NonNull View itemView) {
            super(itemView);

            scheduleLayout = itemView.findViewById(R.id.scheduleLayout);

            more = itemView.findViewById(R.id.DownBtn);
            UpBtn = itemView.findViewById(R.id.UpBtn);
            view = itemView.findViewById(R.id.viewatBtn);
            view_month=itemView.findViewById(R.id.view_att_month);
            chart = itemView.findViewById(R.id.GraphBtn);
            edit = itemView.findViewById(R.id.editBtn);
            delete = itemView.findViewById(R.id.DeleteBtn);

            Sub = itemView.findViewById(R.id.SubTv);
            year = itemView.findViewById(R.id.YearTv);
            type = itemView.findViewById(R.id.TypeTv);
            dep = itemView.findViewById(R.id.DepTv);
            div = itemView.findViewById(R.id.DivTv);
            schedule = itemView.findViewById(R.id.scheduleTV);


        }
    }
}
