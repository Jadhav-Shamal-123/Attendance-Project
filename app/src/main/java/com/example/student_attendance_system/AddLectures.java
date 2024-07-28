package com.example.student_attendance_system;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.student_attendance_system.Model.addatendance;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AddLectures extends AppCompatActivity {

    EditText RollStartET, RollEndET;

    Spinner DaySpinner, Sub_spinner;
    TextView SubjectTV, ETimeTV,STimeTV, DepET, DivET;
    ImageButton yearforwardbtn, yearbackbtn;
    RadioGroup typegroup;

    TextView RollRangeTV;

    int SRollNO = 0, ERollNo = 0;

    String ScheduleDay, selectedType = "Lecture", InCourse, InYear, selectedSub;

    ArrayList<String> SubjectList;

    String[] dayList = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_lectures);

        findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        InCourse = getIntent().getStringExtra("course");
        InYear = getIntent().getStringExtra("year");

        yearforwardbtn = findViewById(R.id.subforwardbtn);
        yearbackbtn = findViewById(R.id.subbackbtn);
        DepET = findViewById(R.id.DepET);
        DivET = findViewById(R.id.DivET);
        typegroup = findViewById(R.id.typegroup);
        RollStartET = findViewById(R.id.RollStartET);
        RollEndET = findViewById(R.id.RollEndET);
        STimeTV = findViewById(R.id.sTimeTV);
        ETimeTV = findViewById(R.id.eTimeTV);
        DaySpinner = findViewById(R.id.DaySpinner);
        SubjectTV = findViewById(R.id.SubjectTV);
        Sub_spinner = findViewById(R.id.Sub_spinner);
        RollRangeTV = findViewById(R.id.RollRangeTV);

        SubjectList = new ArrayList<>();

        DivET.setText(InYear);
        DepET.setText(InCourse);

        getData();


        Sub_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedSub = SubjectList.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(AddLectures.this, android.R.layout.simple_spinner_item, dayList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        DaySpinner.setAdapter(arrayAdapter);

        DaySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ScheduleDay = dayList[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        long currentTimeMillis = System.currentTimeMillis();
        Date currentTime = new Date(currentTimeMillis);
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        String formattedTime = sdf.format(currentTime);
        STimeTV.setText(formattedTime);
        ETimeTV.setText(formattedTime);

        /*typegroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                RadioButton radioButton = findViewById(checkedId);

                selectedType = radioButton.getText().toString();
            }
        });*/

        STimeTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectTime("start");
            }
        });

        ETimeTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectTime("end");
            }
        });


        findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData();
            }
        });


    }

    private void getData() {

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait..");
        progressDialog.show();
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {

                SubjectList.add("Select subject");
                String Uname = getSharedPreferences("LoginData", 0).getString("uname", "");

                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);

                try {
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url("http://testproject.life/Projects/GPKSASystem/SASystem_getAssignedTeacher.php?userName=" + Uname + "&course=" + InCourse + "&year=" + InYear + "")
                            .build();

                    Response response = client.newCall(request).execute();
                    String stringResponse = response.body().string();

                    JSONArray jsonArray = new JSONArray(stringResponse);

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject o = jsonArray.getJSONObject(i);

                        if (o.has("lroll") && o.has("hroll")) {
                            SRollNO = o.getInt("lroll");
                            ERollNo = o.getInt("hroll");

                        }
                        if (o.has("subjectName")) {

                            String sub = o.getString("subjectName");
                            if (!SubjectList.contains(sub)) {
                                SubjectList.add(sub);
                            }
                        }

                        RollRangeTV.setText("Student RollNo \n(Range: "+SRollNO+" - "+ERollNo+")");

                        progressDialog.dismiss();

                    }


                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(AddLectures.this, android.R.layout.simple_spinner_item, SubjectList);
                    arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    Sub_spinner.setAdapter(arrayAdapter);
                    progressDialog.dismiss();

                } catch (Exception e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                }


            }
        });


    }

    private void saveData() {

        ProgressDialog progressDialog = new ProgressDialog(AddLectures.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please wait..");
        progressDialog.show();


        String Dep = DepET.getText().toString().trim();
        String Div = DivET.getText().toString().trim();

        String sr = String.valueOf(SRollNO);
        String er = String.valueOf(ERollNo);

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String formattedDate = dateFormat.format(calendar.getTime());

        String TUname = getSharedPreferences("LoginData", 0).getString("uname", "");
        //String RollRange = RollStartET.getText().toString().trim() + "-" + RollEndET.getText().toString().trim();
        String RollRange = sr + "-" + er;
        String ScheduleTimeDay = formattedDate +","+STimeTV.getText().toString().trim()+","+ETimeTV.getText().toString().trim();

        if (Dep.isEmpty() || Div.isEmpty() /*|| RollStartET.getText().toString().isEmpty() || RollEndET.getText().toString().isEmpty()*/) {
            Toast.makeText(this, "All field required..", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        } else if (selectedType.isEmpty()) {
            Toast.makeText(this, "select Type", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        } else if (selectedSub.equals("Select subject")) {
            Toast.makeText(this, "Please Select Subject", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        } /*else if (Integer.parseInt(RollStartET.getText().toString().trim())>Integer.parseInt(RollEndET.getText().toString().trim() )){
            Toast.makeText(AddLectures.this, "Please entered valid roll number", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }*/else {

            //int ERoll = Integer.parseInt(RollEndET.getText().toString().trim());
            int ERoll = Integer.parseInt(er);

            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                    StrictMode.setThreadPolicy(policy);
                    try {
                        OkHttpClient client = new OkHttpClient();
                        Request request = new Request.Builder()
                                .url("http://www.testproject.life/Projects/GPKSASystem/SAScreateLecture.php?eRoll=" + ERoll + "&userName=" + TUname + "&SelectType=" + selectedType + "&subName=" + selectedSub + "&Dep=" + InCourse + "&Div=" + InYear + "&TUname=" + TUname + "&RollRange=" + RollRange + "&ScheduleTimeDay=" + ScheduleTimeDay + "&Year=" + InYear + "")
                                .build();

                        Response response = client.newCall(request).execute();
                        String responseString = response.body().string();

                        if (responseString.equals("inserted")) {


                            Saveattendance(ERoll,formattedDate,InCourse,InYear,selectedSub,ScheduleTimeDay);

                            progressDialog.dismiss();
                        } else if (responseString.equals("exists")){
                            Toast.makeText(AddLectures.this, "Lecture already exists", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();

                        }else if (responseString.equals("invalidRange")) {
                            Toast.makeText(AddLectures.this, "Please entered valid roll number\n Roll No Range: " + SRollNO + " - " + ERollNo , Toast.LENGTH_SHORT).show();

                            progressDialog.dismiss();
                        } else {
                            progressDialog.dismiss();
                            Log.e("addLecture", "Server response code: " + responseString);
                            Toast.makeText(AddLectures.this, "Something went wrong try later...", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        progressDialog.dismiss();
                        e.printStackTrace();
                        Log.e("addLecture", "Error: " + e.getMessage());
                        Toast.makeText(AddLectures.this, "Something went wrong try later...", Toast.LENGTH_SHORT).show();
                    }
                }
            });


        }


    }

    @SuppressLint("StaticFieldLeak")
    private void Saveattendance(int ER, String cd, String course, String year, String sub, String Schedule) {


        String Uname = getSharedPreferences("LoginData", 0).getString("uname", "");


        /*Date currentDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");*/



        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait..");
        progressDialog.show();
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {

                for (int i=SRollNO;i<=ERollNo;i++) {
                    String RollNO = String.valueOf(i);

                    StringRequest request = new StringRequest(com.android.volley.Request.Method.GET, "http://testproject.life/Projects/GPKSASystem/SAS_addAttendance.php?rollno="+RollNO+"&course="+course+"&subject="+sub+"&TUsername="+Uname+"&status="+"A"+"&date="+cd+"&year="+year+"&schedule="+Schedule, new com.android.volley.Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                        }
                    }, new com.android.volley.Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    });

                    RequestQueue requestQueue = Volley.newRequestQueue(AddLectures.this);
                    requestQueue.add(request);

                    /*StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                    StrictMode.setThreadPolicy(policy);

                    try {
                        OkHttpClient client = new OkHttpClient();
                        Request request = new Request.Builder()
                                //.url("http://testproject.life/Projects/GPKSASystem/SAS_addAttendance.php?rollno="+RollNO+"&course="+course+"&subject="+sub+"&TUsername="+Uname+"&status="+"A"+"&date="+cd+"&year="+year+"&schedule="+Schedule+"")
                                .build();

                        Response response = client.newCall(request).execute();
                        String stringResponse = response.body().string();


                    } catch (Exception e) {
                        e.printStackTrace();
                    }*/


                }

                Toast.makeText(AddLectures.this, "Added successfully", Toast.LENGTH_SHORT).show();
                Intent in = new Intent(AddLectures.this, CreateLecture.class);
                in.putExtra("course", InCourse);
                in.putExtra("year", InYear);
                startActivity(in);
                finish();
                progressDialog.dismiss();

            }
        });


        /*ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait..");
        progressDialog.show();

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                for (int i = SRollNO; i <= ERollNo; i++) {
                    String RollNO = String.valueOf(i);

                    try {
                        OkHttpClient client = new OkHttpClient();
                        Request request = new Request.Builder()
                                .url("http://testproject.life/Projects/GPKSASystem/SAS_addAttendance.php?rollno=" + RollNO + "&course=" + course + "&subject=" + sub + "&TUsername=" + Uname + "&status=" + "A" + "&date=" + cd + "&year=" + year + "&schedule=" + Schedule + "")
                                .build();

                        Response response = client.newCall(request).execute();
                        String stringResponse = response.body().string();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                Toast.makeText(AddLectures.this, "Added successfully", Toast.LENGTH_SHORT).show();
                Intent in = new Intent(AddLectures.this, CreateLecture.class);
                in.putExtra("course", InCourse);
                in.putExtra("year", InYear);
                startActivity(in);
                finish();
                progressDialog.dismiss();
            }
        }.execute();
*/

    }

    private void selectTime(String from) {
        final Calendar c = Calendar.getInstance();
        int mHour = c.get(Calendar.HOUR_OF_DAY);
        int mMinute = c.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {

                        // Convert 24-hour format to 12-hour format
                        String amPm;
                        if (hourOfDay >= 12) {
                            amPm = "PM";
                            if (hourOfDay > 12) {
                                hourOfDay -= 12;
                            }
                        } else {
                            amPm = "AM";
                            if (hourOfDay == 0) {
                                hourOfDay = 12;
                            }
                        }

                        if(from.equals("start")){
                            STimeTV.setText(String.format(Locale.getDefault(), "%02d:%02d %s", hourOfDay, minute, amPm));
                        }else{
                            ETimeTV.setText(String.format(Locale.getDefault(), "%02d:%02d %s", hourOfDay, minute, amPm));
                        }


                    }
                }, mHour, mMinute, false);
        timePickerDialog.show();


    }
}