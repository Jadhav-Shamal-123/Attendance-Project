package com.example.student_attendance_system;

import static java.security.AccessController.getContext;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Presenty extends AppCompatActivity {
    RecyclerView recyclerView;
    PresentyAddpter presentyAdapter;
    List<PresentyModel> presentyModels;
    //ProgressBar progressBar;
    Button submitButton;
    int presentStudentCount;
    int absentStudentCount;
    String uname;
    String pword;
    String date;
    ProgressDialog progressDialog;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_presenty);
        recyclerView = findViewById(R.id.recview);
        progressDialog=new ProgressDialog(Presenty.this);

        presentyModels = new ArrayList<>();

        new FetchDataTask().execute();

        String course = getIntent().getStringExtra("course");
        String year = getIntent().getStringExtra("year");
        String subjectName = getIntent().getStringExtra("subjectName");
        String teacherName = getIntent().getStringExtra("teacherName");
        uname = getIntent().getStringExtra("username");
        pword = getIntent().getStringExtra("password");


        Date currentDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        date = dateFormat.format(currentDate);

        submitButton = findViewById(R.id.submitButton);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<PresentyModel> selectedItems = new ArrayList<>();
                presentStudentCount = 0;
                absentStudentCount = 0;
                for (PresentyModel model : presentyModels) {
                    if (model.isSelected()) {
                        selectedItems.add(model);
                        ++presentStudentCount;
                    }
                    if (!model.isSelected()) {
                        selectedItems.add(model);
                        ++absentStudentCount;
                    }
                }
                if (selectedItems.isEmpty()) {
                    Toast.makeText(Presenty.this, "No items selected.", Toast.LENGTH_SHORT).show();
                } else {
                    // Show a progress bar
                    //progressBar.setVisibility(View.VISIBLE);
                    progressDialog.show();
                    submitButton.setEnabled(false);

                    // Execute the background task
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                for (PresentyModel selectedModel : selectedItems) {
                                    int rollno = selectedModel.getRollNo();
                                    String studentName = selectedModel.getName();
                                    String status = selectedModel.getStatus();
                                    /*presentStudentCount=selectedModel.getPcount();
                                    absentStudentCount=selectedModel.getAcount();*/
                                    OkHttpClient client = new OkHttpClient();
                                    Request request = new Request.Builder()
                                            .url("http://tsm.ecssofttech.com/Library/api/SASystem_presenty.php?rollno=" + rollno +
                                                    "&course=" + course + "&year=" + year +
                                                    "&subject=" + subjectName + "&teacherName=" + teacherName +
                                                    "&studentName=" + studentName + "&status=" + status + "&date=" + date)
                                            .build();

                                    Response response = client.newCall(request).execute();
                                    String responseString = response.body().string();

                                    if (!responseString.equalsIgnoreCase("Presenty Success.")) {
                                        // Handle the failure here if needed
                                    }
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            } finally {
                                // Hide the progress bar
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressDialog.dismiss();
                                        //progressBar.setVisibility(View.GONE);
                                        submitButton.setEnabled(true);
                                        Toast.makeText(Presenty.this, "Selected data inserted", Toast.LENGTH_SHORT).show();

                                        Intent intent = new Intent(Presenty.this, Success.class);
                                        intent.putExtra("pcount", presentStudentCount);
                                        intent.putExtra("acount", absentStudentCount);
                                        intent.putExtra("disableCall", true);
                                        intent.putExtra("uname", uname);
                                        intent.putExtra("pword", pword);
                                        startActivity(intent);
                                    }
                                });
                            }
                        }
                    }).start();
                }
            }
        });
    }

    class FetchDataTask extends AsyncTask<Void, Void, List<PresentyModel>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (progressDialog != null) {
                progressDialog.setMessage("Please wait...");
                progressDialog.show();
                //progressBar.setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected List<PresentyModel> doInBackground(Void... voids) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            try {
                String courseName = getIntent().getStringExtra("course");
                String year = getIntent().getStringExtra("year");

                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url("http://tsm.ecssofttech.com/Library/api/getStudent.php?courseName=" + courseName + "&year=" + year)
                        .build();
                Response response = client.newCall(request).execute();

                if (response.code() == 200) {
                    String responseString = response.body().string();
                    JSONArray contacts = new JSONArray(responseString);
                    for (int i = 0; i < contacts.length(); i++) {
                        JSONObject c = contacts.getJSONObject(i);
                        PresentyModel presentyModel = new PresentyModel();
                        presentyModel.setName(c.getString("studentName").toString());
                        presentyModel.setRollNo(c.getInt("rollno"));
                        presentyModels.add(presentyModel);
                    }
                } else {
                    // Handle server errors (e.g., show an error message)
                    Log.e("Present", "Server response code: " + response.code());
                }
            } catch (JSONException | IOException e) {
                e.printStackTrace();
                Log.e("Present", "Error: " + e.getMessage());
            }
            return presentyModels;
        }

        @Override
        protected void onPostExecute(List<PresentyModel> result) {
            super.onPostExecute(result);
            if (progressDialog != null) {
                //progressBar.setVisibility(View.GONE);
                progressDialog.dismiss();
                Button submitButton = findViewById(R.id.submitButton);
                submitButton.setVisibility(View.VISIBLE);
            }
            if (result != null && !result.isEmpty()) {
                recyclerView.setLayoutManager(new LinearLayoutManager(Presenty.this));
                presentyAdapter = new PresentyAddpter(result, Presenty.this);
                recyclerView.setAdapter(presentyAdapter);
            } else {
                Toast.makeText(Presenty.this, "No data available", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(Presenty.this, TeacherDashboard.class);
        intent.putExtra("uname", uname);
        intent.putExtra("pword", pword);
        startActivity(intent);

    }
}
