
package com.example.student_attendance_system;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TotalTeacherWithSubject extends AppCompatActivity {
    RecyclerView recyclerView;
    TeacherAssignedSubject teacherAssignedSubject;
    List<TeachersubjectassignModel> teachersubjectassignModels;
    ProgressBar dynamicProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_total_teacher_with_subject);

        recyclerView = findViewById(R.id.recview);
        dynamicProgressBar = findViewById(R.id.dynamicProgressBar);
        teachersubjectassignModels = new ArrayList<>();

        // Start the data fetching process
        new GetDataTask().execute();
    }

    class GetDataTask extends AsyncTask<Void, Void, List<TeachersubjectassignModel>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dynamicProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<TeachersubjectassignModel> doInBackground(Void... voids) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            List<TeachersubjectassignModel> models = new ArrayList<>();
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url("http://testproject.life/Projects/GPKSASystem/SASystem_getallTeachersInfo.php")
                    .build();
            try {
                Response response = client.newCall(request).execute();
                String responseString = response.body().string();
                JSONArray contacts = new JSONArray(responseString);
                for (int i = 0; i < contacts.length(); i++) {
                    JSONObject c = contacts.getJSONObject(i);
                    TeachersubjectassignModel teachersubjectassignModel = new TeachersubjectassignModel();
                    teachersubjectassignModel.setTeacherName(c.getString("teacherName"));
                    teachersubjectassignModel.setCourse(c.getString("courseName"));
                    teachersubjectassignModel.setYear(c.getString("year"));
                    teachersubjectassignModel.setSubjectName(c.getString("subjectName"));
                    teachersubjectassignModel.setSubjectCode(c.getString("subjectCode"));
                    teachersubjectassignModel.setId(c.getInt("Id"));
                    models.add(teachersubjectassignModel);
                }
            } catch (JSONException | IOException e) {
                e.printStackTrace();
            }
            return models;
        }

        @Override
        protected void onPostExecute(List<TeachersubjectassignModel> models) {
            super.onPostExecute(models);
            dynamicProgressBar.setVisibility(View.GONE);

            if (models.isEmpty()) {
                // Handle the case when data fetching fails
                Toast.makeText(TotalTeacherWithSubject.this, "Error fetching data", Toast.LENGTH_LONG).show();
            } else {
                teacherAssignedSubject = new TeacherAssignedSubject(models, TotalTeacherWithSubject.this);
                recyclerView.setLayoutManager(new LinearLayoutManager(TotalTeacherWithSubject.this));
                recyclerView.setAdapter(teacherAssignedSubject);
            }
        }
    }
}
// TotalTeacherWithSubject.java
// Import statements

/*
public class TotalTeacherWithSubject extends AppCompatActivity {
    private RecyclerView recyclerView;
    private TeacherAssignedSubject teacherAssignedSubject;
    private List<TeachersubjectassignModel> teachersubjectassignModels;
    private ProgressBar dynamicProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_total_teacher_with_subject);

        recyclerView = findViewById(R.id.recview);
        dynamicProgressBar = findViewById(R.id.dynamicProgressBar);
        teachersubjectassignModels = new ArrayList<>();

        // Start the data fetching process
        new GetDataTask().execute();
    }

    class GetDataTask extends AsyncTask<Void, Void, List<TeachersubjectassignModel>> {
        private static final String TAG = "GetDataTask"; // Add a tag for logging

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dynamicProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<TeachersubjectassignModel> doInBackground(Void... voids) {
            List<TeachersubjectassignModel> models = new ArrayList<>();

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url("http://tsm.ecssofttech.com/Library/api/SASystem_getAssignedTeacher.php")
                    .build();

            try {
                Response response = client.newCall(request).execute();

                if (!response.isSuccessful()) {
                    // Handle HTTP error (log error message)
                    Log.e(TAG, "HTTP Error - Code: " + response.code() + ", Message: " + response.message());
                    return models; // Return an empty list
                }

                String responseString = response.body().string();
                JSONArray contacts = new JSONArray(responseString);

                for (int i = 0; i < contacts.length(); i++) {
                    JSONObject c = contacts.getJSONObject(i);
                    TeachersubjectassignModel teachersubjectassignModel = new TeachersubjectassignModel();
                    teachersubjectassignModel.setTeacherName(c.getString("teacherName"));
                    teachersubjectassignModel.setCourse(c.getString("courseName"));
                    teachersubjectassignModel.setYear(c.getString("year"));
                    teachersubjectassignModel.setSubjectName(c.getString("subjectName"));
                    teachersubjectassignModel.setSubjectCode(c.getString("subjectCode"));
                    teachersubjectassignModel.setId(c.getInt("Id"));
                    models.add(teachersubjectassignModel);
                }
            } catch (IOException e) {
                e.printStackTrace();
                // Handle network error (log error message)
                Log.e(TAG, "Network Error: " + e.getMessage());
            } catch (JSONException e) {
                e.printStackTrace();
                // Handle JSON parsing error (log error message)
                Log.e(TAG, "JSON Parsing Error: " + e.getMessage());
            }

            return models;
        }

        @Override
        protected void onPostExecute(List<TeachersubjectassignModel> models) {
            super.onPostExecute(models);
            dynamicProgressBar.setVisibility(View.GONE);

            if (models.isEmpty()) {
                // Handle the case when data fetching fails
                Toast.makeText(TotalTeacherWithSubject.this, "Error fetching data", Toast.LENGTH_LONG).show();
            } else {
                teacherAssignedSubject = new TeacherAssignedSubject(models, TotalTeacherWithSubject.this);
                recyclerView.setLayoutManager(new LinearLayoutManager(TotalTeacherWithSubject.this));
                recyclerView.setAdapter(teacherAssignedSubject);
            }
        }
    }

}
*/
