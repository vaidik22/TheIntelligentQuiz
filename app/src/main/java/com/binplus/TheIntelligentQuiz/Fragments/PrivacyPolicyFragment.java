package com.binplus.TheIntelligentQuiz.Fragments;
import static com.binplus.TheIntelligentQuiz.BaseURL.BaseURL.getPrivacy;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.binplus.TheIntelligentQuiz.Model.CommonModel;
import com.binplus.TheIntelligentQuiz.R;
import com.google.gson.Gson;

import java.util.List;


public class PrivacyPolicyFragment extends Fragment {

    private TextView tvPrivacyPolicy;
    ProgressBar progressBar;

    public PrivacyPolicyFragment() {
        // Required empty public constructor
    }

    public static PrivacyPolicyFragment newInstance() {
        return new PrivacyPolicyFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_privacy_policy, container, false);
        tvPrivacyPolicy = view.findViewById(R.id.tv_privacy_policy);
        progressBar = view.findViewById(R.id.progressBar);
        fetchPrivacyPolicy();
        return view;
    }
//    private void fetchPrivacyPolicy() {
//        Call<CommonModel> call = apiService.getPrivacy();
//        call.enqueue(new Callback<CommonModel>() {
//            @Override
//            public void onResponse(Call<CommonModel> call, Response<CommonModel> response) {
//                if (response.isSuccessful() && response.body() != null) {
//                    List<CommonModel.Datum> dataList = response.body().getData();
//                    for (CommonModel.Datum data : dataList) {
//                        if ("1".equals(data.getId())) {
//                            tvPrivacyPolicy.setText(data.getMessage());
//                            tvPrivacyPolicy.setVisibility(View.VISIBLE);
//                            progressBar.setVisibility(View.GONE);
//                            break;
//                        }
//                    }
//                } else {
//                    progressBar.setVisibility(View.GONE);
//                    Toast.makeText(getActivity(), "Failed to retrieve data", Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<CommonModel> call, Throwable t) {
//                progressBar.setVisibility(View.GONE);
//                Toast.makeText(getActivity(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
//    }

    private void fetchPrivacyPolicy() {
        progressBar.setVisibility(View.VISIBLE);

        String url = getPrivacy;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        Gson gson = new Gson();
                        CommonModel commonModel = gson.fromJson(response.toString(), CommonModel.class);
                        List<CommonModel.Datum> dataList = commonModel.getData();

                        for (CommonModel.Datum data : dataList) {
                            if ("1".equals(data.getId())) {
                                tvPrivacyPolicy.setText(data.getMessage());
                                tvPrivacyPolicy.setVisibility(View.VISIBLE);
                                break;
                            }
                        }

                        progressBar.setVisibility(View.GONE);
                    } catch (Exception e) {
                        progressBar.setVisibility(View.GONE);
                        Log.e("fetchPrivacyPolicy", "Error parsing JSON response", e);
                        Toast.makeText(getActivity(), "Error parsing data", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    progressBar.setVisibility(View.GONE);
                    Log.e("fetchPrivacyPolicy", "API call failed: " + error.toString());
                    Toast.makeText(getActivity(), "Error: " + error.toString(), Toast.LENGTH_SHORT).show();
                }
        );

        // Adding the request to the queue
        Volley.newRequestQueue(getActivity()).add(jsonObjectRequest);
    }

}
