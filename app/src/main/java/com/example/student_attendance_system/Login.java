package com.example.student_attendance_system;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.OkHttpClient;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class Login extends AppCompatActivity {
    public static final String PREFS_NAME = "MyPrefsFile";
    private static final String ADMIN_LOGGED_IN_KEY = "adminLoggedIn";

    EditText uname, pword;
    RadioGroup group;
    RadioButton admin, student, teacher;
    CardView btnlogin;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        uname = findViewById(R.id.username);
        uname.requestFocus();
        pword = findViewById(R.id.password);
        btnlogin = findViewById(R.id.btnlogin);
        group = findViewById(R.id.group);
        admin = findViewById(R.id.admin);
        student = findViewById(R.id.student);
        teacher = findViewById(R.id.teacher);

        // Check if the user is already logged in
        checkLoggedInUser();

        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int selectedId = group.getCheckedRadioButtonId();

                if (selectedId == admin.getId()) {
                    handleAdminLogin();
                } else if (selectedId == student.getId()) {
                    handleStudentLogin();
                } else if (selectedId == teacher.getId()) {
                    handleTeacherLogin();
                }
            }


        });
    }

        private void handleAdminLogin() {
            String userName = uname.getText().toString().trim();
            String password = pword.getText().toString().trim();

            if (!"a".equals(userName) || !"a".equals(password)) {
                Toast.makeText(Login.this, "Invalid username or password", Toast.LENGTH_SHORT).show();
            } else {
                setAdminLoggedIn(true);
                Intent intent = new Intent(Login.this, AdminDashbord.class);
                startActivity(intent);
            }
        }

        private void handleStudentLogin() {
            String userName = uname.getText().toString().trim();
            String password = pword.getText().toString().trim();

            if (userName.isEmpty()) {
                Toast.makeText(Login.this, "Please enter UserName", Toast.LENGTH_SHORT).show();
            } else if (password.isEmpty()) {
                Toast.makeText(Login.this, "Please enter Password", Toast.LENGTH_SHORT).show();
            } else {
                performLogin(userName, password, "Student", StudentDashboard.class);
            }
        }

        private void handleTeacherLogin() {
            String userName = uname.getText().toString().trim();
            String password = pword.getText().toString().trim();

            if (userName.isEmpty()) {
                Toast.makeText(Login.this, "Please enter UserName", Toast.LENGTH_SHORT).show();
            } else if (password.isEmpty()) {
                Toast.makeText(Login.this, "Please enter Password", Toast.LENGTH_SHORT).show();
            } else {
                performLogin(userName, password, "Teacher", TeacherDashboard.class);
            }
        }

        private void performLogin(String userName, String password, String role, Class<?> dashboardClass) {
            final ProgressDialog progressDialog = new ProgressDialog(Login.this);
            progressDialog.setMessage("Logging in...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            new Handler().post(new Runnable() {
                @Override
                public void run() {


                    StringRequest request = new StringRequest(Request.Method.POST, "http://www.testproject.life/Projects/GPKSASystem/GPK_SASLogin.php", new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            try {

                                JSONObject jsonResponse = new JSONObject(response);

                                boolean success = jsonResponse.getBoolean("success");

                                if (success) {

                                    saveUserDetails(userName, password, role);

                                    Intent intent = new Intent(Login.this, dashboardClass);
                                    intent.putExtra("uname", userName);
                                    intent.putExtra("pword", password);
                                    startActivity(intent);
                                    Toast.makeText(Login.this, "Login Success", Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();

                                } else {
                                    progressDialog.dismiss();
                                    String errorMessage = jsonResponse.getString("message");

                                    Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                progressDialog.dismiss();
                            }

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            progressDialog.dismiss();
                                    Toast.makeText(getApplicationContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }){

                        @Override
                        protected Map<String, String> getParams() {
                            Map<String, String> params = new HashMap<>();
                            params.put("userName", userName);
                            params.put("password", password);
                            params.put("role", role);
                            return params;
                        }
                    };


                    Volley.newRequestQueue(Login.this).add(request);



                }
            });


            /*new Thread(new Runnable() {
                @Override
                public void run() {
                    // Network request and login logic

                    // Assuming responseString is the server response
                    String responseString = "success";

                    if ("success".equalsIgnoreCase(responseString)) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // Save user details in SharedPreferences
                                saveUserDetails(userName, password, role);

                                Intent intent = new Intent(Login.this, dashboardClass);
                                intent.putExtra("uname", userName);
                                intent.putExtra("pword", password);
                                startActivity(intent);
                                Toast.makeText(Login.this, "Login Success", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.dismiss();
                                Toast.makeText(Login.this, "Invalid UserName/Password", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }).start();*/
        }

        private void saveUserDetails(String userName, String password, String role) {
            SharedPreferences.Editor editor = getSharedPreferences("LoginData", 0).edit();
            editor.putString("uname", userName);
            editor.putString("pword", password);
            editor.putString("role", role);
            editor.apply();
        }

    private void setAdminLoggedIn(boolean loggedIn) {
        SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit();
        editor.putBoolean(ADMIN_LOGGED_IN_KEY, loggedIn);
        editor.apply();
    }

    private boolean isAdminLoggedIn() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(ADMIN_LOGGED_IN_KEY, false);
    }

    private void checkLoggedInUser() {
        String checklogin = getSharedPreferences("LoginData", 0).getString("role", "");
        String SaveUsername = getSharedPreferences("LoginData", 0).getString("uname", "");
        String SavePassword = getSharedPreferences("LoginData", 0).getString("pword", "");

        if (!checklogin.isEmpty() && !SaveUsername.isEmpty() && !SavePassword.isEmpty()) {
            if ("Teacher".equals(checklogin)) {
                Intent intent = new Intent(Login.this, TeacherDashboard.class);
                intent.putExtra("uname", SaveUsername);
                intent.putExtra("pword", SavePassword);
                startActivity(intent);
            } else if ("Student".equals(checklogin)) {
                Intent intent = new Intent(Login.this, StudentDashboard.class);
                intent.putExtra("uname", SaveUsername);
                intent.putExtra("pword", SavePassword);
                startActivity(intent);
            }
        }
    }
}
