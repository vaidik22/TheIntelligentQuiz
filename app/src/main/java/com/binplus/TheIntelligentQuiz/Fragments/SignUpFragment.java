package com.binplus.TheIntelligentQuiz.Fragments;

import static android.content.Context.MODE_PRIVATE;

import static com.binplus.TheIntelligentQuiz.BaseURL.BaseURL.SIGN_UP;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.binplus.TheIntelligentQuiz.Activity.HomeActivity;
import com.binplus.TheIntelligentQuiz.R;
import com.binplus.TheIntelligentQuiz.bottomsheetOtp.OtpVerificationBottomSheetDialog;
import com.binplus.TheIntelligentQuiz.common.Common;

import org.json.JSONException;
import org.json.JSONObject;



public class SignUpFragment extends Fragment {
    TextView btnGetOtp_mobileNumber;
    TextView btnGetOtp_emailId;
    EditText etMobileNumber;
    EditText etEmail;
    EditText etName;
    TextView textinput_error;
    int delay = 2000;
    ImageView ivBack;
    Button btnSignUp;
    Common common;
    Button btnExistingMember;
    private OtpVerificationBottomSheetDialog otpDialog;

    private boolean isMobileOtpVerified = false;
    private boolean isEmailOtpVerified = false;
    private String mobileOtp;
    private String emailOtp;

    public SignUpFragment() {

    }

    public static SignUpFragment newInstance(String param1, String param2) {
        SignUpFragment fragment = new SignUpFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);
        initView(view);
        common = new Common((AppCompatActivity) getActivity());
        allClick();
        return view;
    }

    private void initView(View view) {
        btnGetOtp_mobileNumber = view.findViewById(R.id.btnGetOtp_mobileNumber);
        btnGetOtp_emailId = view.findViewById(R.id.btnGetOtp_emailId);
        etMobileNumber = view.findViewById(R.id.etMobileNumber);
        etEmail = view.findViewById(R.id.etEmail);
        etName = view.findViewById(R.id.etName);
        textinput_error = view.findViewById(R.id.textinput_error);
        ivBack = view.findViewById(R.id.ivBack);
        btnSignUp = view.findViewById(R.id.btnSignUp);
        btnExistingMember = view.findViewById(R.id.btnExistingMember);
    }

    private void allClick() {
        btnGetOtp_mobileNumber.setOnClickListener(v -> {
            String mobileNumber = etMobileNumber.getText().toString().trim();
            if (!isValidMobileNumber(mobileNumber)) {
                showError(R.string.please_fill_valid_mobile_number);
            } else {
                callSignUpApiMobileVolley();
            }
        });

        btnGetOtp_emailId.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            if (email.isEmpty()) {
                showError(R.string.please_enter_your_email);
            } else if (!isValidEmail(email)) {
                showError(R.string.enter_valid_email);
            } else {
                callSignUpApiEmailVolley();
            }
        });

        btnSignUp.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String mobileNumber = etMobileNumber.getText().toString().trim();
            String email = etEmail.getText().toString().trim();

            if (name.isEmpty()) {
                showError(R.string.name_required_);
            } else if (!isValidMobileNumber(mobileNumber)) {
                showError(R.string.please_provide_mobile_number);
            } else if (email.isEmpty()) {
                showError(R.string.please_provide_email);
            } else if (!isValidEmail(email)) {
                showError(R.string.enter_valid_email);
            } else if (!isMobileOtpVerified) {
                showError(R.string.please_verify_mobile_otp);
            } else if (!isEmailOtpVerified) {
                showError(R.string.please_verify_email_otp);
            } else {
                callSignUpApiButtonVolley();
                SharedPreferences preferences = getActivity().getSharedPreferences("UserSession", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("IsLoggedIn", true);
                editor.apply();
            }
        });

        btnExistingMember.setOnClickListener(v -> {
            Fragment fragment = new LoginFragment();
            common.switchFragment(fragment);
        });

        ivBack.setOnClickListener(v -> getActivity().onBackPressed());
    }

//    private void callSignUpApiMobile() {
//        String mobileNumber = etMobileNumber.getText().toString().trim();
//        JsonObject object = new JsonObject();
//        object.addProperty("token", Constants.USER_ID);
//        object.addProperty("imei", Constants.IMEI_ID);
//        object.addProperty("mobile", mobileNumber);
//        object.addProperty("key", 1);
//
//        Call<SignUpModel> call = apiInterface.getSignUpApi(object);
//        call.enqueue(new Callback<SignUpModel>() {
//            @Override
//            public void onResponse(Call<SignUpModel> call, Response<SignUpModel> response) {
//                if (response.isSuccessful()) {
//                    SignUpModel resp = response.body();
//                    if (resp != null) {
//                        if (resp.isResponse()) {
//                            showErrorgreen(R.string.otp_sent_successfully);
//
//                            showOtpDialog(true);
//                            otpDialog.setOtp(resp.getOtp(), true);
//
//                        } else {
//                            showError(R.string.mobile_number_already_registered);
//                        }
//                    } else {
//                        Toast.makeText(getContext(), "Response body is null", Toast.LENGTH_SHORT).show();
//                        Log.e("API Response", "Response body is null");
//                    }
//                } else {
//                    Toast.makeText(getContext(), "Response not successful", Toast.LENGTH_SHORT).show();
//                    try {
//                        String errorBody = response.errorBody().string();
//                        Log.e("API Error", errorBody);
//                    } catch (Exception e) {
//                        Log.e("API Error", "Error parsing error body", e);
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(Call<SignUpModel> call, Throwable t) {
//                Log.e("onFailure", "Yes", t);
//            }
//        });
//    }

    private void callSignUpApiMobileVolley() {
        String mobileNumber = etMobileNumber.getText().toString().trim();

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("token", Constants.USER_ID);
            jsonObject.put("imei", Constants.IMEI_ID);
            jsonObject.put("mobile", mobileNumber);
            jsonObject.put("key", 1);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = SIGN_UP;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                response -> {
                    try {
                        if (response.getBoolean("response")) {
                            showErrorgreen(R.string.otp_sent_successfully);
                            showOtpDialog(true);
                            otpDialog.setOtp(Integer.parseInt(response.getString("otp")), true);
                        } else {
                            showError(R.string.mobile_number_already_registered);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    Toast.makeText(getContext(), "Response not successful", Toast.LENGTH_SHORT).show();
                    Log.e("API Error", error.toString());
                }
        );

        Volley.newRequestQueue(getContext()).add(jsonObjectRequest);
    }


//    private void callSignUpApiEmail() {
//        String email = etEmail.getText().toString().trim();
//        JsonObject object = new JsonObject();
//        object.addProperty("token", Constants.USER_ID);
//        object.addProperty("imei", Constants.IMEI_ID);
//        object.addProperty("email", email);
//        object.addProperty("key", 2);
//
//        Call<SignUpModel> call = apiInterface.getSignUpApi(object);
//        call.enqueue(new Callback<SignUpModel>() {
//            @Override
//            public void onResponse(Call<SignUpModel> call, Response<SignUpModel> response) {
//                if (response.isSuccessful()) {
//                    SignUpModel resp = response.body();
//                    if (resp != null) {
//                        if (resp.isResponse()) {
//                            showErrorgreen(R.string.otp_sent_successfully);
//
//
//                                showOtpDialog(false);
//                                otpDialog.setOtp(resp.getOtp(), false);
//
//                        } else {
//                            showError(R.string.email_already_registered);
//                        }
//                    }  else {
//                        Toast.makeText(getContext(), "Response body is null", Toast.LENGTH_SHORT).show();
//                        Log.e("API Response", "Response body is null");
//                    }
//                } else {
//                    Toast.makeText(getContext(), "Response not successful", Toast.LENGTH_SHORT).show();
//                    try {
//                        String errorBody = response.errorBody().string();
//                        Log.e("API Error", errorBody);
//                    } catch (Exception e) {
//                        Log.e("API Error", "Error parsing error body", e);
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(Call<SignUpModel> call, Throwable t) {
//                Log.e("onFailure", "Yes", t);
//            }
//        });
//    }
private void callSignUpApiEmailVolley() {
    String email = etEmail.getText().toString().trim();

    JSONObject jsonObject = new JSONObject();
    try {
        jsonObject.put("token", Constants.USER_ID);
        jsonObject.put("imei", Constants.IMEI_ID);
        jsonObject.put("email", email);
        jsonObject.put("key", 2);
    } catch (JSONException e) {
        e.printStackTrace();
    }

    String url = SIGN_UP;

    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
            response -> {
                try {
                    if (response.getBoolean("response")) {
                        showErrorgreen(R.string.otp_sent_successfully);
                        showOtpDialog(false);
                        otpDialog.setOtp(Integer.parseInt(response.getString("otp")), false);
                    } else {
                        showError(R.string.email_already_registered);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            },
            error -> {
                Toast.makeText(getContext(), "Response not successful", Toast.LENGTH_SHORT).show();
                Log.e("API Error", error.toString());
            }
    );

    Volley.newRequestQueue(getContext()).add(jsonObjectRequest);
}


//    private void callSignUpApiButton() {
//        String email = etEmail.getText().toString().trim();
//        String name = etName.getText().toString().trim();
//        String mobileNumber = etMobileNumber.getText().toString().trim();
//        SharedPreferences sharedPreferences = getContext().getSharedPreferences("UserSession", Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.putString("userName", name);
//        editor.putString("userMobile", mobileNumber);
//        editor.apply();
//        JsonObject object = new JsonObject();
//        object.addProperty("name", name);
//        object.addProperty("mobile", mobileNumber);
//        object.addProperty("email", email);
//        object.addProperty("mobile_otp", mobileOtp);
//        object.addProperty("email_otp", emailOtp);
//        String refer_code = "GMQ51123";
//        object.addProperty("refer_code", refer_code);
//        object.addProperty("key", 3);
//
//        Call<SignUpModel> call = apiInterface.getSignUpApi(object);
//        call.enqueue(new Callback<SignUpModel>() {
//            @Override
//            public void onResponse(Call<SignUpModel> call, Response<SignUpModel> response) {
//                if (response.isSuccessful()) {
//                    SignUpModel resp = response.body();
//                    if (resp != null) {
//                        if (resp.isResponse()) {
//                            showErrorgreen(R.string.sign_up_successfully);
//                            navigateToHome();
//                        } else {
//                            Toast.makeText(getContext(), error_in_sign_up, Toast.LENGTH_SHORT).show();
//                        }
//                    } else {
//                        Toast.makeText(getContext(), response_body_is_null, Toast.LENGTH_SHORT).show();
//                        Log.e("API Response", "Response body is null");
//                    }
//                } else {
//                    Toast.makeText(getContext(), response_not_successful, Toast.LENGTH_SHORT).show();
//                    try {
//                        String errorBody = response.errorBody().string();
//                        Log.e("API Error", errorBody);
//                    } catch (Exception e) {
//                        Log.e("API Error", "Error parsing error body", e);
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(Call<SignUpModel> call, Throwable t) {
//                Log.e("onFailure", "Yes", t);
//            }
//        });
//    }

    private void callSignUpApiButtonVolley() {
        String email = etEmail.getText().toString().trim();
        String name = etName.getText().toString().trim();
        String mobileNumber = etMobileNumber.getText().toString().trim();

        SharedPreferences sharedPreferences = getContext().getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("userName", name);
        editor.putString("userMobile", mobileNumber);
        editor.apply();

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("name", name);
            jsonObject.put("mobile", mobileNumber);
            jsonObject.put("email", email);
            jsonObject.put("mobile_otp", mobileOtp);
            jsonObject.put("email_otp", emailOtp);
            String refer_code = "GMQ51123";
            jsonObject.put("refer_code", refer_code);
            jsonObject.put("key", 3);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = SIGN_UP;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                response -> {
                    try {
                        if (response.getBoolean("response")) {
                            showErrorgreen(R.string.sign_up_successfully);
                            navigateToHome();
                        } else {
                            Toast.makeText(getContext(), R.string.error_in_sign_up, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    Toast.makeText(getContext(), R.string.response_not_successful, Toast.LENGTH_SHORT).show();
                    Log.e("API Error", error.toString());
                }
        );

        Volley.newRequestQueue(getContext()).add(jsonObjectRequest);
    }


    private boolean isValidMobileNumber(String mobileNumber) {
        return mobileNumber.length() == 10 && mobileNumber.charAt(0) >= '6' && mobileNumber.charAt(0) <= '9';
    }

    private boolean isValidEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void showOtpDialog(boolean isMobile) {
        otpDialog = new OtpVerificationBottomSheetDialog();
        otpDialog.setOtpVerificationListener((isMobileOtp, otp) -> {
            if (isMobileOtp) {
                mobileOtp = otp;
                isMobileOtpVerified = true;
            } else {
                emailOtp = otp;
                isEmailOtpVerified = true;
            }
            showErrorgreen(R.string.otp_verified_successfully);
        });
        otpDialog.setResendOtpListener(isMobile ? this::callSignUpApiMobileVolley : this::callSignUpApiEmailVolley);
        otpDialog.show(getChildFragmentManager(), "OtpVerificationBottomSheetDialog");
    }

    private void showError(int resId) {
        textinput_error.setText(resId);
        textinput_error.setVisibility(View.VISIBLE);
        textinput_error.setBackgroundColor(Color.RED);
        new Handler().postDelayed(() -> textinput_error.setVisibility(View.GONE), delay);
    }
    private void showErrorgreen(int resId) {
        textinput_error.setText(resId);
        textinput_error.setVisibility(View.VISIBLE);
        textinput_error.setBackgroundColor(Color.parseColor("#228B22"));
        new Handler().postDelayed(() -> textinput_error.setVisibility(View.GONE), delay);
    }

    private void navigateToHome() {
        Intent intent = new Intent(getActivity(), HomeActivity.class);
        startActivity(intent);
        getActivity().finish();
    }
}
