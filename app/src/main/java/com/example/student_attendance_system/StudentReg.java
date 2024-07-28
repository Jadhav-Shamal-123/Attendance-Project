package com.example.student_attendance_system;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

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

public class StudentReg extends Fragment {
    private Spinner idcourse, idyear;
    private EditText studentname, username, Password,PnumberEt;
    private CardView btnlogin;
    ArrayList<String> list;
    ArrayList<Integer> rollNo;
    ProgressDialog progressDialog;



    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_student_reg, container, false);
        progressDialog = new ProgressDialog(getContext());

        idcourse = view.findViewById(R.id.idcourse);
        idyear = view.findViewById(R.id.idyear);
        studentname = view.findViewById(R.id.studentname);
        username = view.findViewById(R.id.userName);
        Password = view.findViewById(R.id.Password);
        btnlogin = view.findViewById(R.id.btnlogin);
        PnumberEt = view.findViewById(R.id.PnumberEt);


        view.findViewById(R.id.ImportExcelBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), RegisterNewStudent.class));
            }
        });

        new load().execute();

        ArrayAdapter<CharSequence> Tadapter = ArrayAdapter.createFromResource(getContext(), R.array.year, android.R.layout.simple_spinner_item);
        Tadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        idyear.setAdapter(Tadapter);
        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {




                String course = idcourse.getSelectedItem().toString().trim();
                String year = idyear.getSelectedItem().toString().trim();
                String studentName = studentname.getText().toString().trim();
                String userName = username.getText().toString().trim();
                String Pnumber = PnumberEt.getText().toString().trim();
                String password = Password.getText().toString().trim();
                if (course.equals("Select Course")) {
                    Toast.makeText(getContext(), "Please select your course", Toast.LENGTH_SHORT).show();
                    return;
                } else if (year.equals("Select Year")) {
                    Toast.makeText(getContext(), "Please select your year", Toast.LENGTH_SHORT).show();
                    return;
                } else if (studentName.isEmpty()) {
                    Toast.makeText(getContext(), "Please enter student Name", Toast.LENGTH_SHORT).show();
                    return;
                } else if (userName.isEmpty()) {
                    Toast.makeText(getContext(), "Please enter email", Toast.LENGTH_SHORT).show();
                    return;
                } else if (password.isEmpty()) {
                    Toast.makeText(getContext(), "Please enter password number", Toast.LENGTH_SHORT).show();
                    return;
                } else if (Pnumber.length() != 10) {
                    Toast.makeText(getContext(), "Please enter valid parent mobile number", Toast.LENGTH_SHORT).show();
                    return;
                }


                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
                rollNo=new ArrayList<>();
                rollNo.clear();
                try {
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url("http://testproject.life/Projects/GPKSASystem/getStudent.php?courseName=" + course + "&year=" + year)
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
                addStudentDataToFirebase(course,year,studentName,userName);
                new StudentRegistrationTask().execute(course, year, studentName, userName, password,Pnumber);
            }
        });
        return view;
    }
    //getData start here

    private void getData() {

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        list=new ArrayList<>();
        list.add("Select Course");
        OkHttpClient client4 = new OkHttpClient();
        Request request4 = new Request.Builder().url(BaseURL.BASEURL+"SASystem_coursedata.php").build();
        try {
            Response response4 = client4.newCall(request4).execute();
            String responseString4 = response4.body().string();
            JSONArray contacts = new JSONArray(responseString4);
            for (int i = 0; i < contacts.length(); i++) {
                JSONObject c = contacts.getJSONObject(i);
                String course=c.getString("courseName").toString();
                list.add(course);
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, list);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            idcourse.setAdapter(adapter);

            Log.d("list", list.toString());
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
    }


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
            String Pnumber = params[5];
            int rollno = rollNo.size()+1;
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url("http://testproject.life/Projects/GPKSASystem/SASystem_studentRegistration.php?studentName=" + studentName + "&userName=" + userName + "&password=" + password + "&course=" + course + "&year=" + year+"&rollno="+rollno+"&Pnumber="+Pnumber)
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
                Log.e("error",result.toString());
                if (result.equalsIgnoreCase("Registration Success.")) {

                    Toast.makeText(getContext(), "Student Registration Successfully.", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(getContext(), TeacherDashboard.class);
                    startActivity(intent);

                } else {

                    Toast.makeText(getContext(), "Registration failed. Please try again later.", Toast.LENGTH_SHORT).show();

                }
            } else {

                Toast.makeText(getContext(), "Network error. Please check your connection.", Toast.LENGTH_SHORT).show();

            }
        }
    }
    private void addStudentDataToFirebase(String course, String year, String studentName, String userName) {
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference();
        reference.child("Students").child(course).child(year).child(userName).setValue(studentName);
    }
}