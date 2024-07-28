package com.example.student_attendance_system;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

public class AdminDashbord extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
{
    private DrawerLayout drawerLayout;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashbord);
         Toolbar toolbar = findViewById(R.id.toolbar);
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
        else if (item.getItemId() == R.id.nav_course)
        {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new CourseFragment()).commit();
        }
        else if (item.getItemId() == R.id.nav_subject)
        {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new SubjectFragment()).commit();
        }
        else if (item.getItemId() == R.id.nav_teachersubject)
        {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new TeacherSubjectFragment()).commit();
        } else if (item.getItemId()==R.id.nav_teacherReg) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new TeacherReg()).commit();
        } else if (item.getItemId() == R.id.nav_allteacherinfo)
        {
            Intent intent = new Intent(this, TotalTeacherWithSubject.class);
            startActivity(intent);
        }
        else if (item.getItemId() == R.id.nav_logout)
        {
            Toast.makeText(this, "Logout..!", Toast.LENGTH_SHORT).show();
            finishAffinity();
            Intent intent=new Intent(AdminDashbord.this, Login.class);
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