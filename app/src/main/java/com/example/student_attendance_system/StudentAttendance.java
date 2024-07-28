package com.example.student_attendance_system;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Collections;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class StudentAttendance extends Fragment {
    private TextView idsubject, idteacher;

    Spinner idcourse, idyear;
    private ProgressDialog progressDialog;

    private ArrayList<String> courseList;
    private ArrayList<String> teacherNameList;
    private ArrayList<String> subjectList;
    private CardView btnnext;
    String uname;
    String pword;

    String selectedCourse, selectedYear;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_student_attendance, container, false);
        getActivity().setTitle("Student Attendance");

        progressDialog = new ProgressDialog(view.getContext());

        courseList = new ArrayList<>();
        teacherNameList = new ArrayList<>();
        subjectList = new ArrayList<>();


        idcourse = view.findViewById(R.id.idcourse);
        idyear = view.findViewById(R.id.idyear);
        //idsubject = view.findViewById(R.id.idsubject);
        //idteacher = view.findViewById(R.id.idteacher);
        btnnext = view.findViewById(R.id.btnnext);

        new GetDataAsync().execute();


        ArrayAdapter<CharSequence> yearAdapter = ArrayAdapter.createFromResource(requireContext(), R.array.year, android.R.layout.simple_spinner_item);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        idyear.setAdapter(yearAdapter);


        idcourse.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCourse = courseList.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        idyear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                selectedYear = idyear.getSelectedItem().toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        btnnext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (selectedYear.equals("Select Year") || selectedCourse.equals("Select Course")) {
                    Toast.makeText(getActivity(), "Please select All field", Toast.LENGTH_SHORT).show();
                } else {

                    getData2();
                }
            }
        });

        return view;
    }

    private class GetDataAsync extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage("Loading data...");
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            getData();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialog.dismiss();
        }
    }

    private void getData() {

        courseList.add("Select Course");
        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
/*
                    .url("http://tsm.ecssofttech.com/Library/api/SASystem_coursedata.php")
*/
                    .url(BaseURL.BASEURL+"SASystem_coursedata.php")
                    .build();

            Response response4 = client.newCall(request).execute();
            String responseString4 = response4.body().string();
            Log.d("Response", responseString4);

            JSONArray contacts = new JSONArray(responseString4);
            for (int i = 0; i < contacts.length(); i++) {
                JSONObject c = contacts.getJSONObject(i);

                String course = c.getString("courseName");
                if (!courseList.contains(course)) {
                    courseList.add(course);
                }

            }

            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    if (getActivity() != null) {

                        ArrayAdapter<String> courseA_adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, courseList);
                        courseA_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        idcourse.setAdapter(courseA_adapter);

                    }
                }
            });


        } catch (JSONException | IOException e) {
            e.printStackTrace();
            Log.e("Error", "Exception: " + e.getMessage());
        }
    }

    private void getData2() {

        progressDialog.show();
        String Uname = getActivity().getSharedPreferences("LoginData", Context.MODE_PRIVATE).getString("uname", "");

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                try {
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                    StrictMode.setThreadPolicy(policy);

                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url("http://tsm.ecssofttech.com/Library/api/SASystem_getAssignedTeacher.php?userName=" + Uname + "&course=" + selectedCourse + "&year=" + selectedYear + "")
                            .build();

                    Response response4 = client.newCall(request).execute();
                    String responseString4 = response4.body().string();
                    Log.d("Response", responseString4);

                    if (responseString4.equals("notassign")) {
                        Toast.makeText(getActivity(), "You are not assign to this subject or Year", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();

                    } else if (responseString4.equals("Fail")) {
                        Toast.makeText(getActivity(), "Something went wrong try later fail", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    } else if (responseString4.equals("Missing Parameters.")) {
                        Toast.makeText(getActivity(), "Something went wrong try later missing parameter", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    } else {
                        Intent intent = new Intent(getActivity(), CreateLecture.class);
                        intent.putExtra("course", selectedCourse);
                        intent.putExtra("year", selectedYear);
                        startActivity(intent);
                        progressDialog.dismiss();


                    }


                } catch (Exception e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                    Log.e("Error", "Exception: " + e.getMessage());
                }
            }
        });

    }
}

