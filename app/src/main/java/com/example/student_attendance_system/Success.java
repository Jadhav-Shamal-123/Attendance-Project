package com.example.student_attendance_system;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class Success extends AppCompatActivity {
CardView card,card1;
TextView msg,pcount,acount,total;

String uname;
String pword;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success);
        card=findViewById(R.id.card);
        card1=findViewById(R.id.card1);
        msg=findViewById(R.id.msg);
        pcount=findViewById(R.id.pcount);
        acount=findViewById(R.id.acount);
        total=findViewById(R.id.total);
        int Pcount = getIntent().getIntExtra("pcount",0);
        int Acount = getIntent().getIntExtra("acount",0);
         uname=getIntent().getStringExtra("uname");
         pword=getIntent().getStringExtra("pword");

        boolean activation=getIntent().getBooleanExtra("disableCall",false);
        if (activation)
        {
            msg.setVisibility(View.GONE);
            card.setVisibility(View.GONE);
            pcount.setVisibility(View.VISIBLE);
            pcount.setText("Present: "+String.valueOf(Pcount));
            acount.setVisibility(View.VISIBLE);
            acount.setText("Absent: "+String.valueOf(Acount));
            total.setText("Total: "+(Pcount+Acount));
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) card1.getLayoutParams();
            layoutParams.topMargin = 240;
            card1.setLayoutParams(layoutParams);
        } else {
            card.setVisibility(View.VISIBLE);

            msg.setVisibility(View.VISIBLE);
            pcount.setVisibility(View.GONE);
            acount.setVisibility(View.GONE);
            total.setVisibility(View.GONE);
        }

        card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Success.this,TotalTeacherWithSubject.class);
                startActivity(intent);
            }
        });

        card1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    Intent intent = new Intent(Success.this, TeacherDashboard.class);
                    intent.putExtra("uname",uname);
                    intent.putExtra("pword",pword);
                    startActivity(intent);
                    finishAffinity();
            }
        });
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, TeacherDashboard.class);
        intent.putExtra("uname",uname);
        intent.putExtra("pword",pword);
        startActivity(intent);
        recreate();
        finish();
    }
}