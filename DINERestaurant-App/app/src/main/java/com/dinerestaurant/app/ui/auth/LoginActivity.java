package com.dinerestaurant.app.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.dinerestaurant.app.R;
import com.dinerestaurant.app.data.local.StaticData;
import com.dinerestaurant.app.data.local.TokenManager;
import com.dinerestaurant.app.data.remote.api.ApiClient;
import com.dinerestaurant.app.data.repository.AuthRepository;
import com.dinerestaurant.app.model.LoginRequest;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.dinerestaurant.app.ui.MainActivity;
import com.google.android.gms.auth.api.signin.*;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;


public class LoginActivity extends AppCompatActivity {

    EditText edtPhone;
    Button btnSignIn;
    ImageView btnGoogleLogin;
    GoogleSignInClient googleSignInClient;
    private static final int RC_GOOGLE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        setContentView(R.layout.activity_login);

        btnGoogleLogin = findViewById(R.id.btnGoogleLogin);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(
                GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.google_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);

        edtPhone = findViewById(R.id.edtPhone);
        btnSignIn = findViewById(R.id.btnSignIn);

        // Mặc định: xám, không click được
        btnSignIn.setEnabled(false);
        btnSignIn.setBackgroundResource(R.drawable.bg_btn_signin);
        btnSignIn.setAlpha(1f);

        // Lắng nghe nhập số điện thoại
        edtPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String phone = s.toString().trim();
                // Kiểm tra 9 số (do đã có +84)
                boolean valid = phone.length() == 9;

                if (valid) {
                    btnSignIn.setEnabled(true);
                    btnSignIn.setAlpha(1f);
                    btnSignIn.setBackgroundResource(R.drawable.gb_btn_enable); // màu cam đậm
                } else {
                    btnSignIn.setEnabled(false);
                    btnSignIn.setAlpha(1f);
                    btnSignIn.setBackgroundResource(R.drawable.bg_btn_signin); // màu xám
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Click Sign In
        btnSignIn.setOnClickListener(v -> {
            String phone = "84"+edtPhone.getText().toString().trim();

            if (phone.isEmpty()) {
                Toast.makeText(this, "Please enter phone number!", Toast.LENGTH_SHORT).show();
                return;
            }

            // request gửi OTP
            LoginRequest request = new LoginRequest(phone);

            new AuthRepository().loginRequestOtp(request)
                    .enqueue(new Callback<Map<String, Object>>() {
                        @Override
                        public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {

                            if (!response.isSuccessful() || response.body() == null) {
                                showError("Server error!");
                                return;
                            }

                            Map<String, Object> body = response.body();

                            if (body.containsKey("error")) {
                                showError(body.get("error").toString());
                                return;
                            }

                            // Thành công -> sang OTP
                            Intent intent = new Intent(LoginActivity.this, VerificationActivity.class);
                            intent.putExtra("phoneNumber", phone);
                            startActivity(intent);
                        }

                        @Override
                        public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                            showError("Network error: " + t.getMessage());
                        }
                    });

        });

        findViewById(R.id.tvRegister2).setOnClickListener(v ->
                startActivity(new Intent(this, SignUpActivity.class))
        );
        btnGoogleLogin.setOnClickListener(v -> {
            Intent signInIntent = googleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_GOOGLE);
        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_GOOGLE) {
            Task<GoogleSignInAccount> task =
                    GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                String idToken = account.getIdToken();

                loginGoogleToBackend(idToken);

            } catch (ApiException e) {
                Toast.makeText(this, "Google login failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showError(String message) {
        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
    }
    private void loginGoogleToBackend(String idToken) {

        Map<String, String> body = Map.of("idToken", idToken);

        new AuthRepository().loginGoogle(body)
                .enqueue(new Callback<Map<String, Object>>() {
                    @Override
                    public void onResponse(Call<Map<String, Object>> call,
                                           Response<Map<String, Object>> response) {

                        if (!response.isSuccessful() || response.body() == null) {
                            showError("Login failed");
                            return;
                        }

                        Map<String, Object> res = response.body();

                        if (res.containsKey("token") && res.get("token") != null) {

                            String token = String.valueOf(res.get("token"));

                            // Lưu token
                            new TokenManager(LoginActivity.this).saveToken(token);

                            // Chuyển sang Main
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                            return;
                        }

                        // CASE 2: chưa có SĐT
                        else if (Boolean.TRUE.equals(res.get("requirePhoneNumber"))) {

                            Intent i = new Intent(LoginActivity.this, SignUpActivity.class);

                            i.putExtra("fromGoogle", true);
                            i.putExtra("email", res.get("email").toString());
                            i.putExtra("fullName", res.get("fullName").toString());
                            i.putExtra("picture", res.get("profilePicture").toString());

                            startActivity(i);
                        }

                    }

                    @Override
                    public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                        showError("Network error");
                    }
                });
    }

}