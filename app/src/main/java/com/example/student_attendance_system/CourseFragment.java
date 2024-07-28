package com.example.student_attendance_system;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
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
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import java.io.IOException;
import java.util.ArrayList;

public class CourseFragment extends Fragment {
    private CardView btnlogin;
    private EditText text1;
    ArrayList<String> list;
    ProgressDialog progressDialog;

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_course, container, false);

        getActivity().setTitle("Course Master");

        progressDialog = new ProgressDialog(view.getContext());

        text1 = view.findViewById(R.id.text);
        text1.requestFocus();
        btnlogin = view.findViewById(R.id.btnlogin);

        //load class called
        //new load().execute();

        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String courseName = text1.getText().toString().trim();
                if (courseName.isEmpty()) {
                    Toast.makeText(requireContext(), "Please Enter Your Course Name", Toast.LENGTH_SHORT).show();
                }
                else {
                    new InsertCourseTask().execute(courseName);
                }
            }
        });
        return view;
    }
    private class InsertCourseTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String courseName = params[0];

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(BaseURL.BASEURL+"SASystemInsert.php?courseName=" + courseName)
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
                Log.e("error",result);
                if (result.equalsIgnoreCase("Exist")) {
                    Toast.makeText(requireContext(), "Course Name already exist please try another name.", Toast.LENGTH_SHORT).show();
                } else if (result.equalsIgnoreCase("Course name inserted successfully.")) {
                    Toast.makeText(requireContext(), "Course name inserted successfully.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(requireContext(), "Insertion failed. Please try again later.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(requireContext(), "Network error. Please check your connection.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
