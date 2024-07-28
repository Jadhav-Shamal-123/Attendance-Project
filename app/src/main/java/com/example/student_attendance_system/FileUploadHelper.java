package com.example.student_attendance_system;
import android.util.Log;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.File;
import java.io.IOException;

public class FileUploadHelper {

    private static final String BASE_URL = "http://tsm.ecssofttech.com/";

    public static String uploadFile(String filePath) {
        OkHttpClient client = new OkHttpClient();

        File file = new File(filePath);
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", file.getName(), RequestBody.create(MediaType.parse("multipart/form-data"), file))
                .build();

        Request request = new Request.Builder()
                .url(BASE_URL + "/SASExcelFiles/") // Specify the correct endpoint on your server
                .post(requestBody)
                .build();

        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                return "File uploaded successfully";
            } else {
                return "Failed to upload file. Server response: " + response.message();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("IOException occurred: ",e.getMessage());
            return "IOException occurred: " + e.getMessage();

        }
    }
}
