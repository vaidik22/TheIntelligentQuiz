package com.binplus.TheIntelligentQuiz.Fragments;

import static com.binplus.TheIntelligentQuiz.BaseURL.BaseURL.BASE_URL_IMAGE;
import static com.binplus.TheIntelligentQuiz.BaseURL.BaseURL.GET_BANNER;
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
import com.binplus.TheIntelligentQuiz.Adapters.QuizAdapter;
import com.binplus.TheIntelligentQuiz.Model.BannerModel;
import com.binplus.TheIntelligentQuiz.Model.QuizModel;
import com.binplus.TheIntelligentQuiz.R;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;



public class HomePage extends Fragment {

    private ImageSlider image_slider;
    private RecyclerView recyclerView;
    private QuizAdapter quizAdapter;
    private List<QuizModel.Datum> quizModelItemList;
    private ProgressBar progressBar;
    LinearLayout no_upcoming_contest_layout;
    TextView tv_message;

    public HomePage() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_page, container, false);
        initViews(view);
        progressBar.setVisibility(View.VISIBLE);
        callImageSliderApi();
        callUpcomingQuizApi("upcoming");
        recyclerView = view.findViewById(R.id.live_Quiz_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        quizModelItemList = new ArrayList<>();
        quizAdapter = new QuizAdapter(quizModelItemList, getActivity().getSupportFragmentManager());
        recyclerView.setAdapter(quizAdapter);
//        getActivity().getOnBackPressedDispatcher().addCallback(getActivity(), new OnBackPressedCallback(true) {
//            @Override
//            public void handleOnBackPressed() {
//                openDialogueExit();
//            }
//        });
        return view;
    }
//    private void openDialogueExit() {
//        Dialog dialog = new Dialog(getContext());
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.getWindow();
//        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        dialog.getWindow().setGravity(Gravity.CENTER);
//        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
//        dialog.setContentView(R.layout.dialog_exit);
//        Button btn_exit,btn_stay;
//
//        btn_exit = dialog.findViewById(R.id.btn_exit);
//        btn_stay = dialog.findViewById(R.id.btn_stay);
//
//        btn_exit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //close app
//                finishAffinity(getActivity());
//            }
//        });
//        btn_stay.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//            }
//        });
//
//
//        dialog.show();
//    }

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
//        Call<QuizModel> call = apiInterface.getContestApi(params);
//        call.enqueue(new Callback<QuizModel>() {
//            @Override
//            public void onResponse(Call<QuizModel> call, Response<QuizModel> response) {
//                if (response.isSuccessful() && response.body() != null) {
//                    QuizModel quizModel = response.body();
//                    List<QuizModel.Datum> data = quizModel.upcoming_contest.data;
//                    if(quizModel.getUpcoming_contest().getError().equalsIgnoreCase("true")){
//                        no_upcoming_contest_layout.setVisibility(View.VISIBLE);
//                        tv_message.setText(quizModel.getUpcoming_contest().getMessage());
//                    }
//                    else if (data != null) {
//                        no_upcoming_contest_layout.setVisibility(View.GONE);
//                        recyclerView.setVisibility(View.VISIBLE);
//                        quizModelItemList.clear();
//                        quizModelItemList.addAll(data);
//                        quizAdapter.notifyDataSetChanged();
//                        progressBar.setVisibility(View.GONE);
//                        for (QuizModel.Datum datum : data) {
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
//            public void onFailure(Call<QuizModel> call, Throwable t) {
//                Log.e("HomePage", "API call failed: " + t.getMessage());
//                progressBar.setVisibility(View.GONE);
//            }
//        });
//    }
private void callUpcomingQuizApi(String type) {
    progressBar.setVisibility(View.VISIBLE);
    SharedPreferences sharedPreferences = getContext().getSharedPreferences("UserSession", Context.MODE_PRIVATE);
    String authId = sharedPreferences.getString("userId", "Default Id");

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

    String url = GET_CONTEST;

    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, params,
            response -> {
                try {
                    Gson gson = new Gson();
                    QuizModel quizModel = gson.fromJson(response.toString(), QuizModel.class);
                    if (quizModel != null && quizModel.getUpcoming_contest().getError().equalsIgnoreCase("true")) {
                        no_upcoming_contest_layout.setVisibility(View.VISIBLE);
                        tv_message.setText(quizModel.getUpcoming_contest().getMessage());
                    } else {
                        List<QuizModel.Datum> data = quizModel.getUpcoming_contest().getData();
                        if (data != null) {
                            no_upcoming_contest_layout.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                            quizModelItemList.clear();
                            quizModelItemList.addAll(data);
                            quizAdapter.notifyDataSetChanged();
                        } else {
                            Log.e("HomePage", "No data available");
                        }
                    }
                    progressBar.setVisibility(View.GONE);
                } catch (Exception e) {
                    Log.e("HomePage", "Error parsing JSON response", e);
                    progressBar.setVisibility(View.GONE);
                }
            },
            error -> {
                Log.e("HomePage", "API call failed: " + error.toString());
                progressBar.setVisibility(View.GONE);
            }
    );

    Volley.newRequestQueue(getContext()).add(jsonObjectRequest);
}

    private void initViews(View view) {
        image_slider = view.findViewById(R.id.image_slider);
        progressBar = view.findViewById(R.id.progressBar);
        no_upcoming_contest_layout = view.findViewById(R.id.no_upcoming_contest_layout);
        tv_message = view.findViewById(R.id.tv_message);
    }

//    private void callImageSliderApi() {
//        Call<BannerModel> call = apiInterface.getBannerApi();
//        call.enqueue(new Callback<BannerModel>() {
//            @Override
//            public void onResponse(Call<BannerModel> call, Response<BannerModel> response) {
//                if (response.isSuccessful() && response.body() != null) {
//                    BannerModel bannerModel = response.body();
//                    ArrayList<BannerModel.Datum> data = bannerModel.getData();
//                    if (bannerModel.isResponse() && data != null) {
//                        List<SlideModel> slideModels = new ArrayList<>();
//                        for (BannerModel.Datum datum : data) {
//                            String imageUrl = BASE_URL_IMAGE + datum.getImage();
//                            Log.d("HomePage", "Image URL: " + imageUrl);
//                            slideModels.add(new SlideModel(imageUrl, ScaleTypes.FIT));
//                        }
//                        image_slider.setImageList(slideModels, ScaleTypes.FIT);
//                    } else {
//                        Log.e("HomePage", "Response data is not valid");
//                    }
//                } else {
//                    Log.e("HomePage", "Response not successful or body is null");
//                }
//            }
//
//            @Override
//            public void onFailure(Call<BannerModel> call, Throwable t) {
//                Log.e("HomePage", "API call failed: " + t.getMessage());
//            }
//        });
//    }
private void callImageSliderApi() {
    String url = GET_BANNER;

    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
            response -> {
                try {
                    Gson gson = new Gson();
                    BannerModel bannerModel = gson.fromJson(response.toString(), BannerModel.class);

                    if (bannerModel.isResponse() && bannerModel.getData() != null) {
                        ArrayList<BannerModel.Datum> data = bannerModel.getData();
                        List<SlideModel> slideModels = new ArrayList<>();

                        for (BannerModel.Datum datum : data) {
                            String imageUrl = BASE_URL_IMAGE + datum.getImage();
                            Log.d("HomePage", "Image URL: " + imageUrl);
                            slideModels.add(new SlideModel(imageUrl, ScaleTypes.FIT));
                        }

                        image_slider.setImageList(slideModels, ScaleTypes.FIT);
                    } else {
                        Log.e("HomePage", "Response data is not valid");
                    }
                } catch (Exception e) {
                    Log.e("HomePage", "Error parsing JSON response", e);
                }
            },
            error -> {
                Log.e("HomePage", "API call failed: " + error.toString());
            }
    );

    Volley.newRequestQueue(getContext()).add(jsonObjectRequest);
}

}
