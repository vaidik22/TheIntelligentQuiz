package com.binplus.TheIntelligentQuiz.Fragments;

import static android.content.Context.MODE_PRIVATE;
import static com.binplus.TheIntelligentQuiz.BaseURL.BaseURL.BASE_URL_IMAGE;
import static com.binplus.TheIntelligentQuiz.BaseURL.BaseURL.GET_CONFIG;
import static com.binplus.TheIntelligentQuiz.BaseURL.BaseURL.GET_PROFILE;
import static com.binplus.TheIntelligentQuiz.BaseURL.BaseURL.UPDATE_PROFILE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.binplus.TheIntelligentQuiz.Activity.MainActivity;
import com.binplus.TheIntelligentQuiz.Model.ProfileModel;
import com.binplus.TheIntelligentQuiz.Model.UpdateProfileModel;
import com.binplus.TheIntelligentQuiz.R;
import com.binplus.TheIntelligentQuiz.retrofit.ConfigModel;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;



public class ProfileFragment extends Fragment {
    ImageView instagram_icon;
    ImageView facebook_icon;
    ImageView twitter_icon;
    String instagramLink;
    String facebookUrl;
    String twitterLink;
    TextView tv_logout;
    LinearLayout layout_basic,layout_basic_edit,layout_social,layout_social_media,layout_refer,layout_basic_refer,layout_bank;
    ImageView img_down_basic,img_down_social,img_down_refer;
    FrameLayout editProfileImg;
    CircleImageView iv_cir;
    ArrayList<ProfileModel.Data> profileList = new ArrayList<>();
    ArrayList<UpdateProfileModel.Data> updatedProfileList = new ArrayList<>();
     
    TextView tv_name,tv_user_mobile,tv_total_played,tv_total_spent,tv_total_earned;
    EditText username,mobile_number,email,age,state,district,address,et_facebook,whatsaap,et_refer;
    Button btn_submit_basic,btn_submit_social,btn_submit_refer;
    String base64Image;
    UpdateProfileModel updateProfileModel;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 101;
    private static final int PICK_IMAGE_REQUEST = 1;
    private CircleImageView img_profile_dialog;
    private Bitmap selectedBitmap;

    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

         updateProfileModel = new UpdateProfileModel();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        initview(view);
        fetchProfileDetails();
        allClick();
        fetchConfigData();
        fetchConfigDataFaceBook();
        fetchConfigDataTwitter();
        return  view;
    }

    private void allClick() {
        layout_basic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (layout_basic_edit.getVisibility() == View.VISIBLE) {
                    layout_basic_edit.setVisibility(View.GONE);
                    img_down_basic.animate().rotation(0).setDuration(300).start();
                } else {
                    layout_basic_edit.setVisibility(View.VISIBLE);
                    img_down_basic.animate().rotation(180).setDuration(300).start();
                }
            }
        });
        layout_social.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(layout_social_media.getVisibility() == View.VISIBLE){
                    layout_social_media.setVisibility(View.GONE);
                    img_down_social.animate().rotation(0).setDuration(300).start();
                }else {
                    layout_social_media.setVisibility(View.VISIBLE);
                    img_down_social.animate().rotation(180).setDuration(300).start();
                }
            }
        });
        layout_refer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(layout_basic_refer.getVisibility() == View.VISIBLE){
                    layout_basic_refer.setVisibility(View.GONE);
                    img_down_refer.animate().rotation(0).setDuration(300).start();
                }else {
                    layout_basic_refer.setVisibility(View.VISIBLE);
                    img_down_refer.animate().rotation(180).setDuration(300).start();
                }
            }
        });
        editProfileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImagePickerDialogue();
            }
        });
        btn_submit_basic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile(base64Image);
            }
        });
        layout_bank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //open bank details fragment
                requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.homeFragment,new BankDetails())
                        .addToBackStack(null).commit();
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
        tv_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLogoutDialog();
            }
        });
    }

    private void openImagePickerDialogue() {
        Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dialog.setContentView(R.layout.dialoge_edit_profile);

        img_profile_dialog = dialog.findViewById(R.id.img_profile_dialog);
        ImageView img_close_dialog = dialog.findViewById(R.id.img_close_dialog);
        Button btn_upload = dialog.findViewById(R.id.btn_upload);

        img_profile_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    openImagePicker();
                } else {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
                }
            }
        });

        btn_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedBitmap != null) {
                    String base64Image = convertBitmapToBase64(selectedBitmap);
                    iv_cir.setImageBitmap(selectedBitmap); // Set the selected image to the main ImageView
                    updateProfile(base64Image); // Call API to update profile
                    dialog.dismiss(); // Close the dialog
                } else {
                    Toast.makeText(getContext(), "Please select an image first", Toast.LENGTH_SHORT).show();
                }
            }
        });

        img_close_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }


    private void openImagePicker() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryIntent.setType("image/*");
        Intent chooserIntent = Intent.createChooser(galleryIntent, "Select Image");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{cameraIntent});

        if (cameraIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
            startActivityForResult(chooserIntent, PICK_IMAGE_REQUEST);
        } else {
            // If no camera app is available, show the gallery picker
            startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == getActivity().RESULT_OK && requestCode == PICK_IMAGE_REQUEST) {
            if (data != null) {
                Bitmap bitmap = null;
                if (data.getData() != null) {
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), data.getData());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (data.getExtras() != null) {
                    bitmap = (Bitmap) data.getExtras().get("data");
                }

                if (bitmap != null) {
                    selectedBitmap = bitmap;
                    img_profile_dialog.setImageBitmap(selectedBitmap); // Set the image to the dialog's ImageView
                }
            }
        }
    }

    private String convertBitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            return Base64.encodeToString(byteArray, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                byteArrayOutputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openImagePicker();
        }
    }

    private void openLogoutDialog(){
    Dialog dialog;
    dialog = new Dialog (getActivity ());
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
    dialog.getWindow();
    dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    dialog.getWindow().setGravity(Gravity.CENTER);
    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
    dialog.setContentView (R.layout.dialoge_logout);
    Button btn_leave,btn_stay;
    btn_stay=dialog.findViewById (R.id.btn_stay);
    btn_leave=dialog.findViewById (R.id.btn_leave);
    btn_stay.setOnClickListener (new View.OnClickListener ( ) {
        @Override
        public void onClick(View v) {
            dialog.dismiss ();

        }
    });

    btn_leave.setOnClickListener (new View.OnClickListener ( ) {
        @Override
        public void onClick(View v) {
            dialog.dismiss ();
            SharedPreferences preferences = getContext().getSharedPreferences("UserSession", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("IsLoggedIn", false);
            editor.apply();
            Intent intent = new Intent(getContext(), MainActivity.class);
            startActivity(intent);
        }
    });
    dialog.setCanceledOnTouchOutside (false);
    dialog.show ();
}

//    private void fetchConfigData() {
//        Call<ConfigModel> call = apiInterface.getIndexApi();
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

//    private void fetchConfigDataFaceBook() {
//        Call<ConfigModel> call = apiInterface.getIndexApi();
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
//    private void fetchConfigDataTwitter() {
//        Call<ConfigModel> call = apiInterface.getIndexApi();
//        call.enqueue(new Callback<ConfigModel>() {
//            @Override
//            public void onResponse(Call<ConfigModel> call, Response<ConfigModel> response) {
//                if (response.isSuccessful() && response.body() != null) {
//                    ConfigModel configModel = response.body();
//                    if (configModel.getData() != null) {
//                        twitterLink= configModel.getData().getTwitter_link();
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

    private void fetchConfigDataFaceBook() {
        String url = GET_CONFIG;

        // Create a JsonObjectRequest for GET method
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
        String url =GET_CONFIG;
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
            // Handle the case where the link is not available
        }
    }
    private void callFaceBookLink() {
        if (facebookUrl != null && !facebookUrl.isEmpty()) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(facebookUrl));
            startActivity(intent);
        } else {
            // Handle the case where the link is not available
        }
    }

    private void callTwitterLink() {
        if (twitterLink != null && !twitterLink.isEmpty()) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(twitterLink));
            startActivity(intent);
        } else {
            // Handle the case where the link is not available
        }
    }

//    private void fetchProfileDetails() {
//        profileList.clear();
//        JsonObject postData = new JsonObject();
//        SharedPreferences sharedPreferences = getContext().getSharedPreferences("UserSession", MODE_PRIVATE);
//        String authId = sharedPreferences.getString("userId", "Default Id");
//        postData.addProperty("user_id", authId);
//
//        Call<ProfileModel> call = apiInterface.getProfileApi(postData);
//        call.enqueue(new Callback<ProfileModel>() {
//            @Override
//            public void onResponse(@NonNull Call<ProfileModel> call, @NonNull Response<ProfileModel> response) {
//                if (response.isSuccessful() && response.body() != null) {
//                        profileList.add(response.body().getData());
//                    updateUI(profileList);
//                }
//            }
//
//            @Override
//            public void onFailure(@NonNull Call<ProfileModel> call, @NonNull Throwable t) {
//                // Handle failure
//            }
//        });
//    }
private void fetchProfileDetails() {
    profileList.clear();

    SharedPreferences sharedPreferences = getContext().getSharedPreferences("UserSession", Context.MODE_PRIVATE);
    String authId = sharedPreferences.getString("userId", "Default Id");

    String url = GET_PROFILE;
    JSONObject postData = new JSONObject();
    try {
        postData.put("user_id", authId);
    } catch (JSONException e) {
        e.printStackTrace();
        Toast.makeText(getContext(), "Error creating request data", Toast.LENGTH_SHORT).show();
        return;
    }

    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, postData,
            response -> {
                try {
                    Gson gson = new Gson();
                    ProfileModel profileModel = gson.fromJson(response.toString(), ProfileModel.class);

                    if (profileModel != null && profileModel.getData() != null) {
                        profileList.add(profileModel.getData());
                        updateUI(profileList);
                    }
                } catch (Exception e) {
                    Log.e("fetchProfileDetails", "Error parsing JSON response", e);
                    Toast.makeText(getContext(), "Error parsing data", Toast.LENGTH_SHORT).show();
                }
            },
            error -> {
                Log.e("fetchProfileDetails", "API call failed: " + error.toString());
                Toast.makeText(getContext(), "An error occurred: " + error.toString(), Toast.LENGTH_SHORT).show();
            }
    );

    Volley.newRequestQueue(getContext()).add(jsonObjectRequest);
}

    private void updateProfile(String imageUri) {
        updatedProfileList.clear();

        SharedPreferences sharedPreferences = getContext().getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        String authId = sharedPreferences.getString("userId", "Default Id");

        String url = UPDATE_PROFILE;
        JSONObject postData = new JSONObject();
        try {
            postData.put("update_profile", "1");
            postData.put("user_id", authId);
            postData.put("profile", imageUri);
            postData.put("name", username.getText().toString());
            postData.put("email", email.getText().toString());
            postData.put("mobile", mobile_number.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error creating request data", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a JsonObjectRequest
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, postData,
                response -> {
                    try {
                        // Parse the response
                        Gson gson = new Gson();
                        UpdateProfileModel updateProfileModel = gson.fromJson(response.toString(), UpdateProfileModel.class);
                        String message = updateProfileModel.getMessage();
                        if (updateProfileModel.isResponse()) {
                            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                            fetchProfileDetails();
                        } else {
                            Toast.makeText(getContext(), "Failed to update profile: " + message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Log.e("updateProfile", "Error parsing JSON response", e);
                        Toast.makeText(getContext(), "Error parsing data", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e("updateProfile", "API call failed: " + error.toString());
                    Toast.makeText(getContext(), "An error occurred: " + error.toString(), Toast.LENGTH_SHORT).show();
                }
        );

        // Adding the request to the queue
        Volley.newRequestQueue(getContext()).add(jsonObjectRequest);
    }

    @SuppressLint("SetTextI18n")
    private void updateUI(ArrayList<ProfileModel.Data> profile) {
        String imageUrl = BASE_URL_IMAGE + profile.get(0).getProfile();
        Picasso.get()
                .load(imageUrl)
                .placeholder(R.drawable.profile)
                .error(R.drawable.profile)
                .into(iv_cir);
        tv_name.setText(profile.get(0).getName());
        tv_user_mobile.setText(profile.get(0).getMobile());
        tv_total_played.setText(profile.get(0).getTotal_played_game());
        tv_total_spent.setText("Rs."+profile.get(0).getTotal_amount_spend());
        tv_total_earned.setText("Rs."+profile.get(0).getTotal_amount_earned());
        username.setText(profile.get(0).getName());
        mobile_number.setText(profile.get(0).getMobile());
        email.setText(profile.get(0).getEmail());
        age.setText(profile.get(0).getAge());
        state.setText(profile.get(0).getState());
        district.setText(profile.get(0).getDistrict());
        address.setText(profile.get(0).getAddress());
        et_refer.setText(profile.get(0).getRefer_code());
    }


    private void initview(View view) {
        layout_basic = view.findViewById(R.id.layout_basic);
        layout_basic_edit = view.findViewById(R.id.layout_basic_edit);
        img_down_basic = view.findViewById(R.id.img_down_basic);
        layout_social = view.findViewById(R.id.layout_social);
        layout_social_media = view.findViewById(R.id.layout_social_media);
        img_down_social = view.findViewById(R.id.img_down_social);
        layout_refer = view.findViewById(R.id.layout_refer);
        layout_basic_refer = view.findViewById(R.id.layout_basic_refer);
        img_down_refer = view.findViewById(R.id.img_down_refer);
        editProfileImg = view.findViewById(R.id.edit_profile_img);
        iv_cir = view.findViewById(R.id.iv_cir);
        tv_name = view.findViewById(R.id.tv_name);
        tv_user_mobile = view.findViewById(R.id.tv_user_mobile);
        tv_total_played = view.findViewById(R.id.tv_total_played);
        tv_total_spent = view.findViewById(R.id.tv_total_spent);
        tv_total_earned = view.findViewById(R.id.tv_total_earned);
        username = view.findViewById(R.id.username);
        mobile_number = view.findViewById(R.id.mobile_number);
        email = view.findViewById(R.id.email);
        age = view.findViewById(R.id.age);
        state = view.findViewById(R.id.state);
        district = view.findViewById(R.id.district);
        address = view.findViewById(R.id.address);
        et_facebook = view.findViewById(R.id.et_facebook);
        whatsaap = view.findViewById(R.id.whatsaap);
        et_refer = view.findViewById(R.id.et_refer);
        btn_submit_basic = view.findViewById(R.id.btn_submit_basic);
        layout_bank = view.findViewById(R.id.layout_bank);
        instagram_icon = view.findViewById(R.id.instagram_icon);
        facebook_icon = view.findViewById(R.id.facebook_icon);
        twitter_icon = view.findViewById(R.id.twitter_icon);
        tv_logout = view.findViewById(R.id.tv_logout);
    }
}