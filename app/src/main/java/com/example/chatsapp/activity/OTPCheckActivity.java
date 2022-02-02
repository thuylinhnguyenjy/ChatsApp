package com.example.chatsapp.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import com.example.chatsapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.widget.Toast;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.chatsapp.databinding.ActivityOtpcheckBinding;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.mukesh.OnOtpCompletionListener;

import java.util.concurrent.TimeUnit;

public class OTPCheckActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityOtpcheckBinding binding;
    String verifyId;
    ProgressDialog loadingOTP;
    
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityOtpcheckBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loadingOTP = new ProgressDialog(this);
        loadingOTP.setMessage("Loading OTP...");
        loadingOTP.setCancelable(false);
        loadingOTP.show();

        //get value of intent
        String phoneNumber = getIntent().getStringExtra("phoneNumber");
        binding.txtvPhone.setText("Verify " + phoneNumber);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(phoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                    @Override
                    public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                        // Sign in with the credential
                        // ...
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {

                    }

                    @Override
                    public void onCodeSent(String verificationId,
                                           PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        // Save the verification id somewhere
                        // ...
                        // The corresponding whitelisted code above should be used to complete sign-in.
                        //OTPCheckActivity.this.enableUserManuallyInputCode();
                        super.onCodeSent(verificationId, forceResendingToken);
                        loadingOTP.dismiss();

                        verifyId = verificationId;
                    }

                })
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);

        binding.otpView.setOtpCompletionListener(new OnOtpCompletionListener() {
            @Override
            public void onOtpCompleted(String otp) {
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verifyId, otp);
                auth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(OTPCheckActivity.this, "Successfully", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(OTPCheckActivity.this, SetupProfileActivity.class);
                            startActivity(intent);
                            finishAffinity();
                        }
                        else
                            Toast.makeText(OTPCheckActivity.this, "Login failed", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });


//        setSupportActionBar(binding.toolbar);
//
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_otpcheck);
//        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
//        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

    }

    private void enableUserManuallyInputCode() {

    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_otpcheck);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }
}