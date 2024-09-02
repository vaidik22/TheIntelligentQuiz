package com.binplus.TheIntelligentQuiz.Fragments;

import static com.binplus.TheIntelligentQuiz.BaseURL.BaseURL.GET_CONTEST;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.binplus.TheIntelligentQuiz.Adapters.QuizAdapterPast;
import com.binplus.TheIntelligentQuiz.Model.PastModel;
import com.binplus.TheIntelligentQuiz.R;
import com.denzcoskun.imageslider.ImageSlider;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;



public class CompletedQuizFragment extends Fragment {

    private ImageSlider image_slider;
    private RecyclerView recyclerView;
    private QuizAdapterPast quizAdapter;
    private List<PastModel.Datum> pastModelItemList;
    private ProgressBar progressBar;
    LinearLayout no_upcoming_contest_layout;
    TextView tv_message;

    public CompletedQuizFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_completed_quiz, container, false);
        initViews(view);
        progressBar.setVisibility(View.VISIBLE);
        callUpcomingQuizApi("past");
        recyclerView = view.findViewById(R.id.completed_Quiz_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        pastModelItemList = new ArrayList<>();
        quizAdapter = new QuizAdapterPast(pastModelItemList, getActivity().getSupportFragmentManager());
        recyclerView.setAdapter(quizAdapter);
        return view;
    }

//    private void callUpcomingQuizApi(String type) {
//        progressBar.setVisibility(View.VISIBLE);
//        JsonObject params = new JsonObject();
//        SharedPreferences sharedPreferences = getContext().getSharedPreferences("UserSession", Context.MODE_PRIVATE);
//        String authId = sharedPreferences.getString("userId", "Default Id");
//
//        if (type.equalsIgnoreCase("live")) {
//            params.addProperty("key", "1");
//        }  if (type.equalsIgnoreCase("upcoming")) {
//            params.addProperty("key", "0");
//        }  if (type.equalsIgnoreCase("past")) {
//            params.addProperty("key", "2");
//        }
//        params.addProperty("user_id", authId);
//        params.addProperty("authId", authId);
//
//        Call<PastModel> call = apiInterface.getContestApiPast(params);
//        call.enqueue(new Callback<PastModel>() {
//            @Override
//            public void onResponse(Call<PastModel> call, Response<PastModel> response) {
//                if (response.isSuccessful() && response.body() != null) {
//                    PastModel pastModel = response.body();
//                    List<PastModel.Datum> data = pastModel.past_contest.data;
//                    if(pastModel.getPast_contest().error){
//                        no_upcoming_contest_layout.setVisibility(View.VISIBLE);
//                        tv_message.setText(pastModel.getPast_contest().getMessage());
//                    }
//                    else if (data != null) {
//                        no_upcoming_contest_layout.setVisibility(View.GONE);
//                        recyclerView.setVisibility(View.VISIBLE);
//                        pastModelItemList.clear();
//                        pastModelItemList.addAll(data);
//                        quizAdapter.notifyDataSetChanged();
//                        progressBar.setVisibility(View.GONE);
//                        for (PastModel.Datum datum : data) {
//                            Log.d("HomePagewefwef", "Datum: " + data);
//                        }
//                    } else {
//                        Log.e("HomePage", "No data available");
//                        progressBar.setVisibility(View.GONE);
//                    }
//                } else {
//                    Log.e("HomePage", "Response not successful or body is null");
//                    progressBar.setVisibility(View.GONE);
//                }
//            }
//
//            @Override
//            public void onFailure(Call<PastModel> call, Throwable t) {
//                Log.e("HomePage", "API call failed: " + t.getMessage());
//                progressBar.setVisibility(View.GONE);
//            }
//        });
//    }
private void callUpcomingQuizApi(String type) {
    progressBar.setVisibility(View.VISIBLE);
    SharedPreferences sharedPreferences = getContext().getSharedPreferences("UserSession", Context.MODE_PRIVATE);
    String authId = sharedPreferences.getString("userId", "Default Id");

    String url = GET_CONTEST;

    // Setting up parameters for the request
    JSONObject params = new JSONObject();
    try {
        if (type.equalsIgnoreCase("live")) {
            params.put("key", "1");
        } else if (type.equalsIgnoreCase("upcoming")) {
            params.put("key", "0");
        } else if (type.equalsIgnoreCase("past")) {
            params.put("key", "2");
        }
        params.put("user_id", authId);
        params.put("authId", authId);
    } catch (JSONException e) {
        e.printStackTrace();
    }

    // Create a JsonObjectRequest
    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, params,
            response -> {
                progressBar.setVisibility(View.GONE);
                try {
                    Gson gson = new Gson();
                    PastModel pastModel = gson.fromJson(response.toString(), PastModel.class);

                    if (pastModel.getPast_contest().error) {
                        no_upcoming_contest_layout.setVisibility(View.VISIBLE);
                        tv_message.setText(pastModel.getPast_contest().getMessage());
                    } else {
                        List<PastModel.Datum> data = pastModel.past_contest.data;
                        if (data != null) {
                            no_upcoming_contest_layout.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                            pastModelItemList.clear();
                            pastModelItemList.addAll(data);
                            quizAdapter.notifyDataSetChanged();
                            for (PastModel.Datum datum : data) {
                                Log.d("HomePage", "Datum: " + datum.toString());
                            }
                        } else {
                            Log.e("HomePage", "No data available");
                        }
                    }
                } catch (Exception e) {
                    Log.e("HomePage", "Error parsing JSON response", e);
                }
            },
            error -> {
                Log.e("HomePage", "API call failed: " + error.toString());
                progressBar.setVisibility(View.GONE);
            }
    );

    // Adding the request to the queue
    Volley.newRequestQueue(getContext()).add(jsonObjectRequest);
}

    private void initViews(View view) {
        image_slider = view.findViewById(R.id.image_slider);
        progressBar = view.findViewById(R.id.progressBar);
        no_upcoming_contest_layout = view.findViewById(R.id.no_upcoming_contest_layout);
        tv_message = view.findViewById(R.id.tv_message);
    }

}
