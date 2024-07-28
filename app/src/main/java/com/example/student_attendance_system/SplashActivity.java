package com.example.student_attendance_system;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
public class SplashActivity extends AppCompatActivity
{
    private static final long SPLASH_DISPLAY_DURATION = 1200; // 2 seconds
    private static final long CHECK_NETWORK_DELAY = 10; // 0.5 seconds
    private ImageView splashLogo;
    private boolean internetAvailable = false;
    private BroadcastReceiver networkChangeReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        splashLogo = findViewById(R.id.splash_logo);

        /*SharedPreferences sharedPreferences = getSharedPreferences("LoginData",0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("uname");
        editor.remove("pword");
        editor.apply();*/



        Animation translateAnimation = new TranslateAnimation(0, 0, -300, 0);
        translateAnimation.setDuration(1000);
        splashLogo.startAnimation(translateAnimation);
        // Start checking for internet connectivity
        checkInternetAvailability();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Register a BroadcastReceiver to listen for network connectivity changes
        networkChangeReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                checkInternetAvailability();
            }
        };
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeReceiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Unregister the BroadcastReceiver when the activity is not in the foreground
        if (networkChangeReceiver != null) {
            unregisterReceiver(networkChangeReceiver);
        }
    }

    private void checkInternetAvailability() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                internetAvailable = isNetworkAvailable();

                if (internetAvailable) {
                    // Internet is available, proceed with the login activity
                    navigateToLoginActivity();
                } else {
                    // Internet is not available, display the "no internet" image
                    setContentView(R.layout.nointernet);
                }
            }
        }, CHECK_NETWORK_DELAY);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            android.net.NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
        return false;
    }

    private void navigateToLoginActivity() {
        if (!internetAvailable) {
            // The internet became available, so remove the "no internet" image
            setContentView(R.layout.splash); // Switch back to the splash screen
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {


                SharedPreferences sharedPreferences = getSharedPreferences(Login.PREFS_NAME,0);
                //boolean hasLoggedIn = sharedPreferences.getBoolean("hasLoggedIn",false);
                String e1 = sharedPreferences.getString("e1","");
                String e2 = sharedPreferences.getString("e2","");

                if (e1 !="" & e2 !="")
                {
                    Intent intent = new Intent(SplashActivity.this, StudentDashboard.class);
                    intent.putExtra("Mobile", e1);
                    startActivity(intent);
                    finish();
                }
                else {
                    Intent intent = new Intent(SplashActivity.this,Login.class);
                    // intent.putExtra("Mobile", e1);
                    startActivity(intent);
                    finish();
                }




            }
        }, SPLASH_DISPLAY_DURATION);
    }
}


/*public class SplashActivity extends AppCompatActivity {
    private static final long SPLASH_DISPLAY_DURATION = 1200; // 2 seconds
    private static final long CHECK_NETWORK_DELAY = 10; // 0.5 seconds

    private ImageView splashLogo;
    private boolean internetAvailable = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        splashLogo = findViewById(R.id.splash_logo);
        Animation translateAnimation = new TranslateAnimation(0, 0, -300, 0);
        translateAnimation.setDuration(1000);
        splashLogo.startAnimation(translateAnimation);

        // Start checking for internet connectivity
        checkInternetAvailability();
    }
    private void checkInternetAvailability() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                internetAvailable = isNetworkAvailable();

                if (internetAvailable) {
                    // Internet is available, proceed with the login activity
                    navigateToLoginActivity();
                } else {
                    // Internet is not available, display the "no internet" image
                    Toast.makeText(SplashActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                    setContentView(R.layout.nointernet);
                }
            }
        }, CHECK_NETWORK_DELAY);
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            android.net.NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
        return false;
    }
    private void navigateToLoginActivity() {
        if (!internetAvailable) {
            // The internet became available, so remove the "no internet" image
            setContentView(R.layout.splash); // Switch back to the splash screen
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent mainIntent = new Intent(SplashActivity.this, Login.class);
                startActivity(mainIntent);
                finish();
            }
        }, SPLASH_DISPLAY_DURATION);
    }
}*/
/*public class SplashActivity extends AppCompatActivity {
    private static final long SPLASH_DISPLAY_DURATION = 1200; // 2 seconds
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);


        ImageView splashLogo = findViewById(R.id.splash_logo);

        Animation translateAnimation = new TranslateAnimation(0, 0, -300, 0);
        translateAnimation.setDuration(1000);
        splashLogo.startAnimation(translateAnimation);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent mainIntent = new Intent(SplashActivity.this, Login.class);
                startActivity(mainIntent);
                finish();
            }
        }, SPLASH_DISPLAY_DURATION);
    }
}*/
