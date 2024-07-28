package com.example.student_attendance_system;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
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

public class SubjectFragment extends Fragment {

    private Spinner idcourse, idyear;
    private EditText subjectcode, subjectname;
    private CardView btnlogin;
    ProgressDialog progressDialog;
    ArrayList<String> list;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_subject, container, false);

        getActivity().setTitle("Subject Master ");

        progressDialog = new ProgressDialog(view.getContext());

        idcourse = view.findViewById(R.id.idcourse);
        idyear = view.findViewById(R.id.idyear);
        subjectcode = view.findViewById(R.id.subjectcode);
        subjectname = view.findViewById(R.id.subjectname);
        btnlogin = view.findViewById(R.id.btnlogin);

        new load().execute();

        ArrayAdapter<CharSequence> Tadapter = ArrayAdapter.createFromResource(requireContext(), R.array.year, android.R.layout.simple_spinner_item);
        Tadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        idyear.setAdapter(Tadapter);

        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String courseName = idcourse.getSelectedItem().toString().trim();
                String year = idyear.getSelectedItem().toString().trim();
                String subjectCode = subjectcode.getText().toString().trim();
                String subjectName = subjectname.getText().toString().trim();

                if (courseName.equals("Select Course")) {
                    Toast.makeText(requireContext(), "Please select your course", Toast.LENGTH_SHORT).show();
                    return;
                } else if (year.equals("Select Year")) {
                    Toast.makeText(requireContext(), "Please select your year", Toast.LENGTH_SHORT).show();
                    return;
                } else if (subjectCode.isEmpty()) {
                    Toast.makeText(requireContext(), "Please enter student Name", Toast.LENGTH_SHORT).show();
                    return;
                } else if (subjectName.isEmpty()) {
                    Toast.makeText(requireContext(), "Please enter email", Toast.LENGTH_SHORT).show();
                    return;
                }  /*else if (mobile.length() != 10) {
                    Toast.makeText(requireContext(), "Please enter a valid 10-digit mobile number", Toast.LENGTH_SHORT).show();
                    return;
                }*/
                new SubjectFragmentTask().execute(courseName, year, subjectCode, subjectName);
            }
        });
        return view;
    }
    //start here
    private void getData () {
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
        }
    }
    public class load extends AsyncTask<Void,Void,Void>{

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
//end here
    private class SubjectFragmentTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String courseName = params[0];
            String year = params[1];
            String subjectCode = params[2];
            String subjectName = params[3];
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url("http://testproject.life/Projects/GPKSASystem/SASystem_subjectmaster.php?courseName=" + courseName + "&year=" + year + "&subjectCode=" + subjectCode + "&subjectName=" + subjectName)
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
                if (result.equalsIgnoreCase("Success.")) {
                    Toast.makeText(requireContext(), "Saved Successfully.", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(requireContext(), " failed. Please try again later.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(requireContext(), "Network error. Please check your connection.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
