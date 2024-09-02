package com.binplus.TheIntelligentQuiz.Fragments;

import static com.binplus.TheIntelligentQuiz.BaseURL.BaseURL.SEND_WITHDRAWAL_REQUEST;
import static com.binplus.TheIntelligentQuiz.BaseURL.BaseURL.WALLET_HISTORY;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.binplus.TheIntelligentQuiz.Adapters.TransactionAdapter;
import com.binplus.TheIntelligentQuiz.Model.TransactionModel;
import com.binplus.TheIntelligentQuiz.Model.WithdrawModel;
import com.binplus.TheIntelligentQuiz.R;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;




public class WithdrawFragment extends Fragment {

    private RecyclerView recyclerView;
    private TransactionAdapter adapter;
    TextView textInputError;
    long delay = 3000;
    private ArrayList<TransactionModel.Datum> transactionList;
    private ArrayList<WithdrawModel> withdrawalList;
     
    String key = "2";
    TextView available_balance;;
    EditText et_money;
    TextView tv_add_money;

    public WithdrawFragment() {
        // Required empty public constructor
    }

    public static WithdrawalWalletTransFragment newInstance(String param1, String param2) {
        WithdrawalWalletTransFragment fragment = new WithdrawalWalletTransFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         
        transactionList = new ArrayList<>();
        withdrawalList = new ArrayList<>();
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_withdraw, container, false);
        available_balance = view.findViewById(R.id.available_balance);
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        String balance = sharedPreferences.getString("wallet_balance", "0");
        available_balance.setText("Rs."+balance);
        textInputError = view.findViewById(R.id.textinput_error);
        et_money = view.findViewById(R.id.et_money);
        tv_add_money = view.findViewById(R.id.tv_add_money);
        recyclerView = view.findViewById(R.id.rev_withdraw);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new TransactionAdapter(transactionList, "withdrawal");
        recyclerView.setAdapter(adapter);
        tv_add_money.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(et_money.getText().toString().isEmpty()) {
                    showError("Please Enter Amount");
                }else {
                    sendWithdrawRequest();
                }
            }
        });
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
//                        showError("No Data Found");
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
                            showError("No Data Found");
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
//    private void sendWithdrawRequest() {
//        withdrawalList.clear();
//        JsonObject postData = new JsonObject();
//        SharedPreferences sharedPreferences = getContext().getSharedPreferences("UserSession", Context.MODE_PRIVATE);
//        String authId = sharedPreferences.getString("userId", "Default Id");
//        postData.addProperty("user_id", authId);
//        postData.addProperty("request_amount",et_money.getText().toString());
//
//        Call<WithdrawModel> call = apiInterface.getWithdrawalRequestApi(postData);
//        call.enqueue(new Callback<WithdrawModel>() {
//            @Override
//            public void onResponse(@NonNull Call<WithdrawModel> call, @NonNull Response<WithdrawModel> response) {
//                if (response.isSuccessful() && response.body() != null) {
//                    WithdrawModel updateProfileModel = response.body();
//                    String message = updateProfileModel.getMessage();
//                     showError(message);
//                }
//            }
//
//            @Override
//            public void onFailure(@NonNull Call<WithdrawModel> call, @NonNull Throwable t) {
//                // Handle failure
//            }
//        });
//    }
private void sendWithdrawRequest() {
    withdrawalList.clear();

    SharedPreferences sharedPreferences = getContext().getSharedPreferences("UserSession", Context.MODE_PRIVATE);
    String authId = sharedPreferences.getString("userId", "Default Id");
    String requestAmount = et_money.getText().toString();

    String url = SEND_WITHDRAWAL_REQUEST;
    JSONObject postData = new JSONObject();
    try {
        postData.put("user_id", authId);
        postData.put("request_amount", requestAmount);
    } catch (JSONException e) {
        e.printStackTrace();
        Toast.makeText(getContext(), "Error creating request data", Toast.LENGTH_SHORT).show();
        return;
    }

    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, postData,
            response -> {
                try {
                    // Parse the response
                    Gson gson = new Gson();
                    WithdrawModel withdrawModel = gson.fromJson(response.toString(), WithdrawModel.class);
                    String message = withdrawModel.getMessage();
                    showError(message);
                } catch (Exception e) {
                    Log.e("sendWithdrawRequest", "Error parsing JSON response", e);
                    Toast.makeText(getContext(), "Error parsing data", Toast.LENGTH_SHORT).show();
                }
            },
            error -> {
                Log.e("sendWithdrawRequest", "API call failed: " + error.toString());
                Toast.makeText(getContext(), "Error: " + error.toString(), Toast.LENGTH_SHORT).show();
            }
    );

    Volley.newRequestQueue(getContext()).add(jsonObjectRequest);
}

    public void showError(String resId) {
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
