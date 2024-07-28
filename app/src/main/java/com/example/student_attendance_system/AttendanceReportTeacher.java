package com.example.student_attendance_system;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.example.student_attendance_system.Model.AtReportlistmodel;
import com.example.student_attendance_system.adapter.ATadapter;

import org.apache.commons.math3.analysis.function.Exp;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Locale;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AttendanceReportTeacher extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<AtReportlistmodel> arrayList;

    ProgressDialog mainPd;
    TextView FromDateTV, ToDateTV,subnameTV;

    private static final int CALL_SMS_PERMISSION_CODE = 123;

    Button ExportBtn,SendSMS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendancereportteacher);

        arrayList = new ArrayList<>();
        ToDateTV = findViewById(R.id.ToDateTV);
        FromDateTV = findViewById(R.id.FromDateTV);
        subnameTV = findViewById(R.id.subnameTV);
        ExportBtn = findViewById(R.id.ExportBtn);
        SendSMS = findViewById(R.id.SendsmsBtn);


        checkAndRequestCallSmsPermission();


        subnameTV.setText(getIntent().getStringExtra("sub"));

        FromDateTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(FromDateTV);
            }
        });

        ToDateTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(ToDateTV);
            }
        });

        findViewById(R.id.submitBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String fromDate = FromDateTV.getText().toString();
                String toDate = ToDateTV.getText().toString();

                if (fromDate.equals("FromDate") || toDate.equals("ToDate")) {
                    Toast.makeText(AttendanceReportTeacher.this, "Please enter dates", Toast.LENGTH_SHORT).show();

                }else {
                    arrayList.clear();
                    getData(fromDate,toDate);
                }
            }
        });

        ExportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createExcelFile("AttendanceReport.xls",arrayList);
            }
        });
        SendSMS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentMobile(arrayList);
            }
        });


        recyclerView = findViewById(R.id.atReportRVL);
        recyclerView.setLayoutManager(new LinearLayoutManager(AttendanceReportTeacher.this));
        recyclerView.setHasFixedSize(false);


    }

    private void checkAndRequestCallSmsPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {

        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SEND_SMS},
                    CALL_SMS_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CALL_SMS_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permissions are granted; you can perform call and SMS operations here.
            } else {
                // Permissions are denied; handle the case where the user declined the permission request.
            }
        }


    }


    public void showDatePickerDialog(TextView view) {
        Calendar selectedDate = Calendar.getInstance();
        int year = selectedDate.get(Calendar.YEAR);
        int month = selectedDate.get(Calendar.MONTH);
        int dayOfMonth = selectedDate.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        // Update selectedDate with the chosen date
                        selectedDate.set(year, month, day);
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                        String formattedDate = sdf.format(selectedDate.getTime());
                        view.setText(formattedDate);

                    }
                },
                year, month, dayOfMonth);
        datePickerDialog.show();
    }

    private void getParentMobile(ArrayList<AtReportlistmodel> arrayList) {
        mainPd = new ProgressDialog(AttendanceReportTeacher.this);
        mainPd.setCancelable(false);
        mainPd.setMessage("Loading..");
        mainPd.show();

        String course = getIntent().getStringExtra("course");
        String year = getIntent().getStringExtra("year");


        for (int i = 1;i<arrayList.size();i++){

            if (arrayList.get(i).getRemark().equals("Defaulter")) {

                String roll = arrayList.get(i).getRollNo();
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);

                try {
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url("http://testproject.life/Projects/GPKSASystem/SAS_getStudentPnumber.php?RollNo=" + roll + "&YEAR=" + year + "&course=" + course)
                            .build();

                    Response response = client.newCall(request).execute();
                    String strResponse = response.body().string();

                    Log.e("response:", strResponse);

                    JSONArray jsonArray = new JSONArray(strResponse);

                    if (jsonArray.length() > 0) {

                        for (int a = 0; a < jsonArray.length(); a++) {
                            JSONObject o = jsonArray.getJSONObject(a);
                            String pn = o.getString("Pnumber");
                            Log.e("pn:", pn);
                            sendSMS(pn);
                        }

                    }
                    mainPd.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("error :", e.getMessage());
                    mainPd.dismiss();
                }

            }
        }
    }
    public void sendSMS(String omobile) {
        ProgressDialog pd = new ProgressDialog(AttendanceReportTeacher.this);
        pd.setCancelable(false);
        pd.setMessage("Please wait SMS sending....");
        pd.show();

        String message = "Hello parent your child attendance is low";

        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(omobile, null, message, null, null);
            pd.dismiss();
        } catch (Exception e) {
            //Toast.makeText(this, "SMS could not be sent.", Toast.LENGTH_SHORT).show();
            Log.e("sms",e.getMessage());

            Intent smsIntent = new Intent(Intent.ACTION_VIEW);
            smsIntent.setData(Uri.parse("smsto:" + omobile));
            smsIntent.putExtra("sms_body", message);
            startActivity(smsIntent);
            pd.dismiss();
        }
    }


    private void getData(String fromDate, String toDate) {
        arrayList.clear();
        String course = getIntent().getStringExtra("course");
        String year = getIntent().getStringExtra("year");
        String sub = getIntent().getStringExtra("sub");

        String schedule = getIntent().getStringExtra("Schedule");
        String Tname = getSharedPreferences("LoginData",0).getString("uname","");
        ArrayList<String> RollRange = new ArrayList<>();

        arrayList.add(new AtReportlistmodel("Roll No", "Student Name", "Present\nCount", "Absent\nCount", "Percentage", "Remark"));

        ProgressDialog pd = new ProgressDialog(AttendanceReportTeacher.this);
        pd.setCancelable(false);
        pd.setMessage("Please wait..");
        pd.show();

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {

                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);

                try {
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder().url("http://testproject.life/Projects/GPKSASystem/SAS_getATreport.php?ASdate="+fromDate+"&AEdate="+toDate+"&course="+course+"&year="+year+
                                    "&schedule="+schedule+"&Tname="+Tname+"&sub="+sub)
                            .build();
                    Response response = client.newCall(request).execute();
                    String strResponse = response.body().string();
                    JSONObject mainobject = new JSONObject(strResponse);
                    JSONArray jsonArray = mainobject.getJSONArray("data");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject o = jsonArray.getJSONObject(i);
                        String Roll = o.getString("rollno");
                        if (!RollRange.contains(Roll)) {
                            RollRange.add(Roll);
                        }
                    }
                    //int TotalLecture = (jsonArray.length() / RollRange.toArray().length);
                    int TotalLecture = Integer.parseInt(mainobject.getString("TL"));
                    Log.e("TL", String.valueOf(TotalLecture));

                    Collections.sort(RollRange);
                    for (String data : RollRange) {

                        int AC = 0;
                        int PC = 0;
                        int total=0;
                        String studentName = "-";
                        String Remark = "-";
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject o = jsonArray.getJSONObject(i);
                            String Roll = o.getString("rollno");
                            String status = o.getString("status");
                            if (Roll.equals(data)) {
                                studentName = o.getString("studentName");
                            }

                            if (Roll.contains(data) && status.equals("P")) {
                                PC++;
                            }

                            if (Roll.contains(data) && status.equals("A")) {
                                AC++;
                            }
                            total++;

                        }
                        int percentage=0;
                        if(total!=0) {
                            percentage = ((PC* 100)/total);
                        }
                        Log.e("percentage", data + " " + percentage);
                        if (percentage >= 75) {
                            Remark = "Non-Defaulter";
                        } else {
                            Remark = "Defaulter";
                        }
                        arrayList.add(new AtReportlistmodel(data, studentName, String.valueOf(PC), String.valueOf(AC), String.valueOf(percentage), Remark));
                    }
                    recyclerView.setAdapter(new ATadapter(AttendanceReportTeacher.this, arrayList));

                    if (arrayList.size()>0){
                        ExportBtn.setVisibility(View.VISIBLE);
                        SendSMS.setVisibility(View.VISIBLE);
                    }
                    pd.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("fail",e.getMessage());
                    pd.dismiss();
                }

            }
        });
    }
    private void createExcelFile(String fileName, ArrayList<AtReportlistmodel> dataList) {
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("Active student");
        // Create the header row
        HSSFRow headerRow = sheet.createRow(0);
        HSSFCell headerCell = headerRow.createCell(0);
        headerCell.setCellValue("Attendance Report");
        // Populate data rows
        for (int rowIndex = 2; rowIndex <= dataList.size() + 1; rowIndex++) {
            HSSFRow dataRow = sheet.createRow(rowIndex - 1);
            AtReportlistmodel adminModel = dataList.get(rowIndex - 2);

            dataRow.createCell(0).setCellValue(adminModel.getRollNo());
            dataRow.createCell(1).setCellValue(adminModel.getSName());
            dataRow.createCell(2).setCellValue(adminModel.getACount());
            dataRow.createCell(3).setCellValue(adminModel.getPCount());
            dataRow.createCell(4).setCellValue(adminModel.getPercentage()+" %");
            dataRow.createCell(5).setCellValue(adminModel.getRemark());
        }
        // Save the workbook to a file
        File filePath = new File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), fileName);
        try {
            if (!filePath.exists()) {
                filePath.createNewFile();
            }
            FileOutputStream fileOutputStream = new FileOutputStream(filePath);
            workbook.write(fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        shareExcelFile(fileName);
        /*downloadExcelFile(fileName);*/
    }

    private void shareExcelFile(String fileName) {
        File excelFile = new File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), fileName);

        Uri uri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", excelFile);

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);

        // Grant read permissions to the receiving app
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        startActivity(Intent.createChooser(shareIntent, "Share Excel File"));
    }


}