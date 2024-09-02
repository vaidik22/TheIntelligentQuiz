package com.binplus.TheIntelligentQuiz.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.binplus.TheIntelligentQuiz.Adapters.ViewPagerAdapterWallet;
import com.binplus.TheIntelligentQuiz.Model.ProfileModel;
import com.binplus.TheIntelligentQuiz.Model.TransactionModel;
import com.binplus.TheIntelligentQuiz.R;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;


public class WalletFragment extends Fragment {
    TabLayout tabLayout;
    ViewPager viewPager;
    ViewPagerAdapterWallet viewPagerAdapter;
    ArrayList<TransactionModel.Datum> transactionList = new ArrayList<>();
     
    String key = "2";
    ArrayList<ProfileModel.Data> profile = new ArrayList<>();
    TextView available_balance;

    public WalletFragment() {
        // Required empty public constructor
    }
    public static WalletFragment newInstance(String param1, String param2) {
        WalletFragment fragment = new WalletFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_wallet, container, false);
        initview(view);
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        String balance = sharedPreferences.getString("wallet_balance", "0");
        available_balance.setText("Rs."+balance);
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
        return view;
    }

    private void setupViewPager(ViewPager viewPager) {
        viewPagerAdapter = new ViewPagerAdapterWallet(getChildFragmentManager());
        viewPagerAdapter.addFragment(new AllWalletTransFragment(), "All");
        viewPagerAdapter.addFragment(new AddMoneyWalletTransFragment(), "Add Money");
        viewPagerAdapter.addFragment(new WithdrawalWalletTransFragment(), "Withdrawal");
        viewPagerAdapter.addFragment(new ReferralWalletTransFragment(), "Referral");
        viewPagerAdapter.addFragment(new WinningWalletTransFragment(), "Winning");
        viewPager.setAdapter(viewPagerAdapter);
    }

    private void initview(View view) {
        viewPager = view.findViewById(R.id.viewPager);
        tabLayout = view.findViewById(R.id.tabLayout);
        available_balance = view.findViewById(R.id.available_balance);
    }
}