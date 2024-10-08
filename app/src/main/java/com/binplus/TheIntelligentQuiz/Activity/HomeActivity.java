package com.binplus.TheIntelligentQuiz.Activity;

import static android.content.ContentValues.TAG;
import static com.binplus.TheIntelligentQuiz.BaseURL.BaseURL.GET_PROFILE;
import static com.facebook.FacebookSdk.getApplicationContext;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.binplus.TheIntelligentQuiz.Fragments.AddMoneyFragment;
import com.binplus.TheIntelligentQuiz.Fragments.HomePage;
import com.binplus.TheIntelligentQuiz.Fragments.MyQuizFragment;
import com.binplus.TheIntelligentQuiz.Fragments.ProfileFragment;
import com.binplus.TheIntelligentQuiz.Fragments.HowToPlayFragment;
import com.binplus.TheIntelligentQuiz.Fragments.PrivacyPolicyFragment;
import com.binplus.TheIntelligentQuiz.Fragments.ReferralFragment;
import com.binplus.TheIntelligentQuiz.Fragments.SupportFragment;
import com.binplus.TheIntelligentQuiz.Fragments.TermsAndConditionsFragment;
import com.binplus.TheIntelligentQuiz.Fragments.WalletFragment;
import com.binplus.TheIntelligentQuiz.Fragments.WithdrawFragment;
import com.binplus.TheIntelligentQuiz.Model.ProfileModel;
import com.binplus.TheIntelligentQuiz.R;
import com.binplus.TheIntelligentQuiz.common.Common;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.Firebase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.onesignal.Continue;
import com.onesignal.OneSignal;
import com.onesignal.debug.LogLevel;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private BottomNavigationView bottomNavigationView;
    private ImageView menu_icon;
    private ImageView wallet_icon;
    private DrawerLayout drawerLayout;
    private TextView user_name, user_phone;
    private ImageView settings;
    private Common common;
    NavigationView navigationView;
    private Toolbar topNavigation;
    private Toolbar navigationViewToolbar;
    private TextView toolbarTitle;
    ImageView back_icon;
    ArrayList<ProfileModel.Data> profileList = new ArrayList<>();
    private static final String ONESIGNAL_APP_ID = "9b346f12-1396-404c-9a64-e8ab44f4908b";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initView();
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("page")) {
            String page = intent.getStringExtra("page");
            if ("page1".equals(page)) {
                // Handle the click for Page 1

                Log.d("NotificationClick", "Page 1 clicked");
                // Add your logic here
                Fragment selectedFragment = new ProfileFragment();
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.homeFragment, selectedFragment)
                        .commit();
            } else if ("page2".equals(page)) {
                // Handle the click for Page 2
                Log.d("NotificationClick", "Page 2 clicked");
                // Add your logic here
                Fragment selectedFragment = new MyQuizFragment();
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.homeFragment, selectedFragment)
                        .commit();
            }
        }
        int permissionState = ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS);
        // If the permission is not granted, request it.
        if (permissionState == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions( this, new String[]{android.Manifest.permission.POST_NOTIFICATIONS},1);
        }
        fetchProfileDetails();
        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        String userName = sharedPreferences.getString("userName", "Default Name");
        String userMobile = sharedPreferences.getString("userMobile", "Default Mobile");


        View headerView = navigationView.getHeaderView(0);

        user_name = headerView.findViewById(R.id.user_name);
        user_phone = headerView.findViewById(R.id.user_phone);
        settings = headerView.findViewById(R.id.settings);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.closeDrawer(navigationView);
                topNavigation.setVisibility(View.GONE);
                navigationViewToolbar.setVisibility(View.GONE);
                Fragment selectedFragment = new ProfileFragment();
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.homeFragment, selectedFragment)
                        .commit();
            }
        });
        user_name.setText(userName);
        user_phone.setText(userMobile);

        common = new Common(this);
        bottomNavigationHandler();

        drawerLayout = findViewById(R.id.drawer_layout);

        navigationView.setNavigationItemSelectedListener(this);

        menu_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawerLayout.isDrawerOpen(navigationView)) {
                    drawerLayout.closeDrawer(navigationView);
                } else {
                    drawerLayout.openDrawer(navigationView);
                }
            }
        });

        wallet_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment selectedFragment = new AddMoneyFragment();
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.homeFragment, selectedFragment)
                        .commit();
            }
        });

        if (savedInstanceState == null) {
            bottomNavigationView.setSelectedItemId(R.id.navigation_home);
            topNavigation.setVisibility(View.VISIBLE);
            navigationViewToolbar.setVisibility(View.GONE);
            bottomNavigationView.setVisibility(View.VISIBLE);
        }
        back_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Fragment selectedFragment = new HomePage();
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.homeFragment, selectedFragment)
                        .commit();
                topNavigation.setVisibility(View.VISIBLE);
                navigationViewToolbar.setVisibility(View.GONE);
                bottomNavigationView.setVisibility(View.VISIBLE);
            }
        });

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w("FCM token", "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get the FCM token
                        String token = task.getResult();
                        Log.d("FCM token", "FCM Token: " + token);

                        // Optionally, send token to your server or use it for push notifications
                    }
                });

        // Declare the launcher at the top of your Activity/Fragment:

//        OneSignal.getDebug().setLogLevel(LogLevel.VERBOSE);
//        OneSignal.initWithContext(this, ONESIGNAL_APP_ID);
//        OneSignal.getNotifications().requestPermission(false, Continue.none());
////
//        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//
//// Get the layouts to use in the custom notification
//        RemoteViews notificationLayout = new RemoteViews(getPackageName(), R.layout.custom_notification);
//
//// Apply the layouts to the notification.
//        Notification customNotification = new NotificationCompat.Builder(getApplicationContext(),ONESIGNAL_APP_ID)
//                .setSmallIcon(R.drawable.savings)
//                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
//                .setCustomContentView(notificationLayout)
//                .setCustomBigContentView(notificationLayout)
//                .build();
//
//        notificationManager.notify(666, customNotification);
//
    }
//    private void fetchProfileDetails() {
//        profileList.clear();
//        JsonObject postData = new JsonObject();
//        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("UserSession", Context.MODE_PRIVATE);
//        String authId = sharedPreferences.getString("userId", "Default Id");
//        postData.addProperty("user_id", authId);
//
//        Call<ProfileModel> call = apiInterface.getProfileApi(postData);
//        call.enqueue(new Callback<ProfileModel>() {
//            @Override
//            public void onResponse(@NonNull Call<ProfileModel> call, @NonNull Response<ProfileModel> response) {
//                if (response.isSuccessful() && response.body() != null) {
//                    profileList.add(response.body().getData());
//                    ProfileModel.Data profileData = profileList.get(0);
//                    SharedPreferences.Editor editor = sharedPreferences.edit();
//                    editor.putString("wallet_balance", profileData.getWallet());
//                    editor.apply();
//                }
//            }
//
//            @Override
//            public void onFailure(@NonNull Call<ProfileModel> call, @NonNull Throwable t) {
//            }
//        });
//    }

    private void fetchProfileDetails() {
        profileList.clear();

        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        String authId = sharedPreferences.getString("userId", "Default Id");

        String url = GET_PROFILE;


        JSONObject postData = new JSONObject();
        try {
            postData.put("user_id", authId);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Error creating request data", Toast.LENGTH_SHORT).show();
            return;
        }


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, postData,
                response -> {
                    try {

                        Gson gson = new Gson();
                        ProfileModel profileModel = gson.fromJson(response.toString(), ProfileModel.class);

                        if (profileModel != null && profileModel.getData() != null) {
                            profileList.add(profileModel.getData());
                            ProfileModel.Data profileData = profileList.get(0);

                            // Update SharedPreferences
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("wallet_balance", profileData.getWallet());
                            editor.apply();
                        }
                    } catch (Exception e) {
                        Log.e("fetchProfileDetails", "Error parsing JSON response", e);
                        Toast.makeText(getApplicationContext(), "Error parsing data", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e("fetchProfileDetails", "API call failed: " + error.toString());
                    Toast.makeText(getApplicationContext(), "An error occurred: " + error.toString(), Toast.LENGTH_SHORT).show();
                }
        );


        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(jsonObjectRequest);
    }

    private void bottomNavigationHandler() {
        bottomNavigationView.setLabelVisibilityMode(NavigationBarView.LABEL_VISIBILITY_LABELED);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                if (item.getItemId() == R.id.navigation_home) {
                    selectedFragment = new HomePage();
                    topNavigation.setVisibility(View.VISIBLE);
                    navigationViewToolbar.setVisibility(View.GONE);
                    bottomNavigationView.setVisibility(View.VISIBLE);
                }else if (item.getItemId() == R.id.navigation_add_money) {
                    selectedFragment = new AddMoneyFragment();
                    topNavigation.setVisibility(View.GONE);
                    navigationViewToolbar.setVisibility(View.VISIBLE);
                    toolbarTitle.setText("Add Money");
                }
                else if (item.getItemId() == R.id.navigation_my_quiz) {
                   // selectedFragment = new RankingFragment();
                    selectedFragment = new MyQuizFragment();
                    topNavigation.setVisibility(View.GONE);
                    navigationViewToolbar.setVisibility(View.VISIBLE);
                    toolbarTitle.setText("Quiz");
                }
                else if (item.getItemId() == R.id.navigation_profile) {
                    selectedFragment = new ProfileFragment();
                    topNavigation.setVisibility(View.GONE);
                    navigationViewToolbar.setVisibility(View.GONE);
                    toolbarTitle.setText("Profile");
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.homeFragment, selectedFragment).commit();
                return true;
            }
        });
    }
    public void showBottomNavigation() {
        bottomNavigationView.setVisibility(View.VISIBLE);
    }
    public void setTitleBank(){
        toolbarTitle.setText("Bank Details");
    }

    public void hideBottomNavigation() {
        bottomNavigationView.setVisibility(View.GONE);
    }

    public void showTopNavigation() {
        topNavigation.setVisibility(View.VISIBLE);
        navigationViewToolbar.setVisibility(View.GONE);
    }

    public void hideTopNavigation() {
        topNavigation.setVisibility(View.GONE);
        navigationViewToolbar.setVisibility(View.VISIBLE);
    }

    private void initView() {
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        menu_icon = findViewById(R.id.menu_icon);
        wallet_icon = findViewById(R.id.wallet_icon);
        navigationView = findViewById(R.id.nav_view);
        topNavigation = findViewById(R.id.top_navigation);
        navigationViewToolbar = findViewById(R.id.navigation_view_toolbar);
        toolbarTitle = navigationViewToolbar.findViewById(R.id.title);
        back_icon = findViewById(R.id.back_icon);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment selectedFragment = null;
        String title = "";

        if (item.getItemId() == R.id.live_games) {
            selectedFragment = new HomePage();
            title = "Live Games";
        }
        if (item.getItemId() == R.id.add_money) {
            selectedFragment = new AddMoneyFragment();
            title = "Add Money";
        }
        if (item.getItemId() == R.id.Wallet) {
            selectedFragment = new WalletFragment();
            title = "Wallet";
        }
        if (item.getItemId() == R.id.Withdraw) {
            selectedFragment = new WithdrawFragment();
            title = "Withdraw";
        }
        if (item.getItemId() == R.id.how_to_play) {
            selectedFragment = new HowToPlayFragment();
            title = "How to Play";
        }
        if (item.getItemId() == R.id.Support) {
            selectedFragment = new SupportFragment();
            title = "Support";
        }
        if (item.getItemId() == R.id.Terms_and_Conditions) {
            selectedFragment = new TermsAndConditionsFragment();
            title = "Terms and Conditions";
        }
        if (item.getItemId() == R.id.Privacy_Policy) {
            selectedFragment = new PrivacyPolicyFragment();
            title = "Privacy Policy";
        }
        if (item.getItemId() == R.id.Refer_and_Earn) {
            selectedFragment = new ReferralFragment();
            title = "Refer and Earn";
        }
//        if (item.getItemId() == R.id.Logout) {
//
//            return true;
//        }

        if (drawerLayout.isDrawerOpen(navigationView)) {
            drawerLayout.closeDrawer(navigationView);
        }

        toolbarTitle.setText(title);
        topNavigation.setVisibility(View.GONE);
        navigationViewToolbar.setVisibility(View.VISIBLE);

        getSupportFragmentManager().beginTransaction().replace(R.id.homeFragment, selectedFragment).commit();
        return true;
    }




    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}
