package com.example.student_attendance_system;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.student_attendance_system.Model.LectureModel;
import com.example.student_attendance_system.adapter.Lectureadapter;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class StudentDashboard extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawerLayout;

    String course,year;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_dashboard);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new DashboradFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_dashboard);
        }

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new DashboradFragment()).commit();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.nav_dashboard) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new DashboradFragment()).commit();
        }
         /*else if (item.getItemId() == R.id.nav_totalPresenty)
        {
            String uname = getIntent().getStringExtra("uname");
            String pword = getIntent().getStringExtra("pword");
            System.out.println(uname);
            Bundle bundle = new Bundle();
            bundle.putString("uname1", uname);
            bundle.putString("pword1", pword);

            TotalPresenty totalPresenty = new TotalPresenty();
            totalPresenty.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, totalPresenty).commit();
        }*/
        else if (item.getItemId() == R.id.nav_logout) {
            Toast.makeText(this, "Logout..!", Toast.LENGTH_SHORT).show();
            finishAffinity();
            Intent intent = new Intent(StudentDashboard.this, Login.class);
            startActivity(intent);
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void getData() {

        course = getIntent().getStringExtra("course");
        year = getIntent().getStringExtra("year");


        ProgressDialog progressDialog = new ProgressDialog(StudentDashboard.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please wait..");
        progressDialog.show();

        String Uname = getSharedPreferences("LoginData", 0).getString("uname", "");

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url("http://www.testproject.life/Projects/GPKSASystem/SASAlllectures.php?TUname="+Uname+"&YEAR="+year+"&course="+course+"")
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

/*
                    arrayList.add(new LectureModel(id,SubjectName, Department, Division, SelectType, Year, RollNumbers, Schedule));
*/
                    progressDialog.dismiss();
                }

                /*adapter = new Lectureadapter(arrayList, StudentDashboard.this);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();*/

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

}
