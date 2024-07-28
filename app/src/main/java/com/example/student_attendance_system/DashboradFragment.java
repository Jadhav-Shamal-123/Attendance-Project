package com.example.student_attendance_system;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.student_attendance_system.Model.MarkModel;
import com.example.student_attendance_system.adapter.MarkAtAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import okhttp3.OkHttpClient;

public class DashboradFragment extends Fragment {

    RecyclerView recyclerView;

    ArrayList<MarkModel> arrayList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getActivity().setTitle("Home ");
        View view = inflater.inflate(R.layout.fragment_dashborad, container, false);


        arrayList = new ArrayList<>();
        recyclerView = view.findViewById(R.id.markAtRVL);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(false);

        getData(view);


        return view;
    }

    private void getData(View view) {

        String uname = getActivity().getSharedPreferences("LoginData", 0).getString("uname", "e");
        String upass = getActivity().getSharedPreferences("LoginData", 0).getString("pword", "e");

        new Handler().post(new Runnable() {

            @Override
            public void run() {

                StringRequest request = new StringRequest(Request.Method.GET, "http://testproject.life/Projects/GPKSASystem/GPK_getLecturesForStudent.php?uname=" + uname + "&upass=" + upass, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONArray jsonArray = new JSONArray(response);

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject o = jsonArray.getJSONObject(i);

                                String rollno = o.getString("rollno");
                                String courseName = o.getString("courseName");
                                String year = o.getString("year");
                                String SubjectName = o.getString("SubjectName");
                                String Teacher = o.getString("Teacher");
                                String studentName = o.getString("studentName");
                                String Schedule = o.getString("Schedule");

                                boolean isGivenDateTimeInFuture = isDateTimeInFuture(Schedule);

                                if (isGivenDateTimeInFuture) {

                                    /*if (!checkExi(Schedule, courseName, year, SubjectName, Schedule, view,uname)) {
                                    }*/

                                        arrayList.add(new MarkModel(rollno, courseName, year, SubjectName, Teacher, studentName, Schedule));

                                }


                            }

                            recyclerView.setAdapter(new MarkAtAdapter(getActivity(), arrayList));
                            recyclerView.getAdapter().notifyDataSetChanged();


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });


                RequestQueue queue = Volley.newRequestQueue(view.getContext());
                queue.add(request);

            }
        });


    }


    private boolean checkExi(String cd, String course, String year, String sub, String schedule, View view,String Uname) {

        ProgressDialog progressDialog = new ProgressDialog(view.getContext());
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();



        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try {
            OkHttpClient client = new OkHttpClient();
            okhttp3.Request request = new okhttp3.Request.Builder()
                    .url("http://testproject.life/Projects/GPKSASystem/SAS_checkatendance.php?year=" + year + "&Adate=" + cd + "&course=" + course + "&sub=" + sub + "&schedule=" + schedule + "&Tname=" + Uname + "")
                    .build();
            okhttp3.Response response = client.newCall(request).execute();
            String strResponse = response.body().string();
            Log.e("re", strResponse);

            if (strResponse.equals("nonexists")) {

                progressDialog.dismiss();


                return false;


            } else if (strResponse.equals("exists")) {


                progressDialog.dismiss();
                return true;

            } else {

                progressDialog.dismiss();
                return false;
            }


        } catch (Exception e) {
            e.printStackTrace();
            progressDialog.dismiss();
            return false;
        }


    }


    private boolean isDateTimeInFuture(String dateTimeString) {
        // Parse the given string into a Date object
        Date givenDate = parseDateString(dateTimeString);

        if (givenDate != null) {
            // Get the current date and time
            Date currentDate = new Date();

            // Compare the dates
            return givenDate.after(currentDate);
        } else {
            Log.e("DateTimeComparison", "Invalid date format");
            return false;
        }
    }

    @SuppressLint("SimpleDateFormat")
    private Date parseDateString(String dateString) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy,hh:mm a", Locale.getDefault());
        try {
            return sdf.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}