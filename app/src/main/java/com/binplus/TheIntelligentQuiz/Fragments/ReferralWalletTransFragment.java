package com.binplus.TheIntelligentQuiz.Fragments;

import static com.binplus.TheIntelligentQuiz.BaseURL.BaseURL.WALLET_HISTORY;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.binplus.TheIntelligentQuiz.Adapters.TransactionAdapter;
import com.binplus.TheIntelligentQuiz.Model.TransactionModel;
import com.binplus.TheIntelligentQuiz.R;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;



public class ReferralWalletTransFragment extends Fragment {

    private RecyclerView recyclerView;
    private TransactionAdapter adapter;
    TextView textInputError;
    private ArrayList<TransactionModel.Datum> transactionList;
     
    String key = "3";
    long delay = 3000;

    public ReferralWalletTransFragment() {
        // Required empty public constructor
    }

    public static ReferralWalletTransFragment newInstance(String param1, String param2) {
        ReferralWalletTransFragment fragment = new ReferralWalletTransFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         
        transactionList = new ArrayList<>();
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_referral_wallet_trans, container, false);
        textInputError = view.findViewById(R.id.textinput_error);
        recyclerView = view.findViewById(R.id.referal_wallet_trans_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new TransactionAdapter(transactionList, "");
        recyclerView.setAdapter(adapter);
        fetchTransactions();

        return view;
    }

//    private void fetchTransactions() {
//        transactionList.clear();
//        JsonObject postData = new JsonObject();
//        SharedPreferences sharedPreferences = getContext().getSharedPreferences("UserSession", Context.MODE_PRIVATE);
//        String authId = sharedPreferences.getString("userId", "Default Id");
//        postData.addProperty("user_id", authId);
//        postData.addProperty("page", "1");
//        postData.addProperty("key", key);
//
//        Call<TransactionModel> call = apiInterface.getTransactionApi(postData);
//        call.enqueue(new Callback<TransactionModel>() {
//            @Override
//            public void onResponse(@NonNull Call<TransactionModel> call, @NonNull Response<TransactionModel> response) {
//                if (response.isSuccessful() && response.body() != null) {
//                    if (response.body().getData().isEmpty()) {
//                        showError(R.string.no_data_found);
//                    } else {
//                        transactionList.addAll(response.body().getData());
//                        adapter.notifyDataSetChanged();
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(@NonNull Call<TransactionModel> call, @NonNull Throwable t) {
//                // Handle failure
//            }
//        });
//    }
private void fetchTransactions() {
    transactionList.clear();
    String url = WALLET_HISTORY;
    JSONObject postData = new JSONObject();
    try {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        String authId = sharedPreferences.getString("userId", "Default Id");
        postData.put("user_id", authId);
        postData.put("page", "1");
        postData.put("key", key);
    } catch (JSONException e) {
        e.printStackTrace();
        return;
    }

    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, postData,
            response -> {
                try {
                    Gson gson = new Gson();
                    TransactionModel transactionModel = gson.fromJson(response.toString(), TransactionModel.class);

                    if (transactionModel != null && transactionModel.getData() != null) {
                        if (transactionModel.getData().isEmpty()) {
                            showError(R.string.no_data_found);
                        } else {
                            transactionList.addAll(transactionModel.getData());
                            adapter.notifyDataSetChanged();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();

                }
            },
            error -> {
                Log.e("FetchTransactions", "API call failed: " + error.toString());

            }
    );

    Volley.newRequestQueue(getContext()).add(jsonObjectRequest);
}
    public void showError(int resId) {
        textInputError.setText(resId);
        textInputError.setVisibility(View.VISIBLE);
        textInputError.setBackgroundColor(Color.RED);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                textInputError.setVisibility(View.GONE);
            }
        }, delay);
    }

    private void showErrorGreen(int resId) {
        textInputError.setText(resId);
        textInputError.setVisibility(View.VISIBLE);
        textInputError.setBackgroundColor(Color.parseColor("#228B22"));
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                textInputError.setVisibility(View.GONE);
            }
        }, delay);
    }
}
