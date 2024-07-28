package com.example.student_attendance_system;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class StudentRegistration extends AppCompatActivity
{

    private Spinner idcourse, idyear;
    private EditText studentname, username, Password;
    private CardView btnlogin;
    ArrayList<String> list;
    ArrayList<Integer> rollNo;
    ProgressDialog progressDialog;

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_registration);

        progressDialog = new ProgressDialog(StudentRegistration.this);

        idcourse = findViewById(R.id.idcourse);
        idyear = findViewById(R.id.idyear);
        studentname = findViewById(R.id.studentname);
        username = findViewById(R.id.userName);
        Password = findViewById(R.id.Password);
        btnlogin = findViewById(R.id.btnlogin);

        new load().execute();

        ArrayAdapter<CharSequence> Tadapter = ArrayAdapter.createFromResource(StudentRegistration.this, R.array.year, android.R.layout.simple_spinner_item);
        Tadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        idyear.setAdapter(Tadapter);

        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String course = idcourse.getSelectedItem().toString().trim();
                String year = idyear.getSelectedItem().toString().trim();
                String studentName = studentname.getText().toString().trim();
                String userName = username.getText().toString().trim();
                String password = Password.getText().toString().trim();
                if (course.equals("Select Course")) {
                    Toast.makeText(StudentRegistration.this, "Please select your course", Toast.LENGTH_SHORT).show();
                    return;
                } else if (year.equals("Select Year")) {
                    Toast.makeText(StudentRegistration.this, "Please select your year", Toast.LENGTH_SHORT).show();
                    return;
                } else if (studentName.isEmpty()) {
                    Toast.makeText(StudentRegistration.this, "Please enter student Name", Toast.LENGTH_SHORT).show();
                    return;
                } else if (userName.isEmpty()) {
                    Toast.makeText(StudentRegistration.this, "Please enter email", Toast.LENGTH_SHORT).show();
                    return;
                } else if (password.isEmpty()) {
                    Toast.makeText(StudentRegistration.this, "Please enter mobile number", Toast.LENGTH_SHORT).show();
                    return;
                }
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
                rollNo=new ArrayList<>();
                rollNo.clear();
                try {
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url("http://tsm.ecssofttech.com/Library/api/getStudent.php?courseName=" + course + "&year=" + year)
                            .build();

                    Response response = client.newCall(request).execute();

                    if (response.code() == 200) {
                        String responseString = response.body().string();
                        JSONArray contacts = new JSONArray(responseString);

                        for (int i = 0; i < contacts.length(); i++) {
                            JSONObject c = contacts.getJSONObject(i);
                           rollNo.add(c.getInt("rollno"));
                        }
                    } else {
                        // Handle server errors (e.g., show an error message)
                        Log.e("Present", "Server response code: " + response.code());
                    }
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                    Log.e("Present", "Error: " + e.getMessage());
                }
                new StudentRegistrationTask().execute(course, year, studentName, userName, password);
            }
        });
    }
    //getData start here
    private void getData() {

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        list=new ArrayList<>();
        list.add("Select Course");
        OkHttpClient client4 = new OkHttpClient();
        Request request4 = new Request.Builder().url("http://tsm.ecssofttech.com/Library/api/SASystem_coursedata.php").build();
        try {
            Response response4 = client4.newCall(request4).execute();
            String responseString4 = response4.body().string();
            JSONArray contacts = new JSONArray(responseString4);
            for (int i = 0; i < contacts.length(); i++) {
                JSONObject c = contacts.getJSONObject(i);
                String course=c.getString("courseName").toString();
                list.add(course);
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(StudentRegistration.this, android.R.layout.simple_spinner_item, list);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            idcourse.setAdapter(adapter);

            Log.d("list", list.toString());
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
    }

    // getData ends here
//load class start here
    public class load extends AsyncTask<Void,Void,Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage("Please wait..");
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    getData();
                }
            });
            return null;
        }
        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            progressDialog.dismiss();
        }
    }
    // load class ends here
    private class StudentRegistrationTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String course = params[0];
            String year = params[1];
            String studentName = params[2];
            String userName = params[3];
            String password = params[4];
            int rollno = rollNo.size()+1;

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url("http://tsm.ecssofttech.com/Library/api/SASystem_studentRegistration.php?studentName=" + studentName + "&userName=" + userName + "&password=" + password + "&course=" + course + "&year=" + year+"&rollno="+rollno+"")
                    .build();
            try {


                Response response = client.newCall(request).execute();
                return response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
                return "Error: " + e.getMessage();
            }

        }
        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                if (result.equalsIgnoreCase("Registration Success.")) {
                    Toast.makeText(StudentRegistration.this, "Student Registration Successfully.", Toast.LENGTH_SHORT).show();
                    /*Intent intent = new Intent(StudentRegistration.this, StudentDashboard.class);
                    startActivity(intent);*/
                } else {
                    Toast.makeText(StudentRegistration.this, "Registration failed. Please try again later.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(StudentRegistration.this, "Network error. Please check your connection.", Toast.LENGTH_SHORT).show();
            }
        }
    }



}