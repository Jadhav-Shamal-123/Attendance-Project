package com.example.student_attendance_system;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;


public class TotalPresenty extends Fragment {

    String uname2;
    String pword2;

    int TotalDay;
    int presentcount;
    int absentcount;
    TextView Tpresent, Tabsent, Tday,sname;
    ProgressDialog progressDialog;
    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_total_presenty, container, false);
        getActivity().setTitle("Total Present");

        progressDialog = new ProgressDialog(getContext());

        sname = view.findViewById(R.id.sname);
        Tpresent = view.findViewById(R.id.Tpresent);
        Tabsent = view.findViewById(R.id.Tabsent);
        Tday = view.findViewById(R.id.Tday);

        Bundle args = getArguments();
        if (args != null) {
             uname2 = args.getString("uname1");
             pword2 = args.getString("pword1");
            if (uname2 != null && pword2 != null) {
                new LoadDataTask().execute();
            }
        }
        /*presentcount = present.size();
        absentcount = absent.size();

        if (TotalDay > 0)
        {
            TotalPresent = (presentcount/ TotalDay  ) * 100;
            TotalAbsent = (absentcount / TotalDay ) * 100;

            Tpresent.setText("Total Present: "+TotalPresent + " %");
            Tabsent.setText("Total Absent: "+TotalAbsent + " %");
            Tday.setText(String.valueOf(TotalDay));
        }
        if (TotalPresent == 75) {
            Tpresent.setTextColor(Color.GREEN);
        } else {
            Tpresent.setTextColor(Color.RED);
        }*/
        return view;
    }
    public void getData(String uname, String pword) {
        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url("http://tsm.ecssofttech.com/Library/api/SASystem_getSTDInfo.php?uname=" + uname + "&pword=" + pword + "")
                    .build();

            Response response4 = client.newCall(request).execute();
            String responseString4 = response4.body().string();

            //Log.d("Response", responseString4); // Log the server response

            JSONArray contacts = new JSONArray(responseString4);
            for (int i = 0; i < contacts.length(); i++)
            {
                JSONObject c = contacts.getJSONObject(i);

                String  Sname = c.getString("studentName");
                String rollno = c.getString("rollno");
                String course = c.getString("courseName");
                String  year = c.getString("year");

                sname.setText("STD Name :-  "+Sname);
                sname.setTextColor(Color.BLACK);

                if (Sname!=null && rollno!=null && course!=null && year!=null) {
                    getData2(Sname, rollno, course, year);
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
    public void getData2(String sname, String rollno, String course, String year)
    {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url("http://tsm.ecssofttech.com/Library/api/SASystem_getStdPresenty.php?studentName=" + sname + "&rollno=" + rollno + "&course=" + course + "&year=" + year).build();
        try {
            Response response = client.newCall(request).execute();
            String responseString = response.body().string();
            JSONArray contacts = new JSONArray(responseString);

            TotalDay=contacts.length();
            for (int i = 0; i < contacts.length(); i++)
            {
                JSONObject c = contacts.getJSONObject(i);
               String status=c.getString("status");

               if (status.equals("P"))
               {
                   ++presentcount;
               }
               if (status.equals("A"))
               {
                   ++absentcount;
               }
            }
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }

        if (TotalDay > 0)
        {
            float TotalPresent = ((float) presentcount / TotalDay  ) * 100;
            float TotalAbsent = ((float) absentcount / TotalDay ) * 100;

            Tpresent.setText("Present       :-  "+Math.round(TotalPresent) + " %");
            Tabsent.setText( "Absent        :-  "+Math.round(TotalAbsent) + " %");
            Tday.setText(    "Total Day    :-  "+String.valueOf(TotalDay));
            if (TotalPresent >= 75) {
                Tpresent.setTextColor(Color.rgb(7, 130, 38));
                Tabsent.setTextColor(Color.rgb(7, 130, 38));
            }
             else {
                 Tabsent.setTextColor(Color.RED);
                Tpresent.setTextColor(Color.RED);
            }
             if (TotalAbsent<=15)
             {
                 Tabsent.setTextColor(Color.rgb(255,204,0));
             } else if (TotalAbsent<=25) {
                 Tabsent.setTextColor(Color.RED);
             }
             if (TotalAbsent<=10)
             {
                 Tabsent.setTextColor(Color.rgb(7, 130, 38));
             }
            Tday.setTextColor(Color.BLACK);
        }
    }
    public class LoadDataTask extends AsyncTask<Void, Void, Void> {

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
                    getData(uname2, pword2);
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