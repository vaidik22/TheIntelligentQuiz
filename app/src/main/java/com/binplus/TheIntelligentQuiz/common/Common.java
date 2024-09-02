package com.binplus.TheIntelligentQuiz.common;

import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.binplus.TheIntelligentQuiz.R;


public class Common {
    private AppCompatActivity activity;
    private Context context;

    public Common(Context context) {
        this.context = context;
    }

    public Common(AppCompatActivity activity) {
        this.activity = activity;
    }

    public void switchFragment(Fragment fragment) {
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment)
                .addToBackStack(null).commit();
    }

    public void switchFragmentHome(Fragment fragment) {
        FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager ( );
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction ( );
        fragmentTransaction.replace (R.id.homeFragment, fragment);
        fragmentTransaction.addToBackStack (null);
        fragmentTransaction.commit ( );
    }
}
