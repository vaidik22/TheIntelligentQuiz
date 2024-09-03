package com.binplus.TheIntelligentQuiz.Fragments;


import static com.binplus.TheIntelligentQuiz.BaseURL.BaseURL.GET_CONTEST_DETAIL;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.binplus.TheIntelligentQuiz.Adapters.DetailQuizAdapterPast;
import com.binplus.TheIntelligentQuiz.Adapters.WinningListRankAdapter;
import com.binplus.TheIntelligentQuiz.Model.PastModel;
import com.binplus.TheIntelligentQuiz.Model.QuizDetailModel;
import com.binplus.TheIntelligentQuiz.R;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class QuizDetailsPastFragment extends Fragment  {

    private RecyclerView recyclerView;
    private DetailQuizAdapterPast quizAdapter;
    private List<QuizDetailModel.Datum> quizModelItemList;
    private static final String ARG_ID = "id";
    private List<QuizDetailModel.CurrentFill> quizList2;
   // private RecyclerView recyclerViewWinning;
    private WinningListRankAdapter quizAdapterWinning;
    private LinearLayout progressBar;
    private LinearLayout main;
    private String id;

    public QuizDetailsPastFragment() {
        // Required empty public constructor
    }
    private void saveIdToPreferences(String id) {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("contest_id", id);
        editor.apply();
    }
    public static QuizDetailsPastFragment newInstance(PastModel.Datum pastModel) {
        QuizDetailsPastFragment fragment = new QuizDetailsPastFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ID, pastModel.getId());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         
        if (getArguments() != null) {
            id = getArguments().getString(ARG_ID);
            saveIdToPreferences(id);
        }
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_quiz_details_past, container, false);
        progressBar = view.findViewById(R.id.progress_bar);
        main = view.findViewById(R.id.main);
        //recyclerViewWinning = view.findViewById(R.id.rev_winning_list);
       // recyclerViewWinning.setLayoutManager(new LinearLayoutManager(recyclerViewWinning.getContext()));
        quizList2 = new ArrayList<>();
        quizAdapterWinning = new WinningListRankAdapter(quizList2);
       // recyclerViewWinning.setAdapter(quizAdapterWinning);
        callUpcomingQuizDetailApi();
        quizModelItemList = new ArrayList<>();
        recyclerView = view.findViewById(R.id.rev_detail_contest);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        quizAdapter = new DetailQuizAdapterPast(quizModelItemList, getActivity().getSupportFragmentManager());
        recyclerView.setAdapter(quizAdapter);
        return view;
    }
    private void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
        main.setVisibility(View.GONE);

    }

    private void hideLoading() {
        progressBar.setVisibility(View.GONE);
        main.setVisibility(View.VISIBLE);
    }

//    private void callUpcomingQuizDetailApi() {
//        showLoading();
//        JsonObject params = new JsonObject();
//        SharedPreferences sharedPreferences = getContext().getSharedPreferences("UserSession", Context.MODE_PRIVATE);
//        String authId = sharedPreferences.getString("userId", "Default Id");
//
//        params.addProperty("user_id", authId);
//        params.addProperty("contest_id", id);
//
//        Call<QuizDetailModel> call = apiInterface.getContestDetailApi(params);
//        call.enqueue(new Callback<QuizDetailModel>() {
//            @Override
//            public void onResponse(Call<QuizDetailModel> call, Response<QuizDetailModel> response) {
//                hideLoading();
//                if (response.isSuccessful() && response.body() != null) {
//                    QuizDetailModel quizModel = response.body();
//                    ArrayList<QuizDetailModel.Datum> data = quizModel.getData();
//                    quizModelItemList.clear();
//                    quizModelItemList.addAll(data);
//                    quizAdapter.notifyDataSetChanged();
//                    List<QuizDetailModel.CurrentFill> currentFillList = extractCurrentFillList(data);
//                    quizList2.clear();
//                    quizList2.addAll(currentFillList);
//                    quizAdapterWinning.notifyDataSetChanged();
//                } else {
//                    Log.e("QuizDetailFragment", "API call unsuccessful");
//                }
//            }
//
//            @Override
//            public void onFailure(Call<QuizDetailModel> call, Throwable t) {
//                hideLoading();
//                Log.e("QuizDetailFragment", "API call failed: " + t.getMessage());
//            }
//        });
//    }
private void callUpcomingQuizDetailApi() {
    showLoading();
    String url = GET_CONTEST_DETAIL;

    // Create a JSONObject for the parameters
    JSONObject params = new JSONObject();
    try {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        String authId = sharedPreferences.getString("userId", "Default Id");

        params.put("user_id", authId);
        params.put("contest_id", id);
    } catch (JSONException e) {
        e.printStackTrace();
        hideLoading();
        return;
    }

    // Create a JsonObjectRequest
    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, params,
            response -> {
                hideLoading();
                try {
                    Gson gson = new Gson();
                    QuizDetailModel quizModel = gson.fromJson(response.toString(), QuizDetailModel.class);
                    if (quizModel != null && quizModel.getData() != null) {
                        ArrayList<QuizDetailModel.Datum> data = quizModel.getData();
                        quizModelItemList.clear();
                        quizModelItemList.addAll(data);
                        quizAdapter.notifyDataSetChanged();

                        // Extract only the "max fill" data
                        List<QuizDetailModel.CurrentFill> maxFillList = extractMaxFillList(data);
                        quizList2.clear();
                        quizList2.addAll(maxFillList);
                        quizAdapterWinning.notifyDataSetChanged();
                    }
                } catch (Exception e) {
                    Log.e("QuizDetailFragment", "JSON parsing error: " + e.getMessage());
                }
            },
            error -> {
                hideLoading();
                Log.e("QuizDetailFragment", "API call failed: " + error.getMessage());
            }
    );

    // Add the request to the RequestQueue
    Volley.newRequestQueue(getContext()).add(jsonObjectRequest);
}

    private List<QuizDetailModel.CurrentFill> extractMaxFillList(List<QuizDetailModel.Datum> datumList) {
        List<QuizDetailModel.CurrentFill> maxFillList = new ArrayList<>();
        for (QuizDetailModel.Datum datum : datumList) {
            if (datum.getPoints() != null) {
                for (QuizDetailModel.Point point : datum.getPoints()) {
                    QuizDetailModel.CurrentFill currentFill = new QuizDetailModel.CurrentFill();
                    currentFill.setTop_winner(point.getTop_winner());
                    currentFill.setPoints(point.getPoints());
                    maxFillList.add(currentFill);
                }
            }
        }
        return maxFillList;
    }


    private List<QuizDetailModel.CurrentFill> extractCurrentFillList(List<QuizDetailModel.Datum> datumList) {
        List<QuizDetailModel.CurrentFill> currentFillList = new ArrayList<>();
        for (QuizDetailModel.Datum datum : datumList) {
            if (datum.getPoints() != null) {
                for (QuizDetailModel.Point point : datum.getPoints()) {
                    QuizDetailModel.CurrentFill currentFill = new QuizDetailModel.CurrentFill();
                    currentFill.setTop_winner(point.getTop_winner());
                    currentFill.setPoints(point.getPoints());
                    currentFillList.add(currentFill);
                }
            }

            if (datum.getCurrent_fill() != null) {
                currentFillList.addAll(datum.getCurrent_fill());
            }
        }
        return currentFillList;
    }
}
