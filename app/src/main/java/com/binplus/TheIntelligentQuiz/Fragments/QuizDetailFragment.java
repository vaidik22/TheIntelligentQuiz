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
import com.binplus.TheIntelligentQuiz.Adapters.DetailQuizAdapter;
import com.binplus.TheIntelligentQuiz.Adapters.WinningListRankAdapter;
import com.binplus.TheIntelligentQuiz.Model.QuizDetailModel;
import com.binplus.TheIntelligentQuiz.Model.QuizModel;
import com.binplus.TheIntelligentQuiz.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class QuizDetailFragment extends Fragment implements DetailQuizAdapter.OnFillButtonClickListener {

    private RecyclerView recyclerView;
    private DetailQuizAdapter quizAdapter;
    private List<QuizDetailModel.Datum> quizModelItemList;
    private static final String ARG_ID = "id";
    private List<QuizDetailModel.CurrentFill> quizList2;
    private RecyclerView recyclerViewWinning;
    private WinningListRankAdapter quizAdapterWinning;
    private LinearLayout progressBar;
    private LinearLayout main;
    private String id;

    public QuizDetailFragment() {
        // Required empty public constructor
    }
    private void saveIdToPreferences(String id) {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("contest_id", id);
        editor.apply();
    }
    public static QuizDetailFragment newInstance(QuizModel.Datum quizModel) {
        QuizDetailFragment fragment = new QuizDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ID, quizModel.getId());
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
        View view = inflater.inflate(R.layout.fragment_quiz_detail, container, false);
        progressBar = view.findViewById(R.id.progress_bar);
        main = view.findViewById(R.id.main);
        recyclerViewWinning = view.findViewById(R.id.rev_winning_list);
        recyclerViewWinning.setLayoutManager(new LinearLayoutManager(recyclerViewWinning.getContext()));
        quizList2 = new ArrayList<>();
        quizAdapterWinning = new WinningListRankAdapter(quizList2);
        recyclerViewWinning.setAdapter(quizAdapterWinning);
        callUpcomingQuizDetailApi();
        quizModelItemList = new ArrayList<>();
        recyclerView = view.findViewById(R.id.rev_detail_contest);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        quizAdapter = new DetailQuizAdapter(quizModelItemList, this,getActivity().getSupportFragmentManager());
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
        JSONObject params = new JSONObject();
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        String authId = sharedPreferences.getString("userId", "Default Id");

        try {
            params.put("user_id", authId);
            params.put("contest_id", id);
        } catch (JSONException e) {
            e.printStackTrace();
            hideLoading();
            return;
        }

        String url = GET_CONTEST_DETAIL;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, params,
                response -> {
                    hideLoading();
                    try {
                        if (response != null && response.has("data")) {
                            JSONArray dataArray = response.getJSONArray("data");


                            quizModelItemList.clear();
                            for (int i = 0; i < dataArray.length(); i++) {
                                JSONObject dataObject = dataArray.getJSONObject(i);
                                QuizDetailModel.Datum datum = new QuizDetailModel.Datum();
                                datum.setId(dataObject.optString("id"));
                                datum.setName(dataObject.optString("name"));
                                datum.setStart_date(dataObject.optString("start_date"));
                                datum.setEnd_date(dataObject.optString("end_date"));
                                datum.setDescription(dataObject.optString("description"));
                                datum.setImage(dataObject.optString("image"));
                                datum.setEntry(dataObject.optString("entry"));
                                datum.setMax_entry(dataObject.optString("max_entry"));
                                datum.setJoin_spot(dataObject.optString("join_spot"));
                                datum.setAvailable_spot(dataObject.optString("available_spot"));
                                datum.setContest_status(dataObject.optString("contest_status"));
                                datum.setPrize_pool(dataObject.optString("prize_pool"));
                                datum.setDate_created(dataObject.optString("date_created"));
                                datum.setTop_users(dataObject.optString("top_users"));
                                datum.setParticipants(dataObject.optString("participants"));
                                datum.setJoin_contest_status(dataObject.optInt("join_contest_status"));
                                datum.setComplete_status(dataObject.optInt("complete_status"));
                                JSONArray pointsArray = dataObject.optJSONArray("points");
                                ArrayList<QuizDetailModel.Point> pointsList = new ArrayList<>();
                                if (pointsArray != null) {
                                    for (int j = 0; j < pointsArray.length(); j++) {
                                        JSONObject pointObject = pointsArray.getJSONObject(j);
                                        QuizDetailModel.Point point = new QuizDetailModel.Point();
                                        point.setTop_winner(pointObject.optString("top_winner"));
                                        point.setPoints(pointObject.optString("points"));
                                        pointsList.add(point);
                                    }
                                }
                                datum.setPoints(pointsList);
                                JSONArray currentFillArray = dataObject.optJSONArray("current_fill");
                                ArrayList<QuizDetailModel.CurrentFill> currentFillList = new ArrayList<>();
                                if (currentFillArray != null) {
                                    for (int k = 0; k < currentFillArray.length(); k++) {
                                        JSONObject fillObject = currentFillArray.getJSONObject(k);
                                        QuizDetailModel.CurrentFill currentFill = new QuizDetailModel.CurrentFill();
                                        currentFill.setTop_winner(fillObject.optString("top_winner"));
                                        currentFill.setPoints(fillObject.optString("points"));
                                        currentFillList.add(currentFill);
                                    }
                                }
                                datum.setCurrent_fill(currentFillList);

                                quizModelItemList.add(datum);
                            }
                            quizAdapter.notifyDataSetChanged();
                            List<QuizDetailModel.CurrentFill> currentFillList = extractCurrentFillList(quizModelItemList);
                            quizList2.clear();
                            quizList2.addAll(currentFillList);
                            quizAdapterWinning.notifyDataSetChanged();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e("QuizDetailFragment", "Error parsing response: " + e.getMessage());
                    }
                },
                error -> {
                    hideLoading();
                    Log.e("QuizDetailFragment", "API call failed: " + error.getMessage());
                }
        );
        Volley.newRequestQueue(getContext()).add(jsonObjectRequest);
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

    @Override
    public void onMaxFillClick(List<QuizDetailModel.Point> points) {
        List<QuizDetailModel.CurrentFill> maxFillList = new ArrayList<>();
        for (QuizDetailModel.Point point : points) {
            QuizDetailModel.CurrentFill currentFill = new QuizDetailModel.CurrentFill();
            currentFill.setTop_winner(point.getTop_winner());
            currentFill.setPoints(point.getPoints());
            maxFillList.add(currentFill);
        }
        quizList2.clear();
        quizList2.addAll(maxFillList);
        quizAdapterWinning.notifyDataSetChanged();
    }

    @Override
    public void onCurrentFillClick(List<QuizDetailModel.CurrentFill> currentFills) {
        quizList2.clear();
        quizList2.addAll(currentFills);
        quizAdapterWinning.notifyDataSetChanged();
    }
}
