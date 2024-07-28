package com.example.student_attendance_system;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
public class TeacherSubjectFragment extends Fragment {
    private Spinner idcourse, idyear, idsubject,teachernameSpinner;
    private String teacherName;
    private TextView subjectcode;
    private CardView btnlogin;
    ProgressDialog progressDialog;
    ArrayList<String> list;
    ArrayList<String> AllTecherList;
    ArrayList<String> subject;
    ArrayList<String> subjectCodeList;
    private String selectedSubject ;
    private String selectedSubjectCode ;
    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_teacher_subject, container, false);

        getActivity().setTitle("Teacher Subject");

        progressDialog = new ProgressDialog(view.getContext());

        idcourse = view.findViewById(R.id.idcourse);
        idyear = view.findViewById(R.id.idyear);
        idsubject = view.findViewById(R.id.idsubject);
        subjectcode = view.findViewById(R.id.subjectcode);
        teachernameSpinner = view.findViewById(R.id.teacherName);
        btnlogin = view.findViewById(R.id.btnlogin);

        new LoadDataTask().execute();

        subjectCodeList = new ArrayList<>();




        idsubject.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String newSelectedSubject = idsubject.getSelectedItem().toString();
                    // Change the text color of the selected item in idsubject Spinner
                if (!"Select Subject".equals(newSelectedSubject)) {
                    ((TextView) parentView.getChildAt(0)).setTextColor(getResources().getColor(R.color.blue)); // Change to your desired color
                    ((TextView) parentView.getChildAt(0)).setTextSize(17); // Change to your desired size
                } else {
                    ((TextView) parentView.getChildAt(0)).setTextColor(Color.BLACK); // Reset to the default color
                }
                subjectcode.setText("");
                if (!"Select Subject".equals(newSelectedSubject)) {
                    // Check if the selected subject has changed
                    if (!newSelectedSubject.equals(selectedSubject)) {
                        selectedSubject = newSelectedSubject;
                        // Get the subject code from the subjectCodeList based on the selected subject
                        int index = subject.indexOf(selectedSubject);
                        if (index >= 0 && index < subjectCodeList.size()) {
                            selectedSubjectCode = subjectCodeList.get(index);
                            subjectcode.setText(selectedSubjectCode);

                            subjectcode.setTextColor(getResources().getColor(R.color.blue));
                            subjectcode.setTextSize(17);
                        }
                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing here
            }
        });
        idcourse.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedCourse = idcourse.getSelectedItem().toString();
                if (!"Select Course".equals(selectedCourse)) {
                    ((TextView) parentView.getChildAt(0)).setTextColor(getResources().getColor(R.color.blue)); // Change to your desired color
                    ((TextView) parentView.getChildAt(0)).setTextSize(17); // Change to your desired size
                } else {
                    ((TextView) parentView.getChildAt(0)).setTextColor(Color.BLACK); // Reset to the default color
                }
                if (subjectCodeList != null) {
                    subjectCodeList.clear();
                }
                if (subject != null) {
                    subject.clear();
                }
                if (!"Select Course".equals(selectedCourse)) {
                    // Call getData2() to fetch subjects when both course and year are selected
                    ArrayAdapter<CharSequence> yearAdapter = ArrayAdapter.createFromResource(requireContext(), R.array.year, android.R.layout.simple_spinner_item);
                    yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    idyear.setAdapter(yearAdapter);
                    idyear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                            String selectedCourse = idcourse.getSelectedItem().toString();
                            String selectedYear = idyear.getSelectedItem().toString();
                            if (!"Select Year".equals(selectedYear)) {
                                ((TextView) parentView.getChildAt(0)).setTextColor(getResources().getColor(R.color.blue)); // Change to your desired color
                                ((TextView) parentView.getChildAt(0)).setTextSize(17); // Change to your desired size
                            } else {
                                ((TextView) parentView.getChildAt(0)).setTextColor(Color.BLACK); // Reset to the default color
                            }
                            if (position > 0) {
                                // Call getData2() to fetch subjects when both course and year are selected
                                getData2(selectedCourse, selectedYear);
                                setAllteacherSpinner(selectedCourse);
                                ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, subject);
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                idsubject.setAdapter(adapter);
                            }
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> parentView) {
                            // Do nothing here
                        }
                    });
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing here
            }
        });
        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String courseName = idcourse.getSelectedItem() != null ? idcourse.getSelectedItem().toString().trim() : "";
                String year = idyear.getSelectedItem() !=null ? idyear.getSelectedItem().toString().trim():"";
                String subjectName = idsubject.getSelectedItem() !=null ? idcourse.toString().trim():"";


                if ("Select Course".equals(courseName)) {
                    Toast.makeText(requireContext(), "Please select your course", Toast.LENGTH_SHORT).show();
                }
                else if ("Select Year".equals(year)) {
                    Toast.makeText(requireContext(), "Please select your year", Toast.LENGTH_SHORT).show();
                }
                else if (year.isEmpty()) {
                    Toast.makeText(requireContext(), "Please enter your year", Toast.LENGTH_SHORT).show();
                }
                else if (subjectName.isEmpty()) {
                    Toast.makeText(requireContext(), "Please enter subject name", Toast.LENGTH_SHORT).show();
                }
                else if (teacherName.isEmpty()) {
                    Toast.makeText(requireContext(), "Please Select Teacher Name", Toast.LENGTH_SHORT).show();
                }  else {
                    // Perform the network request in an AsyncTask
                    submit(teacherName, courseName, year, selectedSubject, selectedSubjectCode);
                }
            }
        });
        return view;
    }

    private void setAllteacherSpinner(String selectedCourse) {

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        AllTecherList = new ArrayList<>();
        AllTecherList.clear();
        AllTecherList.add("Select Teacher");
        OkHttpClient client4 = new OkHttpClient();
        Request request4 = new Request.Builder().url("http://testproject.life/Projects/GPKSASystem/SAS_getAllTeacher.php?course="+selectedCourse).build();
        try {
            Response response4 = client4.newCall(request4).execute();
            String responseString4 = response4.body().string();
            Log.e("error",responseString4);
            JSONArray contacts = new JSONArray(responseString4);
            for (int i = 0; i < contacts.length(); i++) {
                JSONObject c = contacts.getJSONObject(i);
                String t = c.getString("teacherName");
                AllTecherList.add(t);
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, AllTecherList);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            teachernameSpinner.setAdapter(adapter);

            teachernameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (position>0){
                        teacherName = AllTecherList.get(position);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });



        } catch (JSONException | IOException e) {
            e.printStackTrace();
            Log.e("TeacherSubjectFragment", "Error fetching Teacher data: " + e.getMessage());
            showToast("Error fetching course data. Please try again.");
        }




    }

    private void getData() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        list = new ArrayList<>();
        list.add("Select Course");
        OkHttpClient client4 = new OkHttpClient();
        Request request4 = new Request.Builder().url("http://testproject.life/Projects/GPKSASystem/SASystem_coursedata.php").build();
        try {
            Response response4 = client4.newCall(request4).execute();
            String responseString4 = response4.body().string();
            JSONArray contacts = new JSONArray(responseString4);
            for (int i = 0; i < contacts.length(); i++) {
                JSONObject c = contacts.getJSONObject(i);
                String course = c.getString("courseName").toString();
                list.add(course);
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, list);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            idcourse.setAdapter(adapter);
            Log.d("list", list.toString());
        } catch (JSONException | IOException e) {
            e.printStackTrace();
            Log.e("TeacherSubjectFragment", "Error fetching course data: " + e.getMessage());
            showToast("Error fetching course data. Please try again.");
        }
    }
    private void submit(String teacherName, String courseName, String year, String subjectName, String subjectCode) {

        ProgressDialog p = new ProgressDialog(requireContext());
        p.setMessage("Please wait");
        p.show();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        OkHttpClient client4 = new OkHttpClient();
        Request request4 = new Request.Builder()
                .url("http://testproject.life/Projects/GPKSASystem/assignsubject.php?teacherName="+teacherName+"&courseName="+courseName+"&year="+year+"&subjectName="+subjectName+"&subjectCode="+subjectCode+"")
                .build();
        try {
            Response response4 = client4.newCall(request4).execute();
            String responseString4 = response4.body().string();
            if (responseString4.equals("inserted successfully.")) {
                Toast.makeText(requireContext(), "Submit Successfully", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getContext(), AdminDashbord.class);
                startActivity(intent);
                p.dismiss();
            } else if (responseString4.equals("Teacher dose not available")) {
                Toast.makeText(requireContext(), "Teacher dose not available.please register this teacher name", Toast.LENGTH_SHORT).show();
                p.dismiss();
            } else {
                Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                p.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("TeacherSubjectFragment", "Error fetching subject data: " + e.getMessage());
            showToast("This year data is not available. Please try another.");
            p.dismiss();
        }
    }
    private void getData2(String selectedCourse, String selectedYear)
    {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        subject = new ArrayList<>();
        subjectCodeList.clear(); // Clear the subject code list before populating it
        subjectCodeList.add(""); // Add an empty item
        if (subject != null) {
            subject.clear();
        }
        subject.add("Select Subject");
        OkHttpClient client4 = new OkHttpClient();
        Request request4 = new Request.Builder()
                .url("http://testproject.life/Projects/GPKSASystem/SASystem_getsubject.php?courseName=" + selectedCourse + "&year=" + selectedYear)
                .build();
        try {
            Response response4 = client4.newCall(request4).execute();
            String responseString4 = response4.body().string();
            JSONArray contacts = new JSONArray(responseString4);
            for (int i = 0; i < contacts.length(); i++) {
                JSONObject c = contacts.getJSONObject(i);
                String subjectName = c.getString("subjectName").toString();
                subject.add(subjectName);
                String subjectCode = c.getString("subjectCode").toString();
                subjectCodeList.add(subjectCode); // Add subject code to the list
            }
        } catch (JSONException | IOException e) {
            e.printStackTrace();
            Log.e("TeacherSubjectFragment", "Error fetching subject data: " + e.getMessage());
            showToast("This year data is not available. Please try another.");
        }
    }
    private void showToast(final String message) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }
    private class InsertDataTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String teacherName = params[0];
            String courseName = params[1];
            String year = params[2];
            String subjectName = params[3];
            String subjectCode = params[4];
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    submit(teacherName,courseName,year,subjectName,subjectCode);
                }
            });
            return null;
        }
    }
    private class LoadDataTask extends AsyncTask<Void, Void, Void> {
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
}

