package com.binplus.TheIntelligentQuiz.Fragments;

import static com.binplus.TheIntelligentQuiz.BaseURL.BaseURL.GET_CONFIG;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.binplus.TheIntelligentQuiz.R;
import com.binplus.TheIntelligentQuiz.common.Common;
import com.binplus.TheIntelligentQuiz.retrofit.ConfigModel;
import com.google.gson.Gson;




public class SupportFragment extends Fragment {

ImageView whatsaap_icon;
ImageView instagram_icon;
ImageView facebook_icon;
ImageView twitter_icon;
Common common;
String instagramLink;
String facebookUrl;
String twitterLink;

TextView txtcopypaste;

    public SupportFragment() {
        // Required empty public constructor
    }


    public static SupportFragment newInstance(String param1, String param2) {
        SupportFragment fragment = new SupportFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        common = new  Common(getActivity());
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_support, container, false);
        initView(view);
        allClicks();
        fetchConfigData();
        fetchConfigDataFaceBook();
        fetchConfigDataTwitter();
    return  view;
    }

    private void allClicks() {
        whatsaap_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
                whatsappIntent.setType("text/plain");
                whatsappIntent.setPackage("com.whatsapp");
                whatsappIntent.putExtra(Intent.EXTRA_TEXT, "Hello!");
                try {
                    getActivity().startActivity(whatsappIntent);
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(getContext(), "Whatsapp have not been installed.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        instagram_icon.setOnClickListener(new View.OnClickListener() {
                                              @Override
                                              public void onClick(View v) {
                                                callInstagramLink();
                                              }
                                          });
        facebook_icon.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callFaceBookLink();
            }
        }));
        twitter_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callTwitterLink();
            }
        });
    }

//    private void getFaceBookLink() {
//        if (isAppInstalled()) {
//            Toast.makeText(getApplicationContext(), "facebook app already installed", Toast.LENGTH_SHORT).show();
//            Intent facebookIntent = new Intent(Intent.ACTION_VIEW);
//            facebookIntent.setData(Uri.parse(facebookUrl));
//            startActivity(facebookIntent);
//
//        } else {
//            Toast.makeText(getApplicationContext(), "facebook app not installing", Toast.LENGTH_SHORT).show();
//        }
//    }
//    public boolean isAppInstalled() {
//        try {
//            getApplicationContext().getPackageManager().getApplicationInfo("com.facebook.katana", 0);
//            return true;
//        } catch (PackageManager.NameNotFoundException e) {
//            return false;
//        }
//    }

//    private void fetchConfigData() {
//        Call<ConfigModel> call = apiService.getIndexApi();
//        call.enqueue(new Callback<ConfigModel>() {
//            @Override
//            public void onResponse(Call<ConfigModel> call, Response<ConfigModel> response) {
//                if (response.isSuccessful() && response.body() != null) {
//                    ConfigModel configModel = response.body();
//                    if (configModel.getData() != null) {
//                        instagramLink = configModel.getData().getInstagram_link();
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ConfigModel> call, Throwable t) {
//                // Handle the error
//            }
//        });
//    }
//private void fetchConfigDataFaceBook() {
//        Call<ConfigModel> call = apiService.getIndexApi();
//        call.enqueue(new Callback<ConfigModel>() {
//            @Override
//            public void onResponse(Call<ConfigModel> call, Response<ConfigModel> response) {
//                if (response.isSuccessful() && response.body() != null) {
//                    ConfigModel configModel = response.body();
//                    if (configModel.getData() != null) {
//                        facebookUrl = configModel.getData().getFacebook_link();
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ConfigModel> call, Throwable t) {
//                // Handle the error
//            }
//        });
//    }
//private void fetchConfigDataTwitter() {
//        Call<ConfigModel> call = apiService.getIndexApi();
//        call.enqueue(new Callback<ConfigModel>() {
//            @Override
//            public void onResponse(Call<ConfigModel> call, Response<ConfigModel> response) {
//                if (response.isSuccessful() && response.body() != null) {
//                    ConfigModel configModel = response.body();
//                    if (configModel.getData() != null) {
//                         twitterLink= configModel.getData().getTwitter_link();
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ConfigModel> call, Throwable t) {
//                // Handle the error
//            }
//        });
//    }

    private void fetchConfigData() {
        String url = GET_CONFIG;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        Gson gson = new Gson();
                        ConfigModel configModel = gson.fromJson(response.toString(), ConfigModel.class);

                        if (configModel != null && configModel.getData() != null) {
                            instagramLink = configModel.getData().getInstagram_link();

                        }
                    } catch (Exception e) {
                        e.printStackTrace();

                    }
                },
                error -> {
                    Log.e("FetchConfigData", "API call failed: " + error.toString());

                }
        );

        Volley.newRequestQueue(getContext()).add(jsonObjectRequest);
    }

    private void fetchConfigDataFaceBook() {
        String url = GET_CONFIG;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        Gson gson = new Gson();
                        ConfigModel configModel = gson.fromJson(response.toString(), ConfigModel.class);

                        if (configModel != null && configModel.getData() != null) {
                            facebookUrl = configModel.getData().getFacebook_link();

                        }
                    } catch (Exception e) {
                        e.printStackTrace();

                    }
                },
                error -> {
                    Log.e("FetchConfigDataFaceBook", "API call failed: " + error.toString());

                }
        );

        Volley.newRequestQueue(getContext()).add(jsonObjectRequest);
    }

    private void fetchConfigDataTwitter() {
        String url = GET_CONFIG;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        Gson gson = new Gson();
                        ConfigModel configModel = gson.fromJson(response.toString(), ConfigModel.class);

                        if (configModel != null && configModel.getData() != null) {
                            twitterLink = configModel.getData().getTwitter_link();

                        }
                    } catch (Exception e) {
                        e.printStackTrace();

                    }
                },
                error -> {
                    Log.e("FetchConfigDataTwitter", "API call failed: " + error.toString());

                }
        );

        Volley.newRequestQueue(getContext()).add(jsonObjectRequest);
    }

    private void callInstagramLink() {
        if (instagramLink != null && !instagramLink.isEmpty()) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(instagramLink));
            startActivity(intent);
        } else {

        }
    }
 private void callFaceBookLink() {
        if (facebookUrl != null && !facebookUrl.isEmpty()) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(facebookUrl));
            startActivity(intent);
        } else {

        }
    }

    private void callTwitterLink() {
        if (twitterLink != null && !twitterLink.isEmpty()) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(twitterLink));
            startActivity(intent);
        } else {

        }
    }




    private void initView(View view) {
        whatsaap_icon = view.findViewById(R.id.whatsaap_icon);
        instagram_icon = view.findViewById(R.id.instagram_icon);
        facebook_icon = view.findViewById(R.id.facebook_icon);
        twitter_icon = view.findViewById(R.id.twitter_icon);
        txtcopypaste = view.findViewById(R.id.txtcopypaste);

    }
}