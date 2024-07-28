/*package com.example.student_attendance_system;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;
public class Dashboard extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    public NavigationView nav_view;
    public Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        drawerLayout = findViewById(R.id.drawer_layout);
        nav_view = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);
        ImageView splashLogo = findViewById(R.id.image);

        Animation translateAnimation = new TranslateAnimation(0, 0, -300, 0);
        translateAnimation.setDuration(1000);
        splashLogo.startAnimation(translateAnimation);

        actionBarDrawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );
        setSupportActionBar(toolbar);

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        nav_view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.menu_section1) {
                   Intent intent=new Intent(Dashboard.this,CourseMaster.class);
                    startActivity(intent);
              }
                else if (itemId == R.id.menu_item1) {
                    Intent intent=new Intent(Dashboard.this,TeacherRegistration.class);
                    startActivity(intent);
               } else if (itemId == R.id.menu_item2) {
                    Intent intent=new Intent(Dashboard.this,StudentRegistration.class);
                    startActivity(intent);
                }
                drawerLayout.closeDrawers();
                return true;
            }
        });
        }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}*/
//
package com.example.student_attendance_system;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar; // Correct import
import com.google.android.material.navigation.NavigationView;
public class TeacherDashboard extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawerLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_dashboard);
        Toolbar toolbar = findViewById(R.id.toolbar); // Use androidx.appcompat.widget.Toolbar
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);




        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new DashboradFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_dashboard);
        }
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item)
    {
        if (item.getItemId() == R.id.nav_dashboard)
        {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new DashboradFragment()).commit();
        }
        else if (item.getItemId()==R.id.nav_sAttendance)
        {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new StudentAttendance()).commit();

            String uname=getIntent().getStringExtra("uname");
            String pword=getIntent().getStringExtra("pword");

            Bundle bundle = new Bundle();
            bundle.putString("uname", uname);
            bundle.putString("pword", pword);

            StudentAttendance studentAttendanceFragment = new StudentAttendance();
            studentAttendanceFragment.setArguments(bundle);

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, studentAttendanceFragment).commit();

            //startActivity(new Intent(TeacherDashboard.this, CreateLecture.class));
        }
        else if (item.getItemId()==R.id.nav_studentReg) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new StudentReg()).commit();
        }
        else if (item.getItemId() == R.id.nav_allteacherinfo)
        {
            Intent intent = new Intent(this, TotalTeacherWithSubject.class);
            startActivity(intent);
        }
         else if (item.getItemId() == R.id.nav_logout)
        {

            getSharedPreferences("LoginData",0).edit().remove("role").apply();
            getSharedPreferences("LoginData",0).edit().remove("uname").apply();
            getSharedPreferences("LoginData",0).edit().remove("pword").apply();
            Toast.makeText(this, "Logout..!", Toast.LENGTH_SHORT).show();
           finishAffinity();


           Intent intent=new Intent(TeacherDashboard.this, Login.class);
           startActivity(intent);
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
    @Override
    public void onBackPressed()
    {
        if (drawerLayout.isDrawerOpen(GravityCompat.START))
        {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else {
            super.onBackPressed();
        }
    }
}

