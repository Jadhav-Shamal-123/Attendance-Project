package com.example.student_attendance_system;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.student_attendance_system.Model.AtReportlistmodel;
import com.example.student_attendance_system.Model.AttendanceDetail;
import com.example.student_attendance_system.Model.monthReportmodel;
import com.example.student_attendance_system.adapter.ATadapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import jxl.Sheet;
import jxl.Workbook;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class MonthAttendanceReportActivity extends AppCompatActivity {
    // Add these member variables
    private Spinner monthSpinner;
    private String selectedMonth = "January"; // Use this to track the selected month
    Button share,download;
    String FileName="-";
    ArrayList<monthReportmodel> attendanceData = new ArrayList<>();
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

    List<String> RollRange = new ArrayList<>();
    List<String> nameList=new ArrayList<>();
    Map<String,List<AttendanceDetail>> mapList=new HashMap<>();
    String formattedStartDate,formattedEndDate;

    String Tname,course,year,sub,time;

    ProgressDialog pd ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_month_attendance_report);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        monthSpinner = findViewById(R.id.monthSpinner);
        share=findViewById(R.id.btn_share);
        download=findViewById(R.id.btn_download);
        share.setVisibility(View.GONE);
        download.setVisibility(View.GONE);
        setupMonthSpinner();
        pd= new ProgressDialog(this);
        pd.setCancelable(false);
        pd.setMessage("Please wait..");

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createExcelFile("AttendanceReport_" + selectedMonth + ".xls", mapList,"shar");
            }
        });

        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createExcelFile("AttendanceReport_" + selectedMonth + ".xls", mapList,"download");

            }
        });

    }

    private void setupMonthSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.months_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        monthSpinner.setAdapter(adapter);
        monthSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedMonth=parent.getSelectedItem().toString();
                int selectedMonth = parent.getSelectedItemPosition(); // Assuming you have a spinner for month selection
                //Toast.makeText(MonthAttendanceReportActivity.this, "month "+selectedMonth, Toast.LENGTH_LONG).show();
                // Store the selected month
                // Add your logic here to fetch and check attendance data
                // For simplicity, I'm directly calling the method to create an Excel file
                // You should call this inside your actual data checking logic
                // e.g., after verifying that attendance data for the selected month exists
                share.setVisibility(View.GONE);
                download.setVisibility(View.GONE);
                Log.e("reportatt","calling check att");
                checkAttendanceAndCreateExcel(selectedMonth);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedMonth = "January";
            }
        });
    }

    private void checkAttendanceAndCreateExcel(int month) {
        // Assume we already have attendance data for the month
        // In reality, you'd fetch and verify this data here
        // Get the current year
        int currentYear = Year.now().getValue();
        Log.e("reportatt","check  att for axcel");

        YearMonth yearMonth = YearMonth.of(currentYear, month + 1); // Months are 0-indexed (January = 0)

        // Set the year to the current year
        //yearMonth = yearMonth.withYear(currentYear);

        // Determine the start and end dates
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();
        // Format the start date as a string
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        formattedStartDate = startDate.format(dateFormatter);
        formattedEndDate = endDate.format(dateFormatter);
        Log.e("reportatt","calling getdata start date="+formattedStartDate);
        Log.e("reportatt","calling getdata end date="+formattedEndDate);
       // getData(formattedStartDate,formattedEndDate);
        getData();

        getStudentNames();

    }

    private void getStudentNames() {
        List<String> nlist=new ArrayList<>();
        DatabaseReference reference1=FirebaseDatabase.getInstance().getReference();
        reference1.child("Students").child(course).child(year).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                        nlist.add(dataSnapshot.getValue(String.class));

                    }
                    nameList.addAll(nlist);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


   /* private void createExcelFile(String fileName, ArrayList<monthReportmodel> dataList,String from) {
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("Active student");
        // Create the header row
        HSSFRow headerRow = sheet.createRow(0);
        HSSFCell headerCell = headerRow.createCell(0);
        headerCell.setCellValue("Attendance Report");
        // Populate data rows
        for (int rowIndex = 2; rowIndex <= dataList.size() + 1; rowIndex++) {
            HSSFRow dataRow = sheet.createRow(rowIndex - 1);
            monthReportmodel adminModel = dataList.get(rowIndex - 2);

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
            if(from.equals("download")) {
                Toast.makeText(this, "File Saved at "+filePath, Toast.LENGTH_LONG).show();
            }else {
                shareExcelFile(FileName);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        FileName=fileName;
        //shareExcelFile(fileName);
        /*downloadExcelFile(fileName);
    }

    */


    private void shareExcelFile(String fileName) {
        Log.e("reportatt","at share excel file");
        File excelFile = new File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), fileName);

        Uri uri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", excelFile);

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);

        // Grant read permissions to the receiving app
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        startActivity(Intent.createChooser(shareIntent, "Share Excel File"));
    }

//new get data
private void getData() {
    attendanceData.clear();
    course = getIntent().getStringExtra("course");
    year = getIntent().getStringExtra("year");
    sub = getIntent().getStringExtra("sub");
    String schedule = getIntent().getStringExtra("Schedule");
    Tname = getSharedPreferences("LoginData", 0).getString("uname", "");

   // DateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy");
   // DateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy");

    //String frDate=""+fromDate;

    //String edDate=""+toDate;
    //Log.e("reportatt","inside getdata before parse date="+fromDate);
   /* try {
        Date fdate = inputFormat.parse(String.valueOf(fromDate));
        frDate = outputFormat.format(fdate);

        Date tdate = inputFormat.parse(String.valueOf(toDate));
        edDate = outputFormat.format(tdate);

    } catch (ParseException e) {
        e.printStackTrace();

    }

    */
    //Log.e("reportatt","inside getdata after parse date="+fromDate);


    //Date fdate = inputFormat.parse(fromDate);
    String[] str=schedule.split(",");
    time=str[1];;

    pd.show();

   // String finalFrDate = ;
    //String finalEdDate = edDate;
    new Handler(Looper.getMainLooper()).post(() -> {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        try {
            List<String> rollList=new ArrayList<>();
            RollRange.clear();

            reference.child(Tname).child( course + "/" + year + "/" + sub + "/" + time).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot:snapshot.getChildren()) {
                        rollList.add(dataSnapshot.getKey());
                        //Toast.makeText(MonthAttendanceReportActivity.this, "roll="+dataSnapshot.getKey(), Toast.LENGTH_SHORT).show();
                    }
                    RollRange.addAll(rollList);

                    getAttendanceData();

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    pd.dismiss();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("fail", e.getMessage());
            pd.dismiss();
        }
    });
}

    private void getAttendanceData() {

        Log.e("roll no","roll no size="+RollRange.size());

        for (String roll : RollRange) {

            List<AttendanceDetail> modelList=new ArrayList<>();

            DatabaseReference rollRef = reference.child(Tname).child(course + "/" + year + "/" + sub + "/" + time).child(roll);
            // Replace "/" with "-" in finalFrDate and finalEdDate
            String formattedFinalFrDate = formattedStartDate.replace("/", "-");
            String formattedFinalEdDate = formattedEndDate.replace("/", "-");
            Query query = rollRef.orderByKey().startAt(formattedFinalFrDate).endAt(formattedFinalEdDate);

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        Log.e("reportAtt","snapshot exist");
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            String status = dataSnapshot.getValue(String.class);
                            String date = dataSnapshot.getKey();

                            AttendanceDetail model = new AttendanceDetail(date, status);
                            modelList.add(model);
                            //Toast.makeText(MonthAttendanceReportActivity.this, "date=" + date + " status=" + status, Toast.LENGTH_SHORT).show();
                        }
                        mapList.put(roll, modelList);

                        // Populate your attendanceData here based on the retrieved data
                        // If data is not empty, show share and download buttons
                        share.setVisibility(View.VISIBLE);
                        download.setVisibility(View.VISIBLE);


                    }else {
                        Toast.makeText(MonthAttendanceReportActivity.this, "data not present", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("reportAtt","database error"+error.getMessage());
                    Log.e("reportAtt","database error"+error.getDetails());

                    pd.dismiss();
                }
            });

            Log.e("reportAtt","roll number="+roll);
        }

        pd.dismiss();

    }

    private void createExcelFile(String fileName, Map<String, List<AttendanceDetail>> data, String from) {
        HSSFWorkbook workbook = new HSSFWorkbook();

        HSSFSheet sheet = workbook.createSheet("Active student");
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Excel file creating..");
        progressDialog.show();
        // Create date columns
        int start = Integer.parseInt(formattedStartDate.substring(0, 2));
        int end = Integer.parseInt(formattedEndDate.substring(0, 2));

        HSSFRow r1=sheet.createRow(0);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 5));
        r1.createCell(0).setCellValue("For AICTE Diploma Course");


// Creating and merging cells for the first row
        HSSFRow r2=sheet.createRow(1);
        sheet.addMergedRegion(new CellRangeAddress(1, 2, 0, end + 7));
        HSSFCell cell1 = r2.createCell(0);
        cell1.setCellValue("MAHARASTRA STATE BOARD OF TECHNICAL EDUCATION");
        //cell1.setCellStyle(style);

// Creating and merging cells for the third row
        HSSFRow r3 = sheet.createRow(3);
        sheet.addMergedRegion(new CellRangeAddress(3, 4, 0, end + 7));
        HSSFCell cell2 = r3.createCell(0);
        cell2.setCellValue("Theory Attendance Sheet for Year 2023-24");
        //cell2.setCellStyle(style);

        HSSFRow r4=sheet.createRow(5);
        sheet.addMergedRegion(new CellRangeAddress(5, 5, 0, 4));
        r4.createCell(0).setCellValue("Academic Year: 2023-24");
        sheet.addMergedRegion(new CellRangeAddress(5, 5, 5, end+7));
        r4.createCell(5).setCellValue("Institute : Government Polytechnic, Karad");

        HSSFRow r5=sheet.createRow(6);
        sheet.addMergedRegion(new CellRangeAddress(6, 6, 0, 4));
        r5.createCell(0).setCellValue("Program : "+course);
        sheet.addMergedRegion(new CellRangeAddress(6, 6, 5, end+7));
        r5.createCell(5).setCellValue("Cource :"+sub);

        HSSFRow r6=sheet.createRow(7);
        sheet.addMergedRegion(new CellRangeAddress(7, 7, 0, 4));
        r6.createCell(0).setCellValue("Semester : ");
        sheet.addMergedRegion(new CellRangeAddress(7, 7, 5, end+7));
        r6.createCell(5).setCellValue("Name of Faculty : "+Tname);

        HSSFRow r7=sheet.createRow(8);
        r7.createCell(0).setCellValue("Roll No");
        sheet.addMergedRegion(new CellRangeAddress(8, 8, 1, 4));
        r7.createCell(1).setCellValue("Name");
        sheet.addMergedRegion(new CellRangeAddress(8, 8, 5, end+7));
        r7.createCell(5).setCellValue("Date : "+selectedMonth+",2024");
        sheet.addMergedRegion(new CellRangeAddress(8, 9, end+8, end+8));
        r7.createCell(end+8).setCellValue("Test 1 Marks");
        sheet.addMergedRegion(new CellRangeAddress(8, 9, end+9, end+9));
        r7.createCell(end+9).setCellValue("Test 2 Marks");


        // Create header row
        HSSFRow row1 = sheet.createRow(9);

        for (int i = start; i <= end; i++) {
            if (i < 10) {
                row1.createCell(i+4).setCellValue("0" + i);
            } else {
                row1.createCell(i+4).setCellValue(i + "");
            }
        }

        row1.createCell(end + 5).setCellValue("Present");
        row1.createCell(end + 6).setCellValue("Absent");
        row1.createCell(end + 7).setCellValue("Percentage");

        // Fill data rows
        int rowNum = 10;

        int i=0;
        for (String roll : RollRange) {
            int presentCount = 0;
            int absentCount = 0;
            int total = 0;
            HSSFRow row = sheet.createRow(rowNum);
            List<AttendanceDetail> item = data.get(roll);
            row.createCell(0).setCellValue(roll);

            sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, 1, 4));
            if(nameList.size()>i) {
                row.createCell(1).setCellValue(nameList.get(i));
            }else {
                row.createCell(1).setCellValue("-");
            }

            // Find the cell index for the date and set the status
            for (int j = start; j <= end; j++) {

                for (AttendanceDetail details : item) {
                    boolean found = false;
                    if (details.getDate().startsWith(String.format("%02d", j))) {
                        if (details.getStatus().equals("P")) {
                            presentCount++;
                            found = true;
                        } else {
                            absentCount++;
                        }
                        total++;
                        row.createCell(j+4).setCellValue(found?"1":"0");
                        break;
                    }else {
                        row.createCell(j+4).setCellValue("-");
                    }
                }

            }

            row.createCell(end + 5).setCellValue(presentCount);
            row.createCell(end + 6).setCellValue(absentCount);

            double percentage = total == 0 ? 0 : (presentCount * 100.0 / total);
            row.createCell(end + 7).setCellValue(String.format("%.2f%%", percentage));

            row.createCell(end + 8);
            row.createCell(end + 9);

            rowNum++;
            i++;
        }

        // Merge cells for "Present Percentage" column
        //sheet.addMergedRegion(new CellRangeAddress(0, 0, end + 3, end + 3));

        // Add total row
        HSSFRow totalRow = sheet.createRow(rowNum);
        sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, 0, 4));
        totalRow.createCell(0).setCellValue("Total");
        sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, 5, end + 10));
        totalRow.createCell(5).setCellValue((rowNum - 10)+"");

        /*// Adjust column sizes
        for (int col = 0; col < end + 3; col++) {
            sheet.autoSizeColumn(col);
        }

         */



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
            progressDialog.dismiss();
            if (from.equals("download")) {
                Toast.makeText(this, "File Saved at " + filePath, Toast.LENGTH_LONG).show();
            } else {
                shareExcelFile(fileName);
            }
        } catch (IOException e) {
            progressDialog.dismiss();
            e.printStackTrace();
        }
        FileName = fileName;
    }


   /* private void getData(String fromDate, String toDate) {
        attendanceData.clear();
        String course = getIntent().getStringExtra("course");
        String year = getIntent().getStringExtra("year");
        String sub = getIntent().getStringExtra("sub");

        String schedule = getIntent().getStringExtra("Schedule");
        String Tname = getSharedPreferences("LoginData",0).getString("uname","");
        ArrayList<String> RollRange = new ArrayList<>();

        attendanceData.add(new AtReportlistmodel("Roll No", "Student Name", "Present\nCount", "Absent\nCount", "Percentage", "Remark"));

        ProgressDialog pd = new ProgressDialog(MonthAttendanceReportActivity.this);
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

                        int percentage = (int) ((Float.parseFloat(String.valueOf(PC)) / Float.parseFloat(String.valueOf(total))) * 100);
                        Log.e("percentage", data + " " + percentage);
                        if (percentage >= 75) {
                            Remark = "Non-Defaulter";
                        } else {
                            Remark = "Defaulter";
                        }

                        attendanceData.add(new AtReportlistmodel(data, studentName, String.valueOf(PC), String.valueOf(AC), String.valueOf(percentage), Remark));
                        Log.e("getdatain", String.valueOf(attendanceData.get(0).getSName()));
                    }

                    // Populate your attendanceData here based on the selected month
                    if (attendanceData.size()>1) {
                        // If data is not empty, create and share the Excel file
                        share.setVisibility(View.VISIBLE);
                        download.setVisibility(View.VISIBLE);

                    } else {
                        // Handle case where no data is available
                        //Toast.makeText(this, "list size "+attendanceData.size(), Toast.LENGTH_SHORT).show();
                        //Log.e("att 1",attendanceData.get(0)+"\natt 2"+attendanceData.get(1));

                        share.setVisibility(View.GONE);
                        download.setVisibility(View.GONE);
                        Toast.makeText(MonthAttendanceReportActivity.this, "No attendance data available for selected month.", Toast.LENGTH_SHORT).show();
                    }

                    //Log.e("getdataout", String.valueOf(attendanceData.size()));

                    pd.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("fail",e.getMessage());
                    pd.dismiss();
                }

            }
        });
    }

    */



    @Override
    protected void onPause() {
        super.onPause();
        //monthSpinner.setSelected(false);
        download.setVisibility(View.GONE);
        share.setVisibility(View.GONE);
    }


}