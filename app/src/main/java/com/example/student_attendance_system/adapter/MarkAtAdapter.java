package com.example.student_attendance_system.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.student_attendance_system.Attendance;
import com.example.student_attendance_system.Model.MarkModel;
import com.example.student_attendance_system.Model.addatendance;
import com.example.student_attendance_system.R;

import java.util.ArrayList;

import okhttp3.OkHttpClient;

public class MarkAtAdapter extends RecyclerView.Adapter<MarkAtAdapter.Viewholder> {


    Context context;
    ArrayList<MarkModel> arrayList;

    public MarkAtAdapter(Context context, ArrayList<MarkModel> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public MarkAtAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Viewholder(LayoutInflater.from(context).inflate(R.layout.markatlayout,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MarkAtAdapter.Viewholder holder, int position) {

        MarkModel model = arrayList.get(position);

        holder.Sub.setText(model.getSubjectName());
        holder.year.setText(model.getYear());
        holder.Ltime.setText(model.getSchedule());
        holder.div.setText(model.getYear());
        holder.dep.setText(model.getCourseName());

        holder.markBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Saveattendance(holder.getAdapterPosition(),v);
            }
        });

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }


    private void Saveattendance(int position,View view) {


        String Uname = context.getSharedPreferences("LoginData", 0).getString("uname", "e");

        MarkModel mo = arrayList.get(position);
        String course = mo.getCourseName();
        String year = mo.getYear();
        String sub = mo.getSubjectName();
        String Schedule = mo.getSchedule();
        String RollNO = mo.getRollno();
        String Tuname = mo.getTeacher();

        String[] DT = mo.getSchedule().split(",");
        String cd = DT[0];

        /*StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url("http://testproject.life/Projects/GPKSASystem/SAS_addAttendance.php?rollno="+RollNO+"&course="+course+"&subject="+sub+"&TUsername="+Uname+"&status="+"P"+"&date="+cd+"&year="+year+"&schedule="+Schedule+"")
                    .build();

            Response response = client.newCall(request).execute();
            String stringResponse = response.body().string();

            Log.e("response",stringResponse);

            arrayList.remove(position);
            notifyItemRemoved(position);
            Toast.makeText(context, "Attendance Added Successfully", Toast.LENGTH_SHORT).show();


        } catch (Exception e) {
            e.printStackTrace();
        }

        */


        StringRequest request = new StringRequest(Request.Method.GET, "http://testproject.life/Projects/GPKSASystem/SAS_addAttendance.php?rollno="+RollNO+"&course="+course+"&subject="+sub+"&TUsername="+Tuname+"&status="+"P"+"&date="+cd+"&year="+year+"&schedule="+Schedule+"", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


                Log.e("res",response);

                if (response.equals("inserted")||response.equals("updated")){
                    arrayList.remove(position);
                    notifyItemRemoved(position);
                    Toast.makeText(context, "Attendance Added Successfully", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(context, "Try Again..", Toast.LENGTH_SHORT).show();
                }



            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e("error",error.toString());
            }
        });

        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(request);


    }

    public class Viewholder extends RecyclerView.ViewHolder{

        TextView Sub, year, Ltime, dep, div, schedule;

        Button markBtn;
        public Viewholder(@NonNull View itemView) {
            super(itemView);

            Sub = itemView.findViewById(R.id.SubTv);
            year = itemView.findViewById(R.id.YearTv);
            Ltime = itemView.findViewById(R.id.TimeTV);
            dep = itemView.findViewById(R.id.DepTv);
            div = itemView.findViewById(R.id.DivTv);

            markBtn  = itemView.findViewById(R.id.markBtn);
        }
    }
}
