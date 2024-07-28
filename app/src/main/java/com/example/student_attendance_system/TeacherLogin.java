package com.example.student_attendance_system;

import android.os.Bundle;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;
import java.io.IOException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TeacherLogin extends Fragment
{
    EditText username,Password;
    CardView login;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_teacher_login, container, false);
        getActivity().setTitle("Teacher Login");
        username=view.findViewById(R.id.email);
        Password=view.findViewById(R.id.mobile);
        login=view.findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userName=username.getText().toString().trim();
                String password=Password.getText().toString().trim();
                 if (userName.isEmpty())
                {
                    Toast.makeText(requireContext(), "Please enter UserName/email", Toast.LENGTH_SHORT).show();
                }
                else if (password.isEmpty())
                {
                    Toast.makeText(requireContext(), "Please enter mobileNo/Password", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    StrictMode.ThreadPolicy policy=new StrictMode.ThreadPolicy.Builder().permitAll().build();
                    StrictMode.setThreadPolicy(policy);
                    OkHttpClient client=new OkHttpClient();

                    Request request= new Request.Builder().url("http://tsm.ecssofttech.com/Library/api/SASystem_teacherLogin.php?userName=" + userName+"&password="+password)
                            .build();

                    try {
                        Response response = client.newCall(request).execute();
                        String responseString = response.body().string();

                        if (responseString.equalsIgnoreCase("success")) {
                            Toast.makeText(requireContext(), "Login Success", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(requireContext(), "Invalid MobileNo/Password", Toast.LENGTH_SHORT).show();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(requireContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
        return view;
    }
}