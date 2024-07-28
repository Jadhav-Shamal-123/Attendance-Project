package com.example.student_attendance_system;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.student_attendance_system.Model.NewStudentRegModel;
import com.example.student_attendance_system.adapter.NewStudentRegAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.loopj.android.http.AsyncHttpClient;

import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import jxl.Workbook;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RegisterNewStudent extends AppCompatActivity {


    AsyncHttpClient client;

    ArrayList<NewStudentRegModel> arrayList;

    List<String> SName;
    List<String> rollNo;
    List<String> CourseName;
    List<String> Year;
    List<String> Username;
    List<String> Password;
    List<String> PNumber;

    RelativeLayout submitLayout,selectExcelLayout;

    private static final int PICK_EXCEL_REQUEST_CODE = 123;

    RecyclerView excelstudentRVL;
    Workbook workbook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_new_student);

        SName = new ArrayList<>();
        rollNo = new ArrayList<>();
        CourseName = new ArrayList<>();
        Year = new ArrayList<>();
        Username = new ArrayList<>();
        Password = new ArrayList<>();
        PNumber = new ArrayList<>();

        selectExcelLayout= findViewById(R.id.selectExcelLayout);
        submitLayout = findViewById(R.id.submitLayout);

        arrayList = new ArrayList<>();

        excelstudentRVL = findViewById(R.id.excelstudentRVL);
        excelstudentRVL.setLayoutManager(new LinearLayoutManager(RegisterNewStudent.this));
        excelstudentRVL.setHasFixedSize(false);



        findViewById(R.id.registerStudentBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ProgressDialog progressDialog = new ProgressDialog(RegisterNewStudent.this);
                progressDialog.setCancelable(false);
                progressDialog.setMessage("Please wait..");
                progressDialog.show();


                DatabaseReference reference= FirebaseDatabase.getInstance().getReference();
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        for (int l = 0;l<arrayList.size();l++){
                            String sn = arrayList.get(l).getSName();
                            String rn = arrayList.get(l).getRollNo();
                            String cn = arrayList.get(l).getCourseName();
                            String y = arrayList.get(l).getYear();
                            String un = arrayList.get(l).getUsername();
                            String pw = arrayList.get(l).getPassword();
                            String pn = arrayList.get(l).getPNumber();

                            Log.e("studentName",sn);
                            Log.e("roll no",rn);
                            Log.e("cousename",cn);
                            Log.e("year",y);
                            Log.e("Username",un);
                            Log.e("password",pw);
                            Log.e("Pnumber",pn);


                            reference.child("Students").child(cn).child(y).child(un).setValue(sn);

                            new StudentRegistrationTask().execute(cn, y, sn, un, pw,pn,rn);


                        }
                        progressDialog.dismiss();
                        Intent intent = new Intent(getApplicationContext(), TeacherDashboard.class);
                        startActivity(intent);
                        finish();
                    }
                },2000);




            }
        });
        findViewById(R.id.selectexcelBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pickExcelFile();



            }
        });






    }

    private void pickExcelFile() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*"); // Allow all file types
        String[] mimeTypes = {"application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        startActivityForResult(intent, PICK_EXCEL_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,  Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_EXCEL_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null && data.getData() != null) {
                // Get the URI of the selected file
                Uri uri = data.getData();

                // Get the file name
                String fileName = getFileName(uri);

                String selectedFileUri = data.getData().toString();

                try {
                    UploadData(uri);
                } catch (IOException e) {
                    Log.d("Selected Excel file:", "Cell Value: " + e.getMessage());
                }

                Log.d("Selected Excel file:", "Cell Value: " + fileName);

            }
        }
    }






    private void UploadData(Uri excelFilePath) throws IOException {
        DataFormatter dataFormatter = new DataFormatter();
        arrayList.clear();
        SName.clear();
        rollNo.clear();
        CourseName.clear();
        Year.clear();
        Username.clear();
        Password.clear();
        PNumber.clear();

        InputStream inputStream = getContentResolver().openInputStream(excelFilePath);


        assert inputStream != null;
        org.apache.poi.ss.usermodel.Workbook workbook = WorkbookFactory.create(inputStream);
        org.apache.poi.ss.usermodel.Sheet sheet = workbook.getSheetAt(0);


        for (Row row : sheet) {
            if (row.getPhysicalNumberOfCells() >= 7) {
                org.apache.poi.ss.usermodel.Cell[] cells = new org.apache.poi.ss.usermodel.Cell[7];
                for (int i = 0; i < 7; i++) {
                    cells[i] = row.getCell(i);
                    Log.e("file ", cells[i].toString());
                }

                /*SName.add(cells[0].toString());
                rollNo.add(cells[1].toString());
                CourseName.add(cells[2].toString());
                Year.add(cells[3].toString());
                Username.add(cells[4].toString());
                Password.add(cells[5].toString());
                PNumber.add(cells[6].toString());*/

                SName.add(dataFormatter.formatCellValue(cells[0]));
                rollNo.add(dataFormatter.formatCellValue(cells[1]));
                CourseName.add(dataFormatter.formatCellValue(cells[2]));
                Year.add(dataFormatter.formatCellValue(cells[3]));
                Username.add(dataFormatter.formatCellValue(cells[4]));
                Password.add(dataFormatter.formatCellValue(cells[5]));
                PNumber.add(dataFormatter.formatCellValue(cells[6]));

            } else {
                // Handle the case where the row does not have enough columns
                Log.e("Error", "Row does not have enough columns");
            }

        }


        for (int l = 1;l<SName.size();l++){
            String sn = SName.get(l);
            String rn = rollNo.get(l);
            String cn = CourseName.get(l);
            String y = Year.get(l);
            String un = Username.get(l);
            String pw = Password.get(l);
            String pn = PNumber.get(l);


            arrayList.add(new NewStudentRegModel(sn,rn,cn,y,un,pw,pn));
        }


        excelstudentRVL.setAdapter(new NewStudentRegAdapter(arrayList));

        selectExcelLayout.setVisibility(View.GONE);
        submitLayout.setVisibility(View.VISIBLE);
        System.out.println();


        workbook.close();
        inputStream.close();


    }

    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    result = cursor.getString(nameIndex);
                }
            }
        }
        if (result == null) {
            result = uri.getLastPathSegment();
        }
        return result;
    }

    private class StudentRegistrationTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String course = params[0];
            String year = params[1];
            String studentName = params[2];
            String userName = params[3];
            String password = params[4];
            String Pnumber = params[5];
            int rollno = Integer.parseInt(params[6]);
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url("http://testproject.life/Projects/GPKSASystem/SASystem_studentRegistration.php?studentName=" + studentName + "&userName=" + userName + "&password=" + password + "&course=" + course + "&year=" + year+"&rollno="+rollno+"&Pnumber="+Pnumber)
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
                if (!result.equalsIgnoreCase("Registration Success.")) {
                    Toast.makeText(getApplicationContext(), "Registration failed. Please try again later.", Toast.LENGTH_SHORT).show();

                }
            } else {
                Toast.makeText(getApplicationContext(), "Network error. Please check your connection.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}