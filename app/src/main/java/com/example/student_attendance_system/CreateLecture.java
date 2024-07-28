package com.example.student_attendance_system;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.student_attendance_system.Model.LectureModel;
import com.example.student_attendance_system.adapter.Lectureadapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CreateLecture extends AppCompatActivity {

    ArrayList<LectureModel> arrayList;
    Lectureadapter adapter;
    RecyclerView recyclerView;

    String course ;
    String year ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_lecture);

        recyclerView = findViewById(R.id.subjectlistRVL);
        arrayList = new ArrayList<>();

        recyclerView.setLayoutManager(new LinearLayoutManager(CreateLecture.this));

        getData();

        findViewById(R.id.createLecture).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent in = new Intent(CreateLecture.this, AddLectures.class);
                in.putExtra("course", course);
                in.putExtra("year", year);
                startActivity(in);
                //startActivityForResult(in,101);
            }
        });


    }

    @SuppressLint("NotifyDataSetChanged")
    private void getData() {

        course = getIntent().getStringExtra("course");
        year = getIntent().getStringExtra("year");


        ProgressDialog progressDialog = new ProgressDialog(CreateLecture.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please wait..");
        progressDialog.show();

        String Uname = getSharedPreferences("LoginData", 0).getString("uname", "");

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url("http://www.testproject.life/Projects/GPKSASystem/SASAlllectures.php?TUname="+Uname+"&YEAR="+year+"&course="+course)
                    .build();

            Response response = client.newCall(request).execute();
            String responseString = response.body().string();

            if (!responseString.equals("Not found")) {


                JSONArray jsonArray = new JSONArray(responseString);

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject o = jsonArray.getJSONObject(i);

                    int id = o.getInt("id");

                    String SubjectName = o.getString("SubjectName");
                    String Department = o.getString("Department");
                    String Division = o.getString("Division");
                    String SelectType = o.getString("SelectType");
                    String Year = o.getString("Year");
                    String RollNumbers = o.getString("RollNumbers");
                    String Schedule = o.getString("Schedule");

                    arrayList.add(new LectureModel(id,SubjectName, Department, Division, SelectType, Year, RollNumbers, Schedule));
                    progressDialog.dismiss();
                }

                adapter = new Lectureadapter(arrayList, CreateLecture.this);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();

            }else {
                Toast.makeText(this, "No Lecture Found", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }

        } catch (Exception e) {
            progressDialog.dismiss();
            e.printStackTrace();
            Log.e("addLecture", "Error: " + e.getMessage());
            Toast.makeText(this, "Something went wrong try later...", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(CreateLecture.this, TeacherDashboard.class));
        finishAffinity();
    }
}