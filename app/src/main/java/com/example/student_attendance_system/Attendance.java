package com.example.student_attendance_system;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.example.student_attendance_system.Model.addatendance;
import com.example.student_attendance_system.Model.attendancemodel;
import com.example.student_attendance_system.adapter.AttendanceAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class Attendance extends AppCompatActivity implements AttendanceAdapter.onClickedItem{

    RecyclerView studentlistRVL;
    Map<Integer,attendancemodel> arrayList;
    List<Integer> rollList;
    ArrayList<addatendance> addAttendanceList;


    Calendar calendar;
    String CDate;
    int SRollNO = 0, ERollNo = 0;

    TextView DateTV,presentc,totalc,absentc;
    Button button;
    AttendanceAdapter adapter;

    String Uname,course,year,sub,Schedule,time,formattedDate;

    DatabaseReference reference= FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);

        studentlistRVL = findViewById(R.id.studentlistRVL);
        GridLayoutManager layoutManager = new GridLayoutManager(Attendance.this, 4);
        studentlistRVL.setLayoutManager(layoutManager);

        Toolbar toolbar = findViewById(R.id.attendanceTbar);
        setSupportActionBar(toolbar);

        button = findViewById(R.id.button);

        calendar = Calendar.getInstance();

        addAttendanceList = new ArrayList<>();

        DateTV = findViewById(R.id.DateTV);
        presentc=findViewById(R.id.tx_pcount);
        totalc=findViewById(R.id.tx_tcount);
        absentc=findViewById(R.id.tx_acount);

        presentc.setVisibility(View.GONE);
        totalc.setVisibility(View.GONE);
        absentc.setVisibility(View.GONE);


        Date currentDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        String getCurrentDate = dateFormat.format(currentDate);
        DateTV.setText(getCurrentDate);
        CDate = DateTV.getText().toString();


        /*createList(true);*/
        /*check(true,CDate);*/
        checkExi(CDate);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Saveattendance();
            }
        });


    }

    private void checkExi(String cd) {

        int id = getIntent().getIntExtra("id", 0);
        String course = getIntent().getStringExtra("course");
        String year = getIntent().getStringExtra("year");
        String sub = getIntent().getStringExtra("sub");
        String schedule = getIntent().getStringExtra("Schedule");

        ProgressDialog progressDialog = new ProgressDialog(Attendance.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();


        String Uname = getSharedPreferences("LoginData", 0).getString("uname", "");

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);

                try {
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url("http://testproject.life/Projects/GPKSASystem/SAS_checkatendance.php?year="+year+"&Adate="+cd+"&course="+course+"&sub="+sub+"&schedule="+schedule+"&Tname="+Uname+"")
                            //.url("http://tsm.ecssofttech.com/Library/api/SAS_checkatendance.php?year="+year+"&Adate="+cd+"&course="+course+"&sub="+sub+"&schedule="+schedule+"&Tname="+Uname+"")
                            .build();
                    Response response = client.newCall(request).execute();
                    String strResponse = response.body().string();
                    Log.e("re",strResponse);

                    if (strResponse.equals("nonexists")) {

                        progressDialog.dismiss();

                        /*Intent in = new Intent(Attendance.this, Attendance.class);
                        in.putExtra("id", id);
                        in.putExtra("course", course);
                        in.putExtra("year", year);
                        in.putExtra("sub", sub);
                        in.putExtra("Schedule", schedule);
                        in.putExtra("VerifyS", "nonexists");
                        Attendance.this.startActivity(in);*/

                        check(true,cd,"nonexists");

                    } else if (strResponse.equals("exists")){

                        /*Intent in = new Intent(Attendance.this, Attendance.class);
                        in.putExtra("id", id);
                        in.putExtra("course", course);
                        in.putExtra("year", year);
                        in.putExtra("sub", sub);
                        in.putExtra("Schedule", schedule);
                        in.putExtra("VerifyS", "exists");
                        Attendance.this.startActivity(in);*/

                        check(true,cd,"exists");
                        progressDialog.dismiss();

                    }else {

                        Toast.makeText(Attendance.this, "Something went wrong try later", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(Attendance.this, "Something went wrong try later", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }
        });
    }

    private void showDatePickerDialog(TextView textView) {

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        // Update the calendar with the selected date
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.DAY_OF_MONTH, day);

                        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                        String formattedDate = dateFormat.format(calendar.getTime());
                        textView.setText(formattedDate);


                        /*check(true,DateTV.getText().toString());*/
                        checkExi(DateTV.getText().toString());

                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        // Show the DatePickerDialog
        datePickerDialog.show();
    }

    private void Saveattendance() {


        Uname = getSharedPreferences("LoginData", 0).getString("uname", "");

        course = getIntent().getStringExtra("course");
        year = getIntent().getStringExtra("year");
        sub = getIntent().getStringExtra("sub");
        Schedule = getIntent().getStringExtra("Schedule");
        String cd = DateTV.getText().toString();

        DateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy");
        DateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy");


        try {
            Date date = inputFormat.parse(cd);
            formattedDate = outputFormat.format(date);

            String[] str=Schedule.split(",");
            time=str[1];

        } catch (ParseException e) {
            e.printStackTrace();

        }


        /*Date currentDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");*/


        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait..");
        progressDialog.show();
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {


                for (addatendance attendance : adapter.getall()) {
                    String RollNO =  attendance.getRollno();
                    String Status =  attendance.getStatus();
                    Log.e("attendance_date","cd="+cd);
                    Log.e("attendance_date","schedul="+Schedule);

                    saveAttendanceToFirebase(RollNO,Status,formattedDate,time);


                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                    StrictMode.setThreadPolicy(policy);

                    try {
                        OkHttpClient client = new OkHttpClient();
                        Request request = new Request.Builder()
                                .url("http://testproject.life/Projects/GPKSASystem/SAS_addAttendance.php?rollno="+RollNO+"&course="+course+"&subject="+sub+"&TUsername="+Uname+"&status="+Status+"&date="+cd+"&year="+year+"&schedule="+Schedule+"")
                                .build();

                        Response response = client.newCall(request).execute();
                        String stringResponse = response.body().string();


                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }
                progressDialog.dismiss();

            }
        });


    }

    private void saveAttendanceToFirebase(String roll,String status,String cd,String time) {

            reference.child(Uname).child(course).child(year).child(sub).child(time).child(roll).child(cd).setValue(status);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.attendancemenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (R.id.action_absent == item.getItemId()) {

            createList(false);
        } else if (R.id.action_present == item.getItemId()) {

            createList(true);
        } else if (R.id.action_chooseDate == item.getItemId()){
            showDatePickerDialog(DateTV);
        }else {
            return super.onOptionsItemSelected(item);
        }

        return true;
    }


    private void createList(boolean action) {


        arrayList = new HashMap<>();
        rollList=new ArrayList<>();

        if (arrayList.size()>0) {
            arrayList.clear();
        }

        int Lid = getIntent().getIntExtra("id", 0);

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait..");
        progressDialog.show();
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {


                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);

                try {
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url("http://testproject.life/Projects/GPKSASystem/SAS_getRange.php?id=" + Lid + "")
                            .build();

                    Response response = client.newCall(request).execute();
                    String stringResponse = response.body().string();

                    JSONArray jsonArray = new JSONArray(stringResponse);

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject o = jsonArray.getJSONObject(i);


                        SRollNO = Integer.parseInt(o.getString("sroll"));
                        ERollNo = Integer.parseInt(o.getString("eroll"));

                    }

                    for (int r = SRollNO - 1; r < ERollNo; r++) {
                        attendancemodel student = new attendancemodel();
                        student.setChecked(action);
                        student.setId(String.valueOf((r + 1)));
                        arrayList.put((r+1),student);
                        rollList.add(r+1);
                    }

                    Map<Integer,attendancemodel> treeMap=new HashMap<>(arrayList);
                    Collections.sort(rollList);
                    adapter = new AttendanceAdapter(Attendance.this, treeMap,rollList);
                    AttendanceAdapter.onClickedItem listener=new AttendanceAdapter.onClickedItem() {
                        @Override
                        public void onClicked(int count) {
                            //int size=adapter.getPresent().size();
                            Toast.makeText(Attendance.this, "size="+count, Toast.LENGTH_SHORT).show();

                            totalc.setText(rollList.size()+"");
                            presentc.setText(count+"");
                            absentc.setText(rollList.size()-count+"");
                            totalc.setVisibility(View.VISIBLE);
                            presentc.setVisibility(View.VISIBLE);
                            absentc.setVisibility(View.VISIBLE);
                        }
                    };
                    adapter.setCountListener(listener);
                    studentlistRVL.setAdapter(adapter);

                    progressDialog.dismiss();

                } catch (Exception e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                }


            }
        });

    }

    private void modifyList(String CDate) {

        button.setText("Update");

        arrayList = new HashMap<>();
        rollList=new ArrayList<>();
        if (arrayList.size()>0) {
            arrayList.clear();
        }


        /*Date currentDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String CDate = dateFormat.format(currentDate);*/

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait..");
        progressDialog.show();
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {

                String course  = getIntent().getStringExtra("course");
                String year = getIntent().getStringExtra("year");
                String sub = getIntent().getStringExtra("sub");
                String schedule = getIntent().getStringExtra("Schedule");

                String Uname = getSharedPreferences("LoginData", 0).getString("uname", "");

                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);

                try {
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url("http://testproject.life/Projects/GPKSASystem/SAS_getupdateAttendance.php?Adate="+CDate+"&course="+course+"&year="+year+"&schedule="+schedule+"&TUname="+Uname+"&subject="+sub+"")
                            .build();


                    Response response = client.newCall(request).execute();


                    String stringResponse = response.body().string();

                    JSONArray jsonArray = new JSONArray(stringResponse);

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject o = jsonArray.getJSONObject(i);

                        String Roll = o.getString("rollno");
                        String status = o.getString("status");

                        if (status.equals("P")){

                            attendancemodel student = new attendancemodel();
                            student.setChecked(true);
                            student.setId(Roll);
                            arrayList.put(Integer.valueOf(Roll),student);
                            rollList.add(Integer.valueOf(Roll));

                        }else if (status.equals("A")){

                            attendancemodel student = new attendancemodel();
                            student.setChecked(false);
                            student.setId(Roll);
                            arrayList.put(Integer.valueOf(Roll),student);
                            rollList.add(Integer.valueOf(Roll));

                        }

                    }

                    Map<Integer,attendancemodel> treeMap=new HashMap<>(arrayList);
                    Collections.sort(rollList);
                    adapter = new AttendanceAdapter(Attendance.this, treeMap,rollList);
                    AttendanceAdapter.onClickedItem listener=new AttendanceAdapter.onClickedItem() {
                        @Override
                        public void onClicked(int count) {
                            //int size=adapter.getPresent().size();
                            //Toast.makeText(Attendance.this, "size="+count, Toast.LENGTH_SHORT).show();
                            totalc.setText(""+rollList.size());
                            presentc.setText(""+count+"");
                            absentc.setText(rollList.size()-count+"");
                            totalc.setVisibility(View.VISIBLE);
                            presentc.setVisibility(View.VISIBLE);
                            absentc.setVisibility(View.VISIBLE);

                        }
                    };
                    adapter.setCountListener(listener);
                    studentlistRVL.setAdapter(adapter);

                    //adapter.notifyDataSetChanged();



                    progressDialog.dismiss();

                } catch (Exception e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                }


            }
        });

    }


    private void check(boolean action,String atDate,String strResponse) {

        /*String strResponse = getIntent().getStringExtra("VerifyS");*/

        if (strResponse.equals("nonexists")) {


            createList(action);

        } else if (strResponse.equals("exists")){

            modifyList(atDate);


        }else {

            createList(action);

        }
    }

    @Override
    public void onClicked(int count) {
        AttendanceAdapter.onClickedItem listener=new AttendanceAdapter.onClickedItem() {
            @Override
            public void onClicked(int count) {
                //int size=adapter.getPresent().size();
                Toast.makeText(Attendance.this, "size="+count, Toast.LENGTH_SHORT).show();
                totalc.setText(rollList.size()+"");
                presentc.setText(count+"");
                absentc.setText(rollList.size()-count+"");
                totalc.setVisibility(View.VISIBLE);
                presentc.setVisibility(View.VISIBLE);
                absentc.setVisibility(View.VISIBLE);
            }
        };
        adapter.setCountListener(listener);
    }
}